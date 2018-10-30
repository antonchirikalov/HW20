package com.toennies.ci1429.app.network.test.impl.commands;

import com.toennies.ci1429.app.network.test.IScriptCommand;
import com.toennies.ci1429.app.network.test.IScriptDevice;
import com.toennies.ci1429.app.network.test.Token;
import com.toennies.ci1429.app.util.ASCII;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class SendCommand implements IScriptCommand
{
	private final static Logger logger = LogManager.getLogger();
	private IScriptDevice device;
	private Token token;

	public SendCommand(IScriptDevice device, Token token)
	{
		this.device = device;
		this.token = token;
	}

	@Override
	public void execute() throws IOException
	{

		device.addResponse(ASCII.parseHuman(token.getCommand()).getBytes());

		logger.info("TestDevice: SEND MESSAGE " + ASCII.formatHuman(token.getCommand().getBytes()));
	}
}
