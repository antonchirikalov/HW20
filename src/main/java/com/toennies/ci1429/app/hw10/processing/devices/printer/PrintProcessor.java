package com.toennies.ci1429.app.hw10.processing.devices.printer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.toennies.ci1429.app.hw10.client.ClientType;
import com.toennies.ci1429.app.hw10.client.HW10Client;
import com.toennies.ci1429.app.hw10.processing.HW10CommandDispatcher.Event;
import com.toennies.ci1429.app.hw10.processing.devices.printer.functions.PrinterModelImpl;
import com.toennies.ci1429.app.hw10.processing.events.IHW10ClientCommandMatcher;
import com.toennies.ci1429.app.hw10.util.CommandParser;
import com.toennies.ci1429.app.hw10.util.DeviceValidator;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.services.devices.IDevicesService;

/**
 * Implementation of {@link PrintProcessor} to process printing events requested
 * from client(s).
 * 
 */
@Component
public class PrintProcessor implements IHW10ClientCommandMatcher
{
	// private static final Logger logger = LogManager.getLogger();

	/* Non-greedy match on filler */
	private final static String SKIP = ".*?";

	/* Matches printer device command */
	private final static String ICS = "(ICS)";

	/* Matches upload print-format command */
	private final static String ZI = "(ZI)";

	/* Matches print data command */
	private final static String ZD = "(ZD)";

	/* Matches sending direct to printer command */
	private final static String SA = "(SA)";

	/* Matches direct print commands */
	private final static String xa = "(xa)";
	private final static String xz = "(xz)";

	/* Matches printing upper-case format command */
	private final static String jmb = "(jmb)";
	private final static String jus = "(jus)";

	/* Matches printing lower-case format command */
	private final static String jma = "(jma)";

	private static final Pattern ICS_PATTERN = Pattern.compile(ICS, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern ICS_ZI_PATTERN = Pattern.compile(ICS + SKIP + ZI,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern ICS_ZD_PATTERN = Pattern.compile(ICS + SKIP + ZD,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern ICS_SA_JMB_PATTERN = Pattern.compile(
			ICS + SKIP + SA + SKIP + xa + SKIP + jmb + SKIP + jus + SKIP + xz,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern ICS_SA_JMA_PATTERN = Pattern.compile(
			ICS + SKIP + SA + SKIP + xa + SKIP + jma + SKIP + jus + SKIP + xz,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern ICS_SAText = Pattern.compile(ICS + SKIP + SA,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	@Autowired
	private PrinterModelImpl print;

	@Autowired
	private IDevicesService devicesService;

	/** Matches upload format request using {@link #ZI} pattern. */
	public static boolean isUploadRequested(String command)
	{
		Matcher m = ICS_ZI_PATTERN.matcher(command);
		return m.find();
	}

	/** Matches printing request using {@link #ZD} pattern. */
	public static boolean isPrintRequest(String command)
	{
		Matcher m = ICS_ZD_PATTERN.matcher(command);
		return m.find();
	}

	/** Matches upper-case request using {@link #ICS_SA_JMB_PATTERN} pattern. */
	public static boolean isEnableUpperCase(String command)
	{
		Matcher m = ICS_SA_JMB_PATTERN.matcher(command);
		return m.find();
	}

	/** Matches lower-case request using {@link #ICS_SA_JMA_PATTERN} pattern. */
	public static boolean isEnableLowerCase(String command)
	{
		Matcher m = ICS_SA_JMA_PATTERN.matcher(command);
		return m.find();
	}

	/** Matches direct print request using {@link #ICS_SAText} pattern. */
	public static boolean isDirectPrint(String command)
	{
		Matcher m = ICS_SAText.matcher(command);
		return (m.find() && !isEnableLowerCase(command) && !isEnableUpperCase(command));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean matchesRequest(String command)
	{
		Matcher m = ICS_PATTERN.matcher(command);
		return m.find();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeviceResponse processRequest(Event event)
	{
		String command = (String) event.parameters[0];
		int deviceId = CommandParser.deviceIdParser(command);
		IDevice device = this.devicesService.getDeviceById(deviceId);
		DeviceResponse response = DeviceValidator.isDeviceValid(device, DeviceType.PRINTER);
		if (response != DeviceResponse.OK)
			return response;

		HW10Client client = (HW10Client) event.source;
		// Every Meatline (WinTerm) client will print with big font enabled.
		if (ClientType.MEATLINE.equals(client.getType()))
		{
			response = print.enableBigFont();
			if (response != DeviceResponse.OK)
				return response;

		}

		if (isDirectPrint(command))
			return print.printETI(command);

		if (isUploadRequested(command))
			return print.uploadETI(command);

		if (isPrintRequest(command))
			return print.printETI(command);

		if (isEnableUpperCase(command))
			return print.enableBigFont();

		if (isEnableLowerCase(command))
			return print.enableSmallFont();

		return new DeviceResponse(Status.BAD_REQUEST, "Unknown command '" + command + "'.");
	}

}