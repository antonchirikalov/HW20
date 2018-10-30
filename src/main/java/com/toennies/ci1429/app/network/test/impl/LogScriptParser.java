package com.toennies.ci1429.app.network.test.impl;

import com.toennies.ci1429.app.network.test.IScriptParser;
import com.toennies.ci1429.app.network.test.Script;
import com.toennies.ci1429.app.network.test.ScriptParseException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;



public class LogScriptParser implements IScriptParser {

	private static final Pattern LOG_SCRIPT_LINE_PATTERN = Pattern.compile("\\[(.*)]\\[(.*)]\\[(.*)]");

	@Override
	public Script parse(InputStream inputStream) {

		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		Stream<String> lines = br.lines();
		Script.ScriptBuilder scriptBuilder = new Script.ScriptBuilder();
/*
		lines.forEach(line -> {

			List<String> tokens = parseLine(line);

			if ("send".equalsIgnoreCase(tokens.get(0))) {

				scriptBuilder.send();
				//don't add delay since script language is not supposed to have delay for incoming data

			} else if ("out".equalsIgnoreCase(tokens.get(0))) {

				scriptBuilder.out();

				try {
					scriptBuilder.delay(Long.parseLong(tokens.get(1)));
				} catch (NumberFormatException e) {
					String errorMessage = MessageFormat.format(
							"Delay token [{0}] doesn't contain numeric value", tokens.get(1));
					throw new ScriptParseException(errorMessage, e);
				}

			} else {
				String errorMessage = MessageFormat.format(
						"Invalid Command type [{0}] send line ''{1}''. It must be either ''In'' or ''Out''",
						tokens.get(0), line);
				throw new ScriptParseException(errorMessage);
			}

			scriptBuilder.command(tokens.get(2));

		});*/

		return scriptBuilder.build();
	}

	private List<String> parseLine(String line) {
		Matcher matcher = LOG_SCRIPT_LINE_PATTERN.matcher(line);

		List<String> tokens = new ArrayList<>();

		while (matcher.find()) {
            tokens.add(matcher.group(1).trim()); // fetch commandType
            tokens.add(matcher.group(2).trim()); // fetch delay
            tokens.add(matcher.group(3).trim()); // fetch command
        }

        if (tokens.size() != 3) {
			String errorMessage = MessageFormat.format(
					"Line ''{0}'' couldn't have been parsed", line);
			throw new ScriptParseException(errorMessage);
		}
		return tokens;
	}
}
