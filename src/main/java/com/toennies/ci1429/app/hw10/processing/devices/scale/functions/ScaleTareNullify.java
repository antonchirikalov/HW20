package com.toennies.ci1429.app.hw10.processing.devices.scale.functions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.toennies.ci1429.app.hw10.util.CommandParser;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.scale.Commands.Command;
import com.toennies.ci1429.app.services.devices.IDevicesService;
import com.toennies.ci1429.app.util.ASCII;

/**
 * @author Stenzel, Kai
 * 
 *         This method handles all non-weighing {@link Command}s. These are:
 *         <ul>
 *         <li>ZERO (Nullstellen)</li>
 *         <li>TARE (Tarieren)</li>
 *         <li>CLEAR_TARE (Tara löschen)</li>
 *         <li>TARE_WITH_VALUE (Tara Wertvorgabe)</li>
 *         </ul>
 *
 */
@Component
public class ScaleTareNullify
{
	/* Horizontal Tab */
	private static final String HT = String.valueOf(ASCII.HT.c);

	/* NOK = Not Okay */
	private static final String NOK = "NOK";

	/* OK = Okay */
	private static final String OK = "OK";

	@Autowired
	private IDevicesService devicesService;

	/**
	 * This method is for executing any tare and zero scaleCommand on the scale
	 * specified by deviceId
	 * 
	 * @param scaleCommand
	 *            a {@link Command} type
	 * @param deviceId
	 *            the id of the device the scaleCommand should be run on
	 * @param params
	 *            additional parameters for the process
	 * @return true when execution resulted in {@link Status} = OK. False
	 *         otherwise.
	 */
	private DeviceResponse processCommand(Command scaleCommand, int deviceId, Object... params)
	{
		IDevice device = devicesService.getDeviceById(deviceId);

		Object[] arr = new Object[params.length+1];
		arr[0] = scaleCommand;
		System.arraycopy(params, 0, arr, 1, params.length);
		return device.process(arr);
	}

	/**
	 * This method is deleting the current tare value from a scale. Therefore it
	 * uses the CLEAR_TARE {@link Command} based on the command param
	 * 
	 * @param command
	 *            the command sent by client. It is used to identify the target
	 *            device and to generate the response
	 * @return either a positive or a negative response depending on the
	 *         execution of the command, whether it executed successfully or not
	 */
	public DeviceResponse clearTare(String command)
	{
		DeviceResponse response = this.processCommand(Command.CLEAR_TARE, CommandParser.deviceIdParser(command));
		return generateResponse(response, command);
	}

	/**
	 * This method sets the tare on a specific scale based on a value included
	 * in the command param. Therefore it uses the TARE_WITH_VALUE
	 * {@link Command} based on the command param.
	 * 
	 * @param command
	 *            the command sent by client. It is used to identify the target
	 *            device and to generate the response. The command consists of
	 *            the actual command (e.g., MCI011TW) and a value in kg appended
	 *            (e.g., MCI011TW123) means that the Scale should be tared to
	 *            123 kg.
	 * @return either a positive or a negative response depending on the
	 *         execution of the command, whether it executed successfully or not
	 */
	public DeviceResponse tareWithValue(String command)
	{
		Integer tare = CommandParser.parseForTareValue(command);
		if (tare == null)
			return new DeviceResponse(Status.BAD_REQUEST, "Command "+command+" did not contain a valid tare value.");

		DeviceResponse response = this.processCommand(Command.TARE_WITH_VALUE, CommandParser.deviceIdParser(command), tare);
		return generateResponse(response, command);
	}

	/**
	 * By using this, you can set the tare value of a scale to the current
	 * weight value. Therefor it uses the TARE {@link Command} based on the
	 * command param
	 * 
	 * @param command
	 *            the command sent by client. It is used to identify the target
	 *            device and to generate the response.
	 * @return either a positive or a negative response depending on the
	 *         execution of the command, whether it executed successfully or not
	 */
	public DeviceResponse tare(String command)
	{
		DeviceResponse response = this.processCommand(Command.TARE, CommandParser.deviceIdParser(command));
		return generateResponse(response, command);
	}

	/**
	 * This method nullifies the scale values. Therefore it uses the ZERO
	 * {@link Command} based on the command param.
	 * 
	 * @param command
	 *            the command sent by client. It is used to identify the target
	 *            device and to generate the response.
	 * @return either a positive or a negative response depending on the
	 *         execution of the command, whether it executed successfully or not
	 */
	public DeviceResponse zero(String command)
	{
		DeviceResponse response = this.processCommand(Command.ZERO, CommandParser.deviceIdParser(command));
		return generateResponse(response, command);
	}

	/**
	 * @return {@link #generatePositiveResponse(String)} when successful param
	 *         is true, {@link #generateNegativeResponse(String)} otherwise.
	 */
	private static final DeviceResponse generateResponse(DeviceResponse response, String command)
	{
		if (response == DeviceResponse.OK)
			return new DeviceResponse(generatePositiveResponse(command));

		return new DeviceResponse(generateNegativeResponse(command));
	}

	/**
	 * @return a String containing a message stating that the execution of the
	 *         Scale was successful
	 */
	private static final String generatePositiveResponse(String command)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(command);
		sb.append(OK);
		sb.append(HT);
		sb.append("Positive Quittung von Waage");
		return sb.toString();
	}

	/**
	 * @return a String containing a message stating that the execution of the
	 *         Scale was <b>NOT</b> successful
	 */
	private static final String generateNegativeResponse(String command)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(command);
		sb.append(NOK);
		sb.append(HT);
		sb.append("Negative Quittung von Waage");
		return sb.toString();
	}
}
