package com.toennies.ci1429.app.network.test;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class ScriptBuilderTest {

    @Test
    public void testScriptBuilding() {


        Script.ScriptBuilder scriptBuilder = new Script.ScriptBuilder();
        scriptBuilder.activate().send("ACK").receive("NACK").delay("100").deactivate().build();
        Script script = scriptBuilder.build();

        assertTrue((scriptBuilder.build()) != null);
        assertTrue(script.getTokens().size() == 5);
        assertTrue(script.getTokens().poll().getCommand() == null);
        Token token = script.getTokens().poll();
        assertEquals(token.getCommandType(), CommandType.SEND);
        assertEquals(token.getCommand(), "ACK");

        System.out.println(script.getTokens());
    }
}
