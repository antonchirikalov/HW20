package com.toennies.ci1429.app.network.test;

import com.toennies.ci1429.app.network.test.impl.URIScriptReader;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;


public class URIScriptReaderTest
{


	private IScriptReader scriptReader;


	@Before
	public void setUp()
	{
		scriptReader = new URIScriptReader();
	}

	@Test
	public void testReadFileFromParam() throws IOException
	{
		Script script;
		assertTrue((script = scriptReader.read("scale/testscript.script")) != null);
		assertEquals(script.getTokens().size(), 11);
		Token token = script.getTokens().poll();
		assertEquals(token.getCommandType(), CommandType.WAIT);
		assertEquals(token.getCommand(), "5000");
		token = script.getTokens().poll();
		assertEquals(token.getCommandType(), CommandType.SEND);

	}

	@Test
	public void testReadFromSubdirectory() throws IOException
	{
		Script script;
		assertTrue((script = scriptReader.read("scale/testscript.script")) != null);
		assertEquals(script.getTokens().size(), 8);
		Token token = script.getTokens().poll();
		assertEquals(token.getCommandType(), CommandType.ACTIVATE);
		assertEquals(token.getCommand(), null);
		token = script.getTokens().poll();
		assertEquals(token.getCommandType(), CommandType.SEND);
		assertNotNull(token.getCommand());

	}

	@Test(expected = IOException.class)
	public void testReadAbsentFileFromURI() throws URISyntaxException, IOException
	{
		scriptReader.read("absentScript.txt");

	}


}
