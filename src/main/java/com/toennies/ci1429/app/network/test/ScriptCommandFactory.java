package com.toennies.ci1429.app.network.test;

import com.toennies.ci1429.app.network.test.impl.commands.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScriptCommandFactory
{
	private static final Logger logger = LogManager.getLogger();
	private IScriptDevice device;

	public ScriptCommandFactory(IScriptDevice device)
	{
		this.device = device;
	}

	public IScriptCommand getScriptCommand(Token token)
	{

		logger.info(token);

		switch (token.getCommandType())
		{
			case ACTIVATE:
				return new ActivateCommand(device, token);
			case DEACTIVATE:
				return new DeactivateCommand(device, token);
			case SEND:
				return new SendCommand(device, token);
			case RECEIVE:
				return new ReceiveCommand(device, token);
			case WAIT:
				return new WaitCommand(token);
			default:
				return null;

		}
	}

}
