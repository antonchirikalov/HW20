/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.isotcp;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.network.connector.AWrappedConnector;
import com.toennies.ci1429.app.network.connector.IConnector;
import com.toennies.ci1429.app.network.event.EventNotifierStub;
import com.toennies.ci1429.app.network.event.IEventHandler;
import com.toennies.ci1429.app.network.event.IEventNotifier;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.protocol.isotcp.tpdu.ConnectResponseTPDU;
import com.toennies.ci1429.app.network.protocol.isotcp.tpdu.ConnectTPDU;
import com.toennies.ci1429.app.network.protocol.isotcp.tpdu.DisconnectTPDU;
import com.toennies.ci1429.app.network.protocol.isotcp.tpdu.TPDU;

/**
 * Performs the handshake for a iso on tcp connection.
 * @author renkenh
 */
public class ISOonTCPConnector extends AWrappedConnector<IMessage> implements IEventNotifier
{

	/** The parameter for the source id. */
	public static final String PARAM_SOURCEID = "sourceID";
	/** The parameter for the destination id. */
	public static final String PARAM_DESTID = "destID";


	private static final Logger logger = LogManager.getLogger();

	
	private enum HandShake
	{
		NONE, IN_PROGRESS, FINISHED
	}



	private final EventNotifierStub stub = new EventNotifierStub(this);
	private volatile HandShake hsState = HandShake.NONE;
	private String srcID = null;
	private String destID = null;
	private int srcRef = -1;
	private int destRef = -1;


	/**
	 * Constructor.
	 */
	public ISOonTCPConnector(IConnector<IMessage> connector)
	{
		super(connector);
	}

	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		this.hsState = HandShake.NONE;
		super.connect(config);
		this.srcID = config.getEntry(PARAM_SOURCEID);
		this.destID = config.getEntry(PARAM_DESTID);
		this.performHandshake();
	}
	


	private void performHandshake() throws IOException
	{
		this.setHandShake(HandShake.IN_PROGRESS);
		
		this.push(new ConnectTPDU(this.srcID, this.destID));
		
		try
		{
			IMessage msg = super.pop();
			if (!(msg instanceof ConnectResponseTPDU))
				throw new IOException("Could not connect.");
			
			ConnectResponseTPDU tpdu = (ConnectResponseTPDU) msg;
			tpdu.validate();
			this.srcRef = tpdu.getSrcRef();
			this.destRef = tpdu.getDestRef();
			this.setHandShake(HandShake.FINISHED);
		}
		catch (TimeoutException e)
		{
			logger.info("Handshake failed.", e);
			this.setHandShake(HandShake.NONE);
			throw new IOException(e);
		}
		catch (IOException e)
		{
			logger.info("Handshake failed.", e);
			this.setHandShake(HandShake.NONE);
			throw e;
		}
	}
	
	private void setHandShake(HandShake shake)
	{
		this.hsState = shake;
		this.stub.publishEvent(IDevice.EVENT_STATE_CHANGED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected()
	{
		return super.isConnected() && this.hsState != HandShake.NONE;
	}


	@Override
	public IMessage poll() throws IOException
	{
		return this.handleMSG(super.poll());
	}

	@Override
	public IMessage pop() throws IOException, TimeoutException
	{
		IMessage msg = this.handleMSG(super.pop());
		while (msg == null)
			msg = this.handleMSG(super.pop());
		return msg;
	}

	
	private final IMessage handleMSG(IMessage msg) throws IOException
	{
		if (msg instanceof DisconnectTPDU)
		{
			this.disconnect();
			throw new IOException("Pipeline disconnected on SPS-Request.");
		}
		if (!(msg instanceof TPDU))
			return msg;
		return null;
	}

	@Override
	public void disconnect() throws IOException
	{
		try
		{
			this.push(new DisconnectTPDU(this.srcRef, this.destRef, (short) 0));
			super.disconnect();
		}
		finally
		{
			this.hsState = HandShake.NONE;
		}
	}

	@Override
	public void registerEventHandler(IEventHandler handler)
	{
		this.stub.registerEventHandler(handler);
	}

	@Override
	public void unregisterEventHandler(IEventHandler handler)
	{
		this.stub.unregisterEventHandler(handler);
	}

}
