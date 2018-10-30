package com.toennies.ci1429.app.hw10.devices.scanner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.toennies.ci1429.app.hw10.processing.devices.scanner.ScannerProcessor;
import com.toennies.ci1429.app.util.ASCII;

public class ScannerProcessorTest
{
	private static final String SCANNER_TURN_ON = "SCA011ML";
	private static final String INVALID_SCANNER_TURN_ON = "MCI011ML";
	private static final String SCANNER_TURN_OFF = "SCA011OL";
	private static final String INVALID_SCANNER_TURN_OFF = "MCI011OL";
	private static final String SCANNER_GOOD_READ = "SCA011".concat(String.valueOf(ASCII.ESC.c)).concat("\\[")
			.concat("3q").concat(String.valueOf(ASCII.CR.c));
	private static final String INVALID_SCANNER_GOOD_READ = "MCI011".concat(String.valueOf(ASCII.ESC.c)).concat("\\[")
			.concat("3q").concat(String.valueOf(ASCII.CR.c));
	private static final String SCANNER_BAD_READ = "SCA011".concat(String.valueOf(ASCII.ESC.c)).concat("\\[")
			.concat("4q").concat(String.valueOf(ASCII.CR.c));
	private static final String INVALID_SCANNER_BAD_READ = "MCI011".concat(String.valueOf(ASCII.ESC.c)).concat("\\[")
			.concat("4q").concat(String.valueOf(ASCII.CR.c));

	@Test
	public void isScannerTurnOnTest()
	{
		assertTrue(ScannerProcessor.isScannerTurnOn(SCANNER_TURN_ON));
		assertFalse(ScannerProcessor.isScannerTurnOn(INVALID_SCANNER_TURN_ON));
		assertFalse(ScannerProcessor.isScannerTurnOn(SCANNER_TURN_OFF));
	}

	@Test
	public void isScannerTurnOffTest()
	{
		assertTrue(ScannerProcessor.isScannerTurnOff(SCANNER_TURN_OFF));
		assertFalse(ScannerProcessor.isScannerTurnOff(INVALID_SCANNER_TURN_OFF));
		assertFalse(ScannerProcessor.isScannerTurnOff(SCANNER_TURN_ON));
	}

	@Test
	public void isScannerGoodReadTest()
	{
		assertTrue(ScannerProcessor.isScannerGoodRead(SCANNER_GOOD_READ));
		assertFalse(ScannerProcessor.isScannerGoodRead(INVALID_SCANNER_GOOD_READ));
		assertFalse(ScannerProcessor.isScannerGoodRead(SCANNER_BAD_READ));
	}

	@Test
	public void isScannerBadReadTest()
	{
		assertTrue(ScannerProcessor.isScannerBadRead(SCANNER_BAD_READ));
		assertFalse(ScannerProcessor.isScannerBadRead(INVALID_SCANNER_BAD_READ));
		assertFalse(ScannerProcessor.isScannerBadRead(SCANNER_GOOD_READ));
	}
}
