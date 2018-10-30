package com.toennies.ci1429.app.util;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class PrinterUtil {

	private PrinterUtil() {
	}

	/**
	 * Returns a printer with given name. May return null, if no printer is
	 * found.
	 * 
	 * Example printer names on rhe3759 (krawinkm computer)
	 * 
	 * <pre>
	  	Printer: ZDesigner 110Xi4 300 dpi
		Printer: PDFCreator
		Printer: Microsoft XPS Document Writer
		Printer: Fax
		Printer: \\ntrhep02\RHE010KO
		Printer: \\ntrhep02\RHE002KF
		Printer: \\ntrhep02\RHE317KF
	 * </pre>
	 */
	public static PrintService returnLocalPrinterByName(String localPrinterName) {
		PrintService[] localPrinters = returnLocalPrinters();
		for (PrintService ps : localPrinters) {
			if (ps.getName().equalsIgnoreCase(localPrinterName)) {
				return ps;
			}
		}
		return null;
	}

	/**
	 * See {@link PrintServiceLookup#lookupDefaultPrintService()}
	 */
	public static PrintService returnLocalDefaultPrinter() {
		PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();
		return defaultPrintService;
	}

	/**
	 * Returns a list of locally conntected printers.
	 */
	public static PrintService[] returnLocalPrinters() {
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		return printServices;
	}

}
