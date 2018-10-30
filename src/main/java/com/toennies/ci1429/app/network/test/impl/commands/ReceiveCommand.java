package com.toennies.ci1429.app.network.test.impl.commands;

import com.toennies.ci1429.app.network.test.*;
import com.toennies.ci1429.app.util.ASCII;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;

public class ReceiveCommand implements IScriptCommand
{

	private final static Logger logger = LogManager.getLogger();
	private IScriptDevice device;
	private Token token;

	public ReceiveCommand(IScriptDevice device, Token token)
	{
		this.device = device;
		this.token = token;
	}

	@Override
	public void execute() throws IOException
	{
		byte[] cmd = new byte[0];

		try
		{
			//wait until request is pushed to socket
			cmd = device.takeRequest();
			logger.info("Test Device: RECEIVED MESSAGE " + ASCII.formatHuman(cmd));

		}
		catch (InterruptedException e)
		{
			throw new IOException("Test Device - waiting for request was interrupted");
		}

		if (!Arrays.equals(cmd, ASCII.parseHuman(token.getCommand()).getBytes()))
		{
			// we got wrong command so
			String message = "RECEIVE hasn't receive expected command. Expected: " + ASCII.formatHuman(token.getCommand().getBytes()) + ", recieved: " + ASCII.formatHuman(cmd);
			logger.error(message);
			throw new IOException(message);
		}

	}
}
