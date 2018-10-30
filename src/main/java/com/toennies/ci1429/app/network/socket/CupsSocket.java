package com.toennies.ci1429.app.network.socket;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;
import org.cups4j.PrintRequestResult;

import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.util.CupsPrintResult;
import com.toennies.ci1429.app.util.CupsUtil;

/**
 * You can access a webfrontend of the cupsserver via browser. Just visit
 * http://<cupsserver>:<cupsserverport>/
 * 
 * E.g. http://lxrhe116:631/
 * 
 * Webfrontend can list all printers connected to cups server.
 * ttp://<cupsserver>:<cupsserverport>/printers/
 * 
 * E.g. http://lxrhe116:631/printers/
 * 
 * 
 */
@AtSocket("Cups Socket")
@Parameter(name=CupsSocket.PARAM_CUPS_SERVER, value = "lxrhe116", isRequired = true, toolTip="The host sddress. May be a DNS name or an IP address.")
@Parameter(name=CupsSocket.PARAM_CUPS_SERVER_PORT, value = "631", isRequired = true, typeInformation="int:0..65535", toolTip="The cups port on the server. Default=631")
@Parameter(name=CupsSocket.PARAM_PRINTER_NAME, value = "test_jde_hol_raw", isRequired = true, toolTip="The printer name on the cups server.")
public class CupsSocket implements ISocket
{

	private final static Logger logger = LogManager.getLogger();

	/** The host of the cups server. */
	public final static String PARAM_CUPS_SERVER = "host";
	/** The port of the cups server. */
	public final static String PARAM_CUPS_SERVER_PORT = "port";
	/** The printer name of the cups server. */
	public final static String PARAM_PRINTER_NAME = "printername";


	private volatile CupsPrinter printer;


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		// First load parameter information ...
		String printername = config.getEntry(PARAM_PRINTER_NAME);
		String cupsServer = config.getEntry(PARAM_CUPS_SERVER);
		int cupsServerPort = config.getIntEntry(PARAM_CUPS_SERVER_PORT);

		// try to get CupsPrinter with parameters
		this.printer = CupsUtil.returnPrinterByPrinterName(printername, cupsServer, cupsServerPort);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected()
	{
		// If printer is not null, everything is good.
		// See CupsUtil#returnPrinterByPrinterName method
		return this.printer != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void push(byte[] data) throws IOException
	{
		CupsPrintResult lastPrintResult = this.print(printer, data);
		logger.debug("Tried to print with Cups. Result: {}", lastPrintResult);
		if (!lastPrintResult.isSuccessfulResult())
			throw new IOException("Could not print to " + this.printer + ". Result was: " + lastPrintResult);
	}


	private CupsPrintResult print(CupsPrinter printer, byte[] document) {

		if (printer == null) {
			logger.error("Given printer is null");
			return CupsUtil.createErrorResult();
		}

		try {
			PrintRequestResult result = print(printer, new PrintJob.Builder(document).build());
			CupsPrintResult cupsPrintResult = CupsUtil.transform2CupsPrintResult(result);
			logger.info("CupsPrintResult: {}", cupsPrintResult.toString());
			return cupsPrintResult;
		} catch (Exception e) {
			logger.error("Error while printing", e);
			return CupsUtil.createErrorResult();
		}
	}

	/**
	 * Prints a given {@link PrintJob} on given {@link CupsPrinter}.
	 * 
	 * ByteArrayInputStream bai = (ByteArrayInputStream) job.getDocument();
	 * 
	 * bai doesn't contain any formfeed or other non visible characters
	 */
	private PrintRequestResult print(CupsPrinter printer, PrintJob job) throws Exception {
		// Map<String, String> attributes = new HashMap<>();
		// attributes.put("-o", "raw");
		// job.setAttributes(attributes);
		// ByteArrayInputStream bai = (ByteArrayInputStream) job.getDocument();
		// int n = bai.available();
		// byte[] bytes = new byte[n];
		// bai.read(bytes, 0, n);
		// String s = new String(bytes);
		// System.out.println(">>" + s + "<<" + s.hashCode());
		return printer.print(job);
		// return null;
	}

	@Override
	public byte[] poll()
	{
		return null;
	}
	
	@Override
	public byte[] pop()
	{
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void disconnect()
	{
		// May setting printer to null is not enough
		this.printer = null;
	}

	@Override
	public void shutdown()
	{
		this.disconnect();
	}

}
