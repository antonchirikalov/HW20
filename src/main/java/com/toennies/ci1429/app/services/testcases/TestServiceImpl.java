package com.toennies.ci1429.app.services.testcases;

import org.springframework.stereotype.Component;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.ResponseFormat;
import com.toennies.ci1429.app.model.printer.Printer;
import com.toennies.ci1429.app.model.scale.Commands.Command;
import com.toennies.ci1429.app.model.scale.Scale;
import com.toennies.ci1429.app.model.scale.Scale.Unit;
import com.toennies.ci1429.app.model.scanner.Scanner;

@Component
public class TestServiceImpl implements TestService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeviceResponse doScaleTest(Scale scale, Command command, ResponseFormat responseFormat, Unit scaleResultUnit) {
		return doTestCase(new ScaleTestCase(scale, command, responseFormat, scaleResultUnit));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeviceResponse doPrinterTest(Printer printer, String template, boolean raw) {
		return doTestCase(new PrinterTestCase(printer, template));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeviceResponse doScannerTest(Scanner scanner, ResponseFormat responseFormat) {
		return doTestCase(new ScannerTestCase(scanner, responseFormat));
	}

	/**
	 * Performs the proper test case.
	 */
	private DeviceResponse doTestCase(@SuppressWarnings("rawtypes") TestCase tc) {
		return tc.testDevice();
	}

}
