package com.toennies.ci1429.app.network.test;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Script
{
	private Queue<Token> tokens = new LinkedList<>();

	public Queue<Token> getTokens() {
		return tokens;
	}

	public static class ScriptBuilder {

		private Token currentToken;

		private List<Token> builtTokens = new LinkedList<>();

		public ScriptBuilder send(String cmd) {
			currentToken = new Token(CommandType.SEND, cmd );
			builtTokens.add(currentToken);
			return this;
		}

		public ScriptBuilder receive(String cmd) {
			currentToken = new Token(CommandType.RECEIVE, cmd);
			builtTokens.add(currentToken);
			return this;
		}

		public ScriptBuilder activate() {
			currentToken = new Token(CommandType.ACTIVATE);
			builtTokens.add(currentToken);
			return this;
		}

		public ScriptBuilder delay(String delayInMs) {
			currentToken = new Token(CommandType.WAIT, delayInMs);
			builtTokens.add(currentToken);
			return this;
		}

		public ScriptBuilder deactivate() {
			currentToken = new Token(CommandType.DEACTIVATE);
			builtTokens.add(currentToken);
			return this;
		}

		public Script build() {
			Script script = new Script();
			script.getTokens().addAll(builtTokens);
			return script;
		}
	}




}
