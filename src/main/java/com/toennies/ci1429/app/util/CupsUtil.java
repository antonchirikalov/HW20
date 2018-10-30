package com.toennies.ci1429.app.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.PrintRequestResult;

/**
 * Class that provides some static methods for working with {@link CupsPrinter}.
 */
public class CupsUtil {

	private final static Logger LOGGER = LogManager.getLogger();

	private CupsUtil() {
		//
	}

	/**
	 * Returns the system default {@link CupsPrinter}. May return null if
	 * exceptions occours.
	 */
	public static CupsPrinter returnDefaultPrinter(String cupsServer, int cupsServerPort) {
		try {
			CupsClient cc = returnCupsClient(cupsServer, cupsServerPort);
			CupsPrinter cp = cc.getDefaultPrinter();
			LOGGER.info("Found default printer with name: ", cp.getName());
			return cp;
		} catch (Exception e) {
			LOGGER.error("Error while returning default printer.", e);
			return null;
		}
	}

	/**
	 * Returns a printer via given printer name. May return null if exception
	 * occours or no printer found with given name.
	 */
	public static CupsPrinter returnPrinterByPrinterName(String printername, String cupsServer, int cupsServerPort) {
		try {
			CupsClient cc = returnCupsClient(cupsServer, cupsServerPort);
			for (CupsPrinter cp : cc.getPrinters()) {
				if (cp.getName().equalsIgnoreCase(printername)) {
					LOGGER.info("Found printer with name: {}", printername);
					return cp;
				}
			}
			LOGGER.warn("No printer found with name: {}", printername);
			return null;
		} catch (Exception e) {
			LOGGER.error("Error while returning printer with name: {}", printername, e);
			return null;
		}
	}

	private static CupsClient returnCupsClient(String cupsServer, int cupsServerPort) throws Exception {
		CupsClient cupsClient = new CupsClient(cupsServer, cupsServerPort);
		logAllPrinterNames(cupsClient, cupsServer, cupsServerPort);
		return cupsClient;
	}

	private static void logAllPrinterNames(CupsClient cupsClient, String cupsServer, int cupsServerPort)
			throws Exception {
		// If info is enabled, all found printer names gets logged
		if (LOGGER.isInfoEnabled()) {
			StringBuilder sb = new StringBuilder();
			cupsClient.getPrinters().stream().forEach(p -> sb.append(p.getName()).append(";"));
			LOGGER.info("All found printers on cups server {} with port {}: {}", cupsServer, cupsServerPort,
					sb.toString());
		}
	}

	/**
	 * Takes a {@link PrintRequestResult} and transforms it to pojo class
	 * {@link CupsPrintResult}.
	 */
	public static CupsPrintResult transform2CupsPrintResult(PrintRequestResult result) {
		return new CupsPrintResult(result.getJobId(), result.getResultCode(), result.getResultDescription(),
				result.isSuccessfulResult());
	}

	/**
	 * @return a {@link CupsPrintResult} that represents an error while
	 *         printing.
	 */
	public static CupsPrintResult createErrorResult() {
		return new CupsPrintResult(-1, "", "", false);
	}

}
