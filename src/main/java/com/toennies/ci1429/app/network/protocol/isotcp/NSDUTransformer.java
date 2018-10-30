/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.isotcp;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import com.toennies.ci1429.app.network.connector.IConnector;
import com.toennies.ci1429.app.network.connector.IConnectorWrapper;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Message;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.protocol.isotcp.tpdu.ConnectResponseTPDU;
import com.toennies.ci1429.app.network.protocol.isotcp.tpdu.DataTransferTPDU;
import com.toennies.ci1429.app.network.protocol.isotcp.tpdu.TPDU;
import com.toennies.ci1429.app.network.protocol.isotcp.tpdu.TPDU.Type;

/**
 * Transformer that takes user data and splits it up according the negotiated (with the server) parameters.
 * @author renkenh
 */
public class NSDUTransformer implements IConnector<IMessage>, IConnectorWrapper<IFlexibleConnector<IMessage, TPDU>, IMessage, TPDU>
{
	
	private static final int MAX_TPDU_SIZE = 65531;

	private final ArrayDeque<DataTransferTPDU> transferTPDUs = new ArrayDeque<>();
	private final ReentrantLock transferLock = new ReentrantLock();

	private final IFlexibleConnector<IMessage, TPDU> wrapped;
	private volatile int maxTPduSize = 16;


	/**
	 * Constructor.
	 */
	public NSDUTransformer(IFlexibleConnector<IMessage, TPDU> connector)
	{
		this.wrapped = connector;
	}


	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		this.transferTPDUs.clear();
		this.wrapped.connect(config);
	}


	@Override
	public boolean isConnected()
	{
		return this.wrapped.isConnected();
	}


	@Override
	public IMessage poll() throws IOException
	{
		TPDU tpdu = this.wrapped.poll();
		while (tpdu != null && tpdu.isValid() && tpdu.getType() == Type.DATA_TRANSFER)
		{
			IMessage msg = this.handleDT((DataTransferTPDU) tpdu);
			if (msg != null)
				return msg;
			tpdu = this.wrapped.poll();
		}
		this.checkConnectResponse(tpdu);
		return tpdu;
	}
	
	private IMessage handleDT(DataTransferTPDU dt)
	{
		this.transferLock.lock();
		try
		{
			this.transferTPDUs.add(dt);
			if (!dt.isEOF())
				return null;

			int nsduLength = this.transferTPDUs.parallelStream().mapToInt((t) -> t.getPayloadLength()).sum();
			byte[] nsdu = new byte[nsduLength];
			int position = 0;
			for (DataTransferTPDU t : this.transferTPDUs)
			{
				System.arraycopy(t.getPayload(), 0, nsdu, position, t.getPayloadLength());
				position += t.getPayloadLength();
			}
			this.transferTPDUs.clear();
			return new Message(nsdu);
		}
		finally
		{
			this.transferLock.unlock();
		}
	}

	@Override
	public IMessage pop() throws IOException, TimeoutException
	{
		TPDU tpdu = this.wrapped.pop();
		while (tpdu != null && tpdu.isValid() && tpdu.getType() == Type.DATA_TRANSFER)
		{
			IMessage msg = this.handleDT((DataTransferTPDU) tpdu);
			if (msg != null)
				return msg;
			tpdu = this.wrapped.pop();
		}
		this.checkConnectResponse(tpdu);
		return tpdu;
	}

	@Override
	public void push(IMessage entity) throws IOException
	{
		if (entity instanceof TPDU)
		{
			this.wrapped.push((TPDU) entity);
			return;
		}
		
		final byte[] payload = entity.words().get(0);
        final int maxTSDUSize = this.maxTPduSize - 3;
        if (payload.length == 0)	//do nothing if message empty
        	return;

        int position = 0;
        List<byte[]> splits = new ArrayList<>(payload.length / maxTSDUSize + 1);
        while (payload.length - position > 0)
        {
        	byte[] next = new byte[Math.min(payload.length - position, maxTSDUSize)];
        	System.arraycopy(payload, position, next, 0, next.length);
        	splits.add(next);
        	position += next.length;
        }
        
        for (int i = 0; i < splits.size() - 1; i++)
        	this.wrapped.push(new DataTransferTPDU((short) 0, splits.get(i)));	//class 0 - tpdu nr is always 0
        this.wrapped.push(new DataTransferTPDU(DataTransferTPDU.EOF, splits.get(splits.size()-1)));
	}

	@Override
	public void disconnect() throws IOException
	{
		this.transferTPDUs.clear();
		this.wrapped.disconnect();
	}

	@Override
	public void shutdown()
	{
		this.transferTPDUs.clear();
		this.wrapped.shutdown();
	}

	@Override
	public IFlexibleConnector<IMessage, TPDU> getWrappedConnector()
	{
		return this.wrapped;
	}


	private void checkConnectResponse(TPDU tpdu)
	{
		if (tpdu != null && tpdu.isValid() && tpdu.getType() == Type.CONNECT_RESPONSE)
		{
			ConnectResponseTPDU response = (ConnectResponseTPDU) tpdu;
			this.maxTPduSize = Math.min(MAX_TPDU_SIZE, (int) Math.pow(2, response.getMaxTpduSizeParam()));
		}
    }

}
