package com.toennies.ci1429.app.network.test.impl;

import com.toennies.ci1429.app.network.test.CommandType;
import com.toennies.ci1429.app.network.test.IScriptParser;
import com.toennies.ci1429.app.network.test.Script;
import com.toennies.ci1429.app.util.ASCII;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Stream;


public class ScriptParser implements IScriptParser {

    private static final String OPERAND_SEPARATOR = " ";

    @Override
    public Script parse(InputStream inputStream) {

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        Stream<String> lines = br.lines();

        Script.ScriptBuilder scriptBuilder = new Script.ScriptBuilder();

        lines.forEach(line -> {
            String[] tokens = line.split(OPERAND_SEPARATOR, 2);
            CommandType operator = CommandType.valueOf(tokens[0].toUpperCase());


            switch (operator)
            {
                case SEND:
                    scriptBuilder.send(removeQuotes(tokens[1]));
                    break;
                case RECEIVE:
                    scriptBuilder.receive(removeQuotes(tokens[1]));
                    break;
                case WAIT:
                    scriptBuilder.delay(removeQuotes(tokens[1]));
                    break;
                case ACTIVATE:
                    scriptBuilder.activate();
                    break;
                case DEACTIVATE:
                    scriptBuilder.deactivate();
                    break;
            }

        } );

        return scriptBuilder.build();
    }

    private String removeQuotes(String command){
        String res = command.replaceAll("\"", "");
        return  ASCII.parseHuman(res.trim());
    }

}
