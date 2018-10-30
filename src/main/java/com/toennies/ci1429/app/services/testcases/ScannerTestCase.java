package com.toennies.ci1429.app.services.testcases;

import com.toennies.ci1429.app.model.DeviceException;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.ResponseType;
import com.toennies.ci1429.app.model.ResponseFormat;
import com.toennies.ci1429.app.model.scanner.Scanner;
import com.toennies.ci1429.app.util.ScannerUtil;

public class ScannerTestCase extends TestCase<Scanner> {

	private ResponseFormat responseFormat;

	public ScannerTestCase(Scanner device, ResponseFormat responseFormat) {
		super(device);
		this.responseFormat = responseFormat;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DeviceResponse _doTest() throws DeviceException {
		DeviceResponse response = device.process(responseFormat);
		if (response.getStatus().type != ResponseType.DATA)
			return response;
		return new DeviceResponse(ScannerUtil.formatResponse(response.getPayload()));
	}
}
