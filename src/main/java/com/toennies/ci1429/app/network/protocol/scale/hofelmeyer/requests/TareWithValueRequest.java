package com.toennies.ci1429.app.network.protocol.scale.hofelmeyer.requests;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class TareWithValueRequest extends SimpleRequest
{
	private static final String TARE_WITH_VALUE_COMMAND_PREFIX = "TM";

	private static final DecimalFormat TARE_VALUE_FORMATTER;

	static
	{
		TARE_VALUE_FORMATTER = new DecimalFormat("000000.00");
		DecimalFormatSymbols decimalFormatSymbol = new DecimalFormatSymbols();
		decimalFormatSymbol.setDecimalSeparator(',');
		TARE_VALUE_FORMATTER.setDecimalFormatSymbols(decimalFormatSymbol);
	}

	public TareWithValueRequest(Object value)
	{
		super(createCommand(value));
	}

	private static String createCommand(Object valueObject)
	{
		double value = Double.valueOf(String.valueOf(valueObject));
		return TARE_WITH_VALUE_COMMAND_PREFIX + TARE_VALUE_FORMATTER.format(value);
	}
}
