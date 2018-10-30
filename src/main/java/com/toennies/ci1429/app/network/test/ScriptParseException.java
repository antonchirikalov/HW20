package com.toennies.ci1429.app.network.test;

public class ScriptParseException extends RuntimeException {

    private static final String SCRIPT_PARSE_EXCEPTION_MESSAGE_PREFIX = "Parsing of line failed. ";

    public ScriptParseException(String message) {
        super(SCRIPT_PARSE_EXCEPTION_MESSAGE_PREFIX + message);
    }

    public ScriptParseException(String message, Throwable originalException) {
        super(SCRIPT_PARSE_EXCEPTION_MESSAGE_PREFIX + message, originalException);
    }

}
