package com.toennies.ci1429.app.services.testcases;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.ResponseFormat;
import com.toennies.ci1429.app.model.printer.Printer;
import com.toennies.ci1429.app.model.scale.Commands.Command;
import com.toennies.ci1429.app.model.scale.Scale;
import com.toennies.ci1429.app.model.scale.Scale.Unit;
import com.toennies.ci1429.app.model.scanner.Scanner;

public interface TestService {

	/**
	 * Performs test case for given {@link Scale}
	 */
	DeviceResponse doScaleTest(Scale scale, Command command, ResponseFormat responseFormat, Unit scaleResultUnit);

	/**
	 * Performs test case for given {@link Printer}
	 */
	DeviceResponse doPrinterTest(Printer printer, String template, boolean raw);

	/**
	 * Performs test case for given {@link Scanner}
	 */
	DeviceResponse doScannerTest(Scanner scanner, ResponseFormat responseFormat);

}
