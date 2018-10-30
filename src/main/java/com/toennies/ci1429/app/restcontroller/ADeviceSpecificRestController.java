/**
 * 
 */
package com.toennies.ci1429.app.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.services.devices.DevicesService;
import com.toennies.ci1429.app.services.logging.RestLogger;

/**
 * RestController Support type to simplify the checks against the devices and their state. Since
 * the devices have a generic return interface - mapping is used to further simplify the generation
 * of the returned entities.
 * @author renkenh
 */
@Component
public abstract class ADeviceSpecificRestController
{

	public static final String EVENT_REQUEST_RECEIVED = "REQUEST_RECIEVED";

	public static final String EVENT_BATCH_REQUEST_RECEIVED = "BATCH_REQUEST_RECIEVED";

	public static final String EVENT_RESPONSE_SEND = "RESPONSE_SEND";


	@Autowired
	protected DevicesService devicesService;
	
	@Autowired
	private RestLogger logger;
	
	protected final DeviceType type;

	/**
	 * Constuctor.
	 * @param type The device type this controller handles.
	 */
	protected ADeviceSpecificRestController(DeviceType type)
	{
		this.type = type;
	}
	

	/**
	 * Processes a given request for a given device. Does all the checks against the parameters and device states.
	 * Furthermore, it uses mapping to generically convert the response.
	 * @param deviceID The deviceID of the device which will process the request. 
	 * @param params The parameters of the request, required by the device (in a specific order).
	 * @return A response entity containing the an appropriate payload.
	 */
	public final DeviceResponse processRequest(int deviceID, Object... params)
	{
		this.logger.handleEvent(EVENT_REQUEST_RECEIVED, Integer.valueOf(deviceID), params);
		IDevice device = devicesService.getDeviceById(deviceID);
		DeviceResponse response = checkDevice(device, this.type);
		if (response == null)
			response = device.process(params);

		this.logger.handleEvent(EVENT_RESPONSE_SEND, Integer.valueOf(deviceID), response);
		return response;
	}
	
	/**
	 * This method is for batched requests, i.e. where the same request must be executed several times and the results
	 * will be returned as one response.
	 * Processes a given request for a given device. Does all the checks against the parameters and device states.
	 * Furthermore, it uses mapping to generically convert the response.
	 * @param deviceID The deviceID of the device which will process the request. 
	 * @param batch How often the request must be processed.
	 * @param params The parameters of the request, required by the device (in a specific order).
	 * @return A response entity containing the an appropriate payload.
	 */
	protected final DeviceResponse batchProcessRequest(int deviceID, int batch, Object... params)
	{
		this.logger.handleEvent(EVENT_BATCH_REQUEST_RECEIVED, Integer.valueOf(deviceID), Integer.valueOf(batch), params);
		IDevice device = this.devicesService.getDeviceById(deviceID);
		DeviceResponse response = checkDevice(device, this.type);
		if (response == null)
			response = device.batchProcess(batch, params);

		this.logger.handleEvent(EVENT_RESPONSE_SEND, Integer.valueOf(deviceID), response);
		return response;
	}

	/**
	 * Does some checks against the device and its state.
	 * @return A response entity containing the check result or <code>null</code>.
	 */
	protected static final DeviceResponse checkDevice(IDevice device, DeviceType type)
	{
		if (device == null)
			return new DeviceResponse(Status.BAD_NOT_FOUND, "Could not find requested device.");
		if (device.getType() != type)
			return new DeviceResponse(Status.BAD_REQUEST, "Requested device not of type " + type);
		return null;
	}

}
