package com.toennies.ci1429.app.network.test.impl;

import com.toennies.ci1429.app.network.test.IScriptReader;
import com.toennies.ci1429.app.network.test.IScriptRunner;
import com.toennies.ci1429.app.network.test.Script;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;



public class ScriptRunner implements IScriptRunner
{


	Script script;

	private final static Logger logger = LogManager.getLogger();

	public ScriptRunner(String scriptName)
	{

		IScriptReader scriptReader = new URIScriptReader();
		try
		{
			script = scriptReader.read(scriptName);
		}
		catch (IOException e)
		{
			logger.error(e.getMessage());
		}

	}



	@Override
	public void push(byte[] cmd)
	{

	}

	@Override
	public byte[] pop()
	{
		return new byte[0];
	}
}
