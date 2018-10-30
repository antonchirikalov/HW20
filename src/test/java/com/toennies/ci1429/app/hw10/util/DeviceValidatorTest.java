package com.toennies.ci1429.app.hw10.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.scanner.Scanner;
import com.toennies.ci1429.app.repository.DeviceDescriptionEntity;

public class DeviceValidatorTest
{
	private static final Scanner scannerNull = null;
	private static final DeviceDescriptionEntity description = new DeviceDescriptionEntity(DeviceType.SCANNER,
			"Powerscan", "Datalogic", "com.toennies.ci1429.app.network.protocol.scanner.PM9500ScannerProtocol", null);
	private static final Scanner validDisconnectedScanner = new Scanner(description);

	@Test
	public void isDeviceValidTest()
	{
		assertEquals(DeviceValidator.isDeviceValid(scannerNull, DeviceType.SCANNER).getStatus(), Status.BAD_NOT_FOUND);
		assertEquals(DeviceValidator.isDeviceValid(validDisconnectedScanner, DeviceType.SCANNER).getStatus(), Status.BAD_NOT_CONNECTED);
		assertEquals(DeviceValidator.isDeviceValid(validDisconnectedScanner, DeviceType.SCALE).getStatus(), Status.BAD_REQUEST);
	}
}
