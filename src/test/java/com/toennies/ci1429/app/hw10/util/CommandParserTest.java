package com.toennies.ci1429.app.hw10.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CommandParserTest
{
	private static final String VALID_COMMAND_DEVICE01 = "SCA011ML";
	private static final String VALID_COMMAND_DEVICE10 = "SCA100ML";

	private static final String INVALID_TARE_WITH_VALUE_COMMAND_NULL = null;
	private static final String INVALID_TARE_WITH_VALUE_COMMAND0 = "MCI011TW";
	private static final String VALID_TARE_WITH_VALUE_COMMAND = "MCI011TW123456";

	@Test
	public void deviceIdParserTest()
	{
		int deviceId = CommandParser.deviceIdParser(VALID_COMMAND_DEVICE01);
		assertTrue(deviceId == 1);
	}

	@Test
	public void deviceIdParserTest10()
	{
		int deviceId = CommandParser.deviceIdParser(VALID_COMMAND_DEVICE10);
		assertTrue(deviceId == 10);
	}

	@Test
	public void getScanResultPrefix01Test()
	{
		String prefix = CommandParser.getScanResultPrefixByCommand(VALID_COMMAND_DEVICE01);
		assertNotNull(prefix);
		assertTrue("SCA011".equals(prefix));

	}

	@Test
	public void getScanResultPrefix10Test()
	{
		String prefix = CommandParser.getScanResultPrefixByCommand(VALID_COMMAND_DEVICE10);
		assertNotNull(prefix);
		assertTrue("SCA100".equals(prefix));

	}

	@Test
	public void parseForTareValueTest()
	{
		assertNull(CommandParser.parseForTareValue(INVALID_TARE_WITH_VALUE_COMMAND_NULL));
		assertNull(CommandParser.parseForTareValue(INVALID_TARE_WITH_VALUE_COMMAND0));
		assertEquals(123456, CommandParser.parseForTareValue(VALID_TARE_WITH_VALUE_COMMAND).intValue());
	}
}
