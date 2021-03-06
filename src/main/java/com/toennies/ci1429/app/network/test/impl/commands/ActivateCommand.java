package com.toennies.ci1429.app.network.test.impl.commands;

import com.toennies.ci1429.app.network.test.IScriptCommand;
import com.toennies.ci1429.app.network.test.IScriptDevice;
import com.toennies.ci1429.app.network.test.Token;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ActivateCommand implements IScriptCommand
{
	private final static Logger logger = LogManager.getLogger();
	private IScriptDevice device;
	private Token token;

	public ActivateCommand(IScriptDevice device, Token token)
	{
		this.device = device;
		this.token = token;
	}

	@Override
	public void execute()
	{
		logger.info("Test Device: ACTIVATE command is called");
		device.activate();
	}
}
