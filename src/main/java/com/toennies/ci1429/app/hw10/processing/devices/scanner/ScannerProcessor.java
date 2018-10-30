package com.toennies.ci1429.app.hw10.processing.devices.scanner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.toennies.ci1429.app.hw10.client.HW10Client;
import com.toennies.ci1429.app.hw10.processing.HW10CommandDispatcher.Event;
import com.toennies.ci1429.app.hw10.processing.devices.scanner.functions.HW10ScanRegistry;
import com.toennies.ci1429.app.hw10.processing.devices.scanner.functions.ScanResponseListener;
import com.toennies.ci1429.app.hw10.processing.events.IHW10ClientCommandMatcher;
import com.toennies.ci1429.app.hw10.util.CommandParser;
import com.toennies.ci1429.app.hw10.util.DeviceValidator;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.services.devices.IDevicesService;

@Component
public class ScannerProcessor implements IHW10ClientCommandMatcher
{

	private final static String SKIP = ".*?";
	private final static String SCA = "(SCA)";
	private final static String BRA = "(\\[)";
	private final static String THREE = "(3)";
	private final static String FOUR = "(4)";
	private final static String Q = "(q)";
	private final static String ML = "(ML)";
	private final static String OL = "(OL)";

	private static final Pattern SCA_THREE_PATTERN = Pattern.compile(SCA + SKIP + BRA + THREE + Q,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern SCA_FOUR_PATTERN = Pattern.compile(SCA + SKIP + BRA + FOUR + Q,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern SCA_OL_PATTERN = Pattern.compile(SCA + SKIP + OL,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern SCA_ML_PATTERN = Pattern.compile(SCA + SKIP + ML,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private static final Pattern SCA_PATTERN = Pattern.compile(SCA, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	@Autowired
	private HW10ScanRegistry scanRegistry;
	@Autowired
	private IDevicesService service;

	/**
	 * Checks whether the given command is a ScannerGoodRead command. A valid
	 * scanner turn on command starts with SCA and ends with [ESC][3q[CR], e.g.,
	 * SCA011[ESC][3q[CR].
	 */
	public static boolean isScannerGoodRead(String command)
	{
		Matcher m = SCA_THREE_PATTERN.matcher(command);
		return m.find();
	}

	/**
	 * Checks whether the given command is a ScannerTurnOff command. A valid
	 * scanner turn on command starts with SCA and ends with [ESC][4q[CR], e.g.,
	 * SCA011[ESC][4q[CR].
	 */
	public static boolean isScannerBadRead(String command)
	{
		Matcher m = SCA_FOUR_PATTERN.matcher(command);
		return m.find();
	}

	/**
	 * Checks whether the given command is a ScannerTurnOff command. A valid
	 * scanner turn on command starts with SCA and ends with OL, e.g., SCA011OL.
	 */
	public static boolean isScannerTurnOff(String command)
	{
		Matcher m = SCA_OL_PATTERN.matcher(command);
		return m.find();
	}

	/**
	 * Checks whether the given command is a ScannerTurnOn command. A valid
	 * scanner turn on command starts with SCA and ends with ML, e.g., SCA011ML.
	 */
	public static boolean isScannerTurnOn(String command)
	{
		Matcher m = SCA_ML_PATTERN.matcher(command);
		return m.find();
	}

	/**
	 * This method checks whether the given command matches a Scanner function.
	 */
	@Override
	public boolean matchesRequest(String command)
	{
		Matcher m = SCA_PATTERN.matcher(command);
		return m.find();
	}

	/**
	 * This method is used to process scanner commands. Valid scanner functions
	 * are:
	 * <ul>
	 * <li>Scanner turn on</li>
	 * <li>Scanner turn off</li>
	 * <li>Scanner good read</li>
	 * <li>Scanner bad read</li>
	 * </ul>
	 * 
	 * @param event
	 *            an instance of an {@link Event} providing the request with all
	 *            needed info
	 * 
	 * @return [ACK] for valid requestS, [NAK] otherwise.
	 */
	@Override
	public DeviceResponse processRequest(Event event)
	{
		String command = (String) event.parameters[0];
		HW10Client listener = (HW10Client) event.source;
		
		final int deviceId = CommandParser.deviceIdParser(command);
		IDevice device = this.service.getDeviceById(deviceId);
		DeviceResponse response = DeviceValidator.isDeviceValid(device, DeviceType.SCANNER);
		if (response != DeviceResponse.OK)
			return response;
		
		if (isScannerGoodRead(command))
			return DeviceResponse.OK;

		if (isScannerBadRead(command))
			return DeviceResponse.OK;
		
		if (ScannerProcessor.isScannerTurnOn(command))
		{
			scanRegistry.addScanListener(new ScanResponseListener(listener, command), deviceId);
			return DeviceResponse.OK;
		}
		
		if (ScannerProcessor.isScannerTurnOff(command))
		{
			scanRegistry.removeScanListener(listener.getID(), CommandParser.deviceIdParser(command));
			return DeviceResponse.OK;
		}
		
		return new DeviceResponse(Status.BAD_REQUEST, "Unknown command '"+command+"'.");
	}
}