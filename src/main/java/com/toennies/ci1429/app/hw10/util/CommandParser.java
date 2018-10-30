package com.toennies.ci1429.app.hw10.util;

import com.toennies.ci1429.app.hw10.processing.devices.scale.ScaleProcessor;

/**
 * Implementation to parse/extract useful information received from device command(s). 
 *
 * @author renkenh
 */
public class CommandParser
{

	private CommandParser()
	{
		// do nth, there must not be an instance of this class
	}

	/**
	 * This method extracts the COM port from the requested command e.g.,
	 * SCA011ML -> 01 This command must not be null.
	 */
	public static int deviceIdParser(String command)
	{
		String substring = command.substring(3, 5);
		try
		{
			return Integer.valueOf(substring);
		}
		catch (NumberFormatException ex)
		{
			return -1;
		}
	}

	/**
	 * This method extracts entire scanning command requested e.g., 
	 * SCA011ML -> SCA011 This command must not be null.
	 */
	public static String getScanResultPrefixByCommand(String command)
	{
		String prefix = new String(command.toCharArray(), 0, 6);
		return prefix;
	}

	/**
	 * This method extracts the tare value in kg from the tare with value
	 * command given to the server e.g., 
	 * MCI011TW123456 -> 123456.
	 */
	public static Integer parseForTareValue(String scaleCommand)
	{
		if (scaleCommand != null && ScaleProcessor.isTareWithValue(scaleCommand))
		{
			String tareValue = scaleCommand.substring(8);
			return (!tareValue.isEmpty()) ? Integer.parseInt(tareValue) : null;
		}
		return null;
	}
}