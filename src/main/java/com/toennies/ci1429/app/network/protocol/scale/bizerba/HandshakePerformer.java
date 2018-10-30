/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.bizerba;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.network.connector.AWrappedConnector;
import com.toennies.ci1429.app.network.connector.ADataTransformer;
import com.toennies.ci1429.app.network.connector.IConnector;
import com.toennies.ci1429.app.network.event.EventNotifierStub;
import com.toennies.ci1429.app.network.event.IEventHandler;
import com.toennies.ci1429.app.network.event.IEventNotifier;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse.Status;
import com.toennies.ci1429.app.network.protocol.scale.bizerba.HeaderRequest.HeaderResponse;
import com.toennies.ci1429.app.util.ASCII;

/**
 * Performs the handshake for bizerba scales.
 * @author renkenh
 */
class HandshakePerformer extends AWrappedConnector<IMessage> implements IEventNotifier
{

	private static final DecimalFormat UNITNR_FORMATTER = new DecimalFormat("00");
	private static final Logger logger = LogManager.getLogger();

	
	private enum HandShake
	{
		NONE, IN_PROGRESS, FINISHED
	}



	private final EventNotifierStub stub = new EventNotifierStub(this);
	private volatile HandShake hsState = HandShake.NONE;
	private final BizerbaMSGTransformer transformer;
	

	/**
	 * @param transformer
	 * @param connector
	 */
	public HandshakePerformer(BizerbaMSGTransformer transformer, IConnector<IMessage> connector)
	{
		super(connector);
		this.transformer = transformer;
	}

	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		if (this.hsState != HandShake.FINISHED)
			this.setupTransformer1Time(config);
		super.connect(config);
		if (this.hsState != HandShake.FINISHED)
			this.performHandshake();
	}
	

	private void setupTransformer1Time(IConfigContainer config)
	{
		int scaleNr = config.getIntEntry(BizerbaSTProtocol.PARAM_SCALE_NUMBER);
		int unitNr = config.getIntEntry(BizerbaSTProtocol.PARAM_UNIT_NUMBER);
		String startControl = ASCII.parseHuman(config.getEntry(ADataTransformer.PARAM_FRAME_START));
		String sepControl = ASCII.parseHuman(config.getEntry(ADataTransformer.PARAM_FRAME_SEP));
		String endControl = ASCII.parseHuman(config.getEntry(ADataTransformer.PARAM_FRAME_END));
		
		String header = "00" + UNITNR_FORMATTER.format(unitNr) + scaleNr;
		if (header.length() != 5)
			throw new IllegalArgumentException("Header ["+header+"] does not comply to standard.");
		
		this.transformer.setup(startControl, header, sepControl, endControl);
	}
	

	private void performHandshake() throws IOException
	{
		this.setHandShake(HandShake.IN_PROGRESS);
		HeaderRequest request = new HeaderRequest();
		this.push(request.getRequestMessage());
		
		try
		{
			HardwareResponse result = null;
			do
			{
				result = request.handleResponse(this.pop());
			}
			while(result.getStatus() == Status.WAIT);
				
			if (result.getStatus() == Status.OK_DATA)
			{
				byte[] newHeader = ((HeaderResponse) result).getHeader();
				newHeader[0] = '0';	//override status bit with zeros
				newHeader[1] = '0';
				this.transformer.setupHeader(newHeader);
				this.setHandShake(HandShake.FINISHED);
				return;
			}
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
	public void shutdown()
	{
		try
		{
			super.shutdown();
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
