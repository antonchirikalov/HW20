package com.toennies.ci1429.app.hw10.processing.devices.printer.functions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.toennies.ci1429.app.hw10.util.CommandParser;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.printer.ICSTemplate;
import com.toennies.ci1429.app.model.printer.LabelData;
import com.toennies.ci1429.app.services.devices.IDevicesService;

/**
 * Implementation to upload and print data received from client request.
 *
 */
@Component
public class PrinterModelImpl
{
	private static final Logger logger = LogManager.getLogger();
	
	public static final String TEMPLATE_NAME = "JS";

//	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private IDevicesService devicesService;

	private boolean smallFont = false;
	private boolean bigFont = false;


	/**
	 * This method is used to print requested information that are converted to
	 * ZPL format.
	 * <p>
	 * There are two steps to proceed printing labels: retrieve font size set,
	 * convert ICS format to ZPL format.
	 */
	private DeviceResponse printData(String data, int deviceId)
	{
		IDevice device = devicesService.getDeviceById(deviceId);

		LabelData labelData = ICSLabelDataParser.parseToLabelData(data);
		labelData.setFontSize(this.smallFont, this.bigFont);
		return device.process(labelData);
	}

	/**
	 * This method prints labeled data.
	 */
	public DeviceResponse printETI(String command)
	{
		int deviceId = CommandParser.deviceIdParser(command);
		return printData(command, deviceId);
	}

	/**
	 * This methods converts ICS to ZPL format and uploads the converted format
	 * into printers memory (directory).
	 * <p>
	 * It identifies the availability of printer COM ID, proceeds with ICS to
	 * ZPL format converting and uploads the ZPL format.
	 */
	private DeviceResponse uploadFormat(String data, int deviceId)
	{
		IDevice device = devicesService.getDeviceById(deviceId);
		try
		{
			ICSTemplate template = ICSTemplateParser.parseICSFormat(data);
			return device.process(template);
		}
		catch (Exception ex)
		{
			logger.error("Could not parse given ICS data: {}", data, ex);
			return new DeviceResponse(Status.BAD_REQUEST, "Given data does not comply to HW10 'format'.");
		}
	}

	/**
	 * This method uploads the format into printers memory (directory).
	 * <p>
	 * It parses the device COM port and the ICS format
	 * {@link #uploadFormat(String, int)}.
	 */
	public DeviceResponse uploadETI(String format)
	{
		return uploadFormat(format, CommandParser.deviceIdParser(format));
	}

	public DeviceResponse enableSmallFont()
	{
		this.smallFont = true;
		this.bigFont = !this.smallFont;
		return DeviceResponse.OK;
	}

	public DeviceResponse enableBigFont()
	{
		this.smallFont = false;
		this.bigFont = !this.smallFont;
		return DeviceResponse.OK;
	}

}