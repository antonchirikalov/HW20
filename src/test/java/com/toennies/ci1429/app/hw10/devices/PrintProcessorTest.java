package com.toennies.ci1429.app.hw10.devices;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.toennies.ci1429.app.hw10.processing.devices.printer.PrintProcessor;

public class PrintProcessorTest
{
	private static final String VALID_UPLOAD_REQUEST = "ICS010ZI";
	private static final String INVALID_UPLOAD_REQUEST = "SCA010ZI";
	private static final String VALID_PRINT_REQUEST = "ICS010ZD";
	private static final String INVALID_PRINT_REQUEST = "SCA010ZD";
	private static final String VALID_UPPER_CASE_REQUEST = "ICS010SA^xa^jmb^jus^xz";
	private static final String INVALID_UPPER_CASE_REQUEST = "SCA010SA^xa^jmb^jus^xz";
	private static final String VALID_LOWER_CASE_REQUEST = "ICS010SA^xa^jma^jus^xz";
	private static final String INVALID_LOWER_CASE_REQUEST = "SCA010SA^xa^jma^jus^xz";
	private static final String VALID_DIRECT_PRINT_REQUEST = "ICS010SA";
	private static final String INVALID_DIRECT_PRINT_REQUEST = "SCA010SA";

	@Test
	public void isUploadRequestedTest()
	{
		assertTrue(PrintProcessor.isUploadRequested(VALID_UPLOAD_REQUEST));
		assertFalse(PrintProcessor.isUploadRequested(INVALID_UPLOAD_REQUEST));
		assertFalse(PrintProcessor.isUploadRequested(VALID_PRINT_REQUEST));
	}

	@Test
	public void isPrintRequestTest()
	{
		assertTrue(PrintProcessor.isPrintRequest(VALID_PRINT_REQUEST));
		assertFalse(PrintProcessor.isPrintRequest(INVALID_PRINT_REQUEST));
		assertFalse(PrintProcessor.isPrintRequest(VALID_UPPER_CASE_REQUEST));
	}

	@Test
	public void isEnableUpperCaseTest()
	{
		assertTrue(PrintProcessor.isEnableUpperCase(VALID_UPPER_CASE_REQUEST));
		assertFalse(PrintProcessor.isEnableUpperCase(INVALID_UPPER_CASE_REQUEST));
		assertFalse(PrintProcessor.isEnableUpperCase(VALID_LOWER_CASE_REQUEST));
		// This is needed because a direct print request is the same command but
		// with different data
		assertFalse(PrintProcessor.isEnableUpperCase(VALID_DIRECT_PRINT_REQUEST));
	}

	@Test
	public void isEnableLowerCaseTest()
	{
		assertTrue(PrintProcessor.isEnableLowerCase(VALID_LOWER_CASE_REQUEST));
		assertFalse(PrintProcessor.isEnableLowerCase(INVALID_LOWER_CASE_REQUEST));
		assertFalse(PrintProcessor.isEnableLowerCase(VALID_UPPER_CASE_REQUEST));
		// This is needed because a direct print request is the same command but
		// with different data
		assertFalse(PrintProcessor.isEnableLowerCase(VALID_DIRECT_PRINT_REQUEST));
	}

	@Test
	public void isDirectPrintTest()
	{
		assertTrue(PrintProcessor.isDirectPrint(VALID_DIRECT_PRINT_REQUEST));
		assertFalse(PrintProcessor.isDirectPrint(INVALID_DIRECT_PRINT_REQUEST));
		assertFalse(PrintProcessor.isDirectPrint(VALID_UPPER_CASE_REQUEST));
		assertFalse(PrintProcessor.isDirectPrint(VALID_LOWER_CASE_REQUEST));
	}

}
