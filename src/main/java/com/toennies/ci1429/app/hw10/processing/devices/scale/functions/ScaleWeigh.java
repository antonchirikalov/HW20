package com.toennies.ci1429.app.hw10.processing.devices.scale.functions;

import java.text.DecimalFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.toennies.ci1429.app.hw10.util.CommandParser;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.ResponseFormat;
import com.toennies.ci1429.app.model.scale.Commands.Command;
import com.toennies.ci1429.app.model.scale.Scale.Unit;
import com.toennies.ci1429.app.model.scale.Scale;
import com.toennies.ci1429.app.model.scale.WeightData;
import com.toennies.ci1429.app.model.scale.WeightDataFormatter;
import com.toennies.ci1429.app.services.devices.IDevicesService;
import com.toennies.ci1429.app.util.ASCII;

/**
 * @author Stenzel, Kai
 * 
 *         This class handles all weighing commands, which are:
 *         <ul>
 *         <li>ITEM_ADDING (regGewicht)</li>
 *         <li>WEIGH_AUTOMATIC (Automatikwiegen)</li>
 *         <li>WEIGH (Gewicht)</li>
 *         </ul>
 */
@Component
public class ScaleWeigh
{

	private static final Logger logger = LogManager.getLogger();

	private static final DecimalFormat WEIGHT_FORMAT = new DecimalFormat("########");
	private static final DecimalFormat COUNTER_FORMAT = new DecimalFormat("00000000");


	@Autowired
	private IDevicesService devicesService;

	/**
	 * This method provides weigh values in kilogram.
	 * 
	 * @param command
	 *            an instance of {@link Command}
	 * @param deviceId
	 *            the device id the command should be run on
	 */
	private DeviceResponse weigh(Command command, int deviceId)
	{
		IDevice device = devicesService.getDeviceById(deviceId);
		DeviceResponse response = device.process(command);
		if (response.getPayload() instanceof WeightData)
		{
			WeightData data = response.getPayload();
			Scale.scaleLogger.info("Scale: {} HW10 API: {}", deviceId, WeightDataFormatter.formatWeightData(data, ResponseFormat.HUMAN, 2, Unit.KG));
		}
		return response;
	}

	/**
	 * This method runs the ITEM_ADDING {@link Command} based on the command
	 * param
	 * 
	 * @param command
	 *            the command sent by client. It is used to identify the target
	 *            device and to generate the response
	 * @return a String containing an added response with net and brutto weight,
	 *         a counter and tare
	 */
	public DeviceResponse itemAdd(String command)
	{
		DeviceResponse response = weigh(Command.ITEM_ADDING, CommandParser.deviceIdParser(command));
		if (response.getStatus() != Status.OK_DATA)
			return response;
		
		logger.info("itemAdd result: {}", response);
		return createWeighResponse(command, response, true);
	}

	/**
	 * This method runs the WEIGH {@link Command} based on the command param
	 * 
	 * @param command
	 *            the command sent by client. It is used to identify the target
	 *            device and to generate the response
	 * @return a String containig a net weigh response with brutto and netto
	 *         weight and tare
	 */
	public DeviceResponse netWeigh(String command)
	{
		DeviceResponse response = weigh(Command.WEIGH, CommandParser.deviceIdParser(command));
		if (response.getStatus() != Status.OK_DATA)
			return response;

		logger.info("netWeigh result: {}", response);
		return createWeighResponse(command, response, false);
	}

	/**
	 * This method runs the WEIGH_AUTOMATIC {@link Command} based on the command
	 * param
	 * 
	 * @param command
	 *            the command sent by client. It is used to identify the target
	 *            device and to generate the response
	 * @return a String containing an added response with net and brutto weight,
	 *         a counter and tare
	 */
	public DeviceResponse weighAutomatic(String command)
	{
		DeviceResponse response = this.weigh(Command.WEIGH_AUTOMATIC, CommandParser.deviceIdParser(command));
		if (response.getStatus() != Status.OK_DATA)
			return response;

		logger.info("autoWeigh result: {}", response);
		return createWeighResponse(command, response, true);
	}

	/**
	 * @return a {@link String} containing the net and brutto weight and the
	 *         tare value. If counterNeeded is true then the counter is also
	 *         attached to the response.
	 */
	private static final DeviceResponse createWeighResponse(String command, DeviceResponse response, boolean counterNeeded)
	{
		if (response.getStatus() != Status.OK_DATA)
			return response;

		WeightData data = response.getPayload();

		StringBuilder sb = new StringBuilder();
		sb.append(command);
		sb.append(formatWeight(data.getBrutto()));
		sb.append(ASCII.HT.c);
		sb.append(formatWeight(data.getNetto()));
		sb.append(ASCII.HT.c);
		sb.append(formatWeight(data.getTara()));
		sb.append(ASCII.HT.c);

		if (counterNeeded)
			sb.append(formatCounter(data.getCounter()));

		return new DeviceResponse(sb.toString());
	}

	private static final String formatWeight(double value)
	{
		return format(value, WEIGHT_FORMAT);
	}
	
	private static final String formatCounter(int value)
	{
		return format(value, COUNTER_FORMAT);
	}

	private static final String format(double value, DecimalFormat format)
	{
		return padTo9(format.format(value));
	}
	
	private static final String padTo9(String toPad)
	{
		return new String(new char[9 - toPad.length()]).replace('\0', ' ') + toPad;
	}
	
}