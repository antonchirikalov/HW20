package com.toennies.ci1429.app.network.test;

import com.toennies.ci1429.app.network.connector.ADataTransformer;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.socket.AtSocket;
import com.toennies.ci1429.app.network.socket.ISocket;
import com.toennies.ci1429.app.util.ASCII;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Component
@AtSocket("Test device Socket")
@AtDefaultParameters.Parameter(name = ScriptDevice.PARAM_TEST_SCRIPT, isRequired = true, toolTip = "URI of test script file. Required")
public class TestSocket implements ISocket
{

	private final static Logger logger = LogManager.getLogger();
 	private boolean isFirstRequest = true;
 	private int timeout = 0;
 	private IScriptDevice scriptDevice;

	private IConfigContainer config;

	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		if (this.isConnected()) {
			this.isFirstRequest = true;
			return;
		}
		this.config = config;
		this.timeout = config.getIntEntry(ISocket.PARAM_TIMEOUT);
		this.scriptDevice = new ScriptDevice(config);
		this.scriptDevice.runScript();

	}

	@Override
	public boolean isConnected()
	{
		return this.scriptDevice!= null && this.scriptDevice.isActivated();
	}

	@Override
	public byte[] poll() throws IOException
	{
		if (!this.isConnected())
			throw new IllegalStateException("Test Device can't answer. It is deactivated.");

		//return null until the first request comes
		//some connectors are polling before performing any requests to device
		if (isFirstRequest) {
			logger.warn("no data to response. No request yet came to device");
			return null;
		}

		try {
			//wait until script runner works with SEND operand
            byte[] response = this.scriptDevice.takeResponse(timeout);

            if (response.length == 0) {
                logger.debug("got from Device: NULL");
                return null;
            }

			//add empty response to responseQueue to signalize that device finished to answer
			// it happens only in three cases
			if ( ASCII.formatHuman(response).equals(ASCII.ACK.toString()) ||
				 ASCII.formatHuman(response).equals(ASCII.ENQ.toString()) ||
				 ASCII.formatHuman(response).contains(config.getEntry(ADataTransformer.PARAM_FRAME_END)) ) {
				this.scriptDevice.addResponse(new byte[]{});

			}

			logger.debug("got from Device: "+ ASCII.formatHuman(response));
			return response;

		} catch (TimeoutException e) {
			throw new IOException("Timeout exception. Test Device is not responding in timeout.");
		}
	}

	@Override
	public byte[] pop() throws IOException, TimeoutException
	{
		return poll();
	}

	@Override
	public void push(byte[] entity) throws IOException
	{
		if (!this.scriptDevice.isActivated())
			throw new IllegalStateException("Couldn't send request to Test Device. It is deactivated.");

		this.isFirstRequest = false;
		this.scriptDevice.addRequest(entity);
	}

	@Override
	public void disconnect()
	{
		this.isFirstRequest = true;
		this.scriptDevice.deactivate();
	}

	@Override
	public void shutdown()
	{
		this.disconnect();
	}


}
