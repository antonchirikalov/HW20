/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.bizerba;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.network.connector.IConnector;
import com.toennies.ci1429.app.network.connector.IConnectorWrapper;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Message;
import com.toennies.ci1429.app.network.message.Messages;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;
import com.toennies.ci1429.app.util.ASCII;



/**
 * Bizerba connector that handles protocol specific attributes.
 * @author renkenh
 */
class BizerbaSTConnector implements IConnector<IMessage>, IConnectorWrapper<IFlexibleConnector<IMessage, IExtendedMessage>, IMessage, IExtendedMessage>
{
	
	private static final Logger LOGGER = LogManager.getLogger();
	private static final IMessage ACK = new Message(ASCII.ACK.code);
	private static final IMessage NAK = new Message(ASCII.NAK.code);
	private static final IMessage ENQ = new Message(ASCII.ENQ.code);
	
	private final IFlexibleConnector<IMessage, IExtendedMessage> connector;
	private boolean sendENQ = false;


	public BizerbaSTConnector(IFlexibleConnector<IMessage, IExtendedMessage> connector)
	{
		this.connector = connector;
	}


	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		this.sendENQ = config.getBooleanEntry(BizerbaSTProtocol.PARAM_SEND_ENQ);
		this.connector.connect(config);
		IMessage poll = this.connector.poll();
		while (poll != null)
		{
			poll = this.connector.poll();
			LOGGER.info("Got possibly old message: {}", poll);
		}
	}

	@Override
	public IFlexibleConnector<IMessage, IExtendedMessage> getWrappedConnector()
	{
		return this.connector;
	}
	
	@Override
	public boolean isConnected()
	{
		return this.connector.isConnected();
	}

	@Override
	public void push(IMessage entity) throws IOException
	{
		try
		{
			this.sendENQ();
			this.sendDataAwaitACK(entity);
		}
		catch (TimeoutException e)
		{
			throw new IOException(e);
		}
	}

	
	@Override
	public IMessage poll() throws IOException
	{
		try
		{
			return this.pop();
		}
		catch (TimeoutException e)
		{
			throw new IOException(e);
		}
	}


	@Override
	public synchronized IMessage pop() throws IOException, TimeoutException
	{
		IMessage response = null;
		do
		{
			if (this.sendENQ)
			{
				IMessage next = this._pop();
				if (!Messages.isENQ(next))
				{
					this.connector.push(NAK);
					throw new IOException("Didn't get expected ENQ. Got: " + next);
				}
			}

			response = this._pop();
		}
		while (Responses.map2Result(response) == HardwareResponse.WAIT);
		return response;
	}

	private IMessage _pop() throws IOException, TimeoutException
	{
		IMessage msg = this.connector.pop();
		if (!Messages.isACK(msg) && !Messages.isNAK(msg))
			this.connector.push(ACK);
		return msg;
	}

	
	private void sendENQ() throws IOException, TimeoutException
	{
		if (this.sendENQ)
			this.sendDataAwaitACK(ENQ);
	}
	
	private void sendDataAwaitACK(IMessage msg) throws IOException, TimeoutException
	{
		int tries = 0;
		ASCII response = ASCII.NAK;
		while (response == ASCII.NAK && tries < 2)
		{
			this.connector.push(msg);
			response = this.awaitACKNAK();
			LOGGER.debug("Send {}", ASCII.formatHuman(msg.words().get(0)));
			LOGGER.debug("Received {}", response);
			tries++;
		}
		if (response == ASCII.NAK)
			throw new IOException("Could not correctly send data '"+ASCII.formatHuman(msg.words().get(0))+"' to scale.");
	}
	
	private ASCII awaitACKNAK() throws IOException, TimeoutException
	{
		IMessage next = this._pop();
		if (Messages.isACK(next))
			return ASCII.ACK;
		if (Messages.isNAK(next))
			return ASCII.NAK;
		throw new IOException("Expected ACK or NAK. Message has wrong content. " + String.valueOf(next));
	}


	@Override
	public void disconnect() throws IOException
	{
		this.connector.disconnect();
	}

	@Override
	public void shutdown()
	{
		this.connector.shutdown();
	}

}
