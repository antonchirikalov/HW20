package com.toennies.ci1429.app.network.test;

import com.toennies.ci1429.app.network.test.impl.ScriptParser;
import com.toennies.ci1429.app.util.ASCII;
import org.junit.Test;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;


public class ScriptParserTest {

    private File scriptFile;

    private ScriptParser scriptParser = new ScriptParser();



    @Test
    public void testParseScriptFile() throws IOException, URISyntaxException
    {
        String fileName = "testscripts/testscript.script";
        URI fileURI = Thread.currentThread().getContextClassLoader().getResource(fileName).toURI();
        scriptFile = new File(fileURI);
        BufferedReader br = new BufferedReader(new FileReader(scriptFile));

        Script script = scriptParser.parse(Files.newInputStream(scriptFile.toPath()));
        assertTrue(script != null);
        assertEquals(br.lines().count(), script.getTokens().size());
        Token token = script.getTokens().poll();
        assertEquals(CommandType.WAIT, token.getCommandType());
        assertEquals("5000", token.getCommand());
        token = script.getTokens().poll();
        assertEquals(CommandType.SEND, token.getCommandType());
        assertEquals(ASCII.parseHuman("[ENQ]w5[ACK]"), token.getCommand());
    }

    @Test
    public void testBizerbaScriptFile() throws URISyntaxException, IOException
    {
        String fileName = "testscripts/bizerba/bizerba_item_adding.script";
        Script script = scriptParser.parse(getScriptAsInputStream(fileName));
        assertEquals(script.getTokens().size(), 19);
        Token token = script.getTokens().poll();
        assertEquals(token.getCommandType(), CommandType.RECEIVE);
        assertEquals(token.getCommand(), ASCII.parseHuman("[ACK]"));
        token = script.getTokens().poll();
        assertEquals(token.getCommandType(), CommandType.SEND);
        assertEquals(token.getCommand(), ASCII.parseHuman("kg[ETX]+!   0101,0kg[ETB]"));
        token = script.getTokens().poll();
        assertEquals(token.getCommandType(), CommandType.SEND);
        assertEquals(token.getCommand(), ASCII.parseHuman("01,0kg[ETX]   0000,0"));
        token = script.getTokens().poll();
        assertEquals(token.getCommandType(), CommandType.SEND);
        assertEquals(token.getCommand(), ASCII.parseHuman(",0kg[ETX]]1[ETX]17002172[ETX]+!   01"));


    }

    private InputStream getScriptAsInputStream(String fileName) throws URISyntaxException, IOException
    {
        URI fileURI = Thread.currentThread().getContextClassLoader().getResource(fileName).toURI();
        File scriptFile = new File(fileURI);
        return  Files.newInputStream(scriptFile.toPath());
    }



}
