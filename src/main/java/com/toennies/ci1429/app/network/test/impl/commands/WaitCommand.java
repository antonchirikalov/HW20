package com.toennies.ci1429.app.network.test.impl.commands;

import com.toennies.ci1429.app.network.test.IScriptCommand;
import com.toennies.ci1429.app.network.test.Token;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class WaitCommand implements IScriptCommand
{
	private final static Logger logger = LogManager.getLogger();
	private Token token;

	public WaitCommand(Token token)
	{
		this.token = token;
	}

	@Override
	public void execute() throws IOException
	{

			try
			{
				logger.info("TestDevice: WAIT COMMAND " + token.getCommand());
				Thread.sleep(Long.parseLong(token.getCommand()));
			}
			catch (InterruptedException e)
			{
				logger.error(e.getMessage());
				throw new IOException(e);
			}


	}
}
