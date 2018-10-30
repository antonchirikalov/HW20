package com.toennies.ci1429.app.hw10.util;

import com.toennies.ci1429.app.hw10.processing.devices.printer.functions.PrinterModelImpl;
import com.toennies.ci1429.app.hw10.processing.devices.scale.functions.ScaleTareNullify;
import com.toennies.ci1429.app.hw10.processing.devices.scale.functions.ScaleWeigh;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.model.IDevice.DeviceState;

/**
 * Implementation to verify the availability of devices.
 * It is used in {@link PrinterModelImpl}, {@link ScaleTareNullify}, {@link ScaleWeigh}, {@link ScannerModelImpl}
 * to check device status (availability) before continuing using device functionalities.
 * 
 * @author renkenh
 */
public class DeviceValidator
{
	private DeviceValidator()
	{
		//do nth, there must not be an instance of this class
	}
	
	/**
	 * This method validates availability of devices.
     * <p>
     * The availability of devices is performed using the parameters 
     * {@link IDevice#getType() and IDevice#getDeviceState()}.
	 */
	public static DeviceResponse isDeviceValid(IDevice device, DeviceType deviceType)
	{
		if (device == null)
			return new DeviceResponse(Status.BAD_NOT_FOUND, "Device not found.");
		if (device.getType() != deviceType)
			return new DeviceResponse(Status.BAD_REQUEST, "Device " + device.getDeviceID() + " is not a "+deviceType+".");
		if (device.getDeviceState() != DeviceState.CONNECTED)
			return DeviceResponse.BAD_NOT_CONNECTED;
		return DeviceResponse.OK;
	}
}