package com.toennies.ci1429.app.network.test;

import com.toennies.ci1429.app.network.test.impl.LogScriptParser;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;


public class LogScriptParserTest {

    private IScriptParser scriptParser;
    private List<String> linesToParse = Arrays.asList(
            "[In ][ ][[ACK]]",
            "[send ][ ][[SOH]00001[ETX]qY[ETB]]",
            "[OUT][ 8][,0kg[ETX]]1[ETX]]"
    );

    private Script script;

    @Before
    public void setUp() throws IOException
    {
        scriptParser = new LogScriptParser();
        File file = new File("testScript.txt");
        Files.write(file.toPath(), linesToParse);
        script = scriptParser.parse(Files.newInputStream(file.toPath()));
        file.delete();
    }

    @Test
    public void testParseLines()
    {
        assertTrue(script != null);

        assertEquals(linesToParse.size(), script.getTokens().size());
        assertEquals(CommandType.RECEIVE, script.getTokens().poll().getCommandType());
        assertEquals("[ACK]", script.getTokens().poll().getCommand());
        assertEquals(CommandType.RECEIVE, script.getTokens().poll().getCommandType());
        assertEquals("[SOH]00001[ETX]qY[ETB]", script.getTokens().poll().getCommand());
        assertEquals(CommandType.SEND, script.getTokens().poll().getCommandType());
        assertEquals(",0kg[ETX]]1[ETX]", script.getTokens().poll().getCommand());
    }

    @Test(expected = ScriptParseException.class)
    public void testDelaysWithNonNumericValues() throws IOException {
        List<String> lineWithNonNumericDelay = Arrays.asList(
                "[OUT][ 8a][,0kg[ETX]]1[ETX]]",
                "[OUT][ abc][,0kg[ETX]]1[ETX]]"
        );
        File file = new File("testScript.txt");
        Path path = file.toPath();
        Files.write(path, lineWithNonNumericDelay);

        try {
            script = scriptParser.parse(Files.newInputStream(path));
        } finally {
            file.delete();
        }
    }

    @Test(expected = ScriptParseException.class)
    public void testlineWithWrongCommandType() throws IOException {

        List<String> lineWithWrongCommandType = Arrays.asList(
                "[XOUT][ 8][,0kg[ETX]]1[ETX]]",
                "[IUT][ 2][,0kg[ETX]]1[ETX]]"
        );

        File file = new File("testScript.txt");
        Files.write(file.toPath(), lineWithWrongCommandType);
        try {
            script = scriptParser.parse(Files.newInputStream(file.toPath()));
        } finally {
            file.delete();
        }
    }


}
