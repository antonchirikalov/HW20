package com.toennies.ci1429.app.network.test.impl;

import com.toennies.ci1429.app.network.test.IScriptParser;
import com.toennies.ci1429.app.network.test.IScriptReader;
import com.toennies.ci1429.app.network.test.Script;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;


public class URIScriptReader implements IScriptReader
{


	@Override
	public Script read(String scriptName) throws IOException
	{

		IScriptParser scriptParser = new ScriptParser();
		ClassLoader classLoader = getClass().getClassLoader();
		if (classLoader.getResource(scriptName) != null)
		{
			File file = new File(classLoader.getResource(scriptName).getFile());
			InputStream scriptInput = Files.newInputStream(file.toPath());
			return scriptParser.parse(scriptInput);
		}
		else throw new IOException("Script file not found");

	}


}
