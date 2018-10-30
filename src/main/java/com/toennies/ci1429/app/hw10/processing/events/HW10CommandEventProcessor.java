/**
 * 
 */
package com.toennies.ci1429.app.hw10.processing.events;

import java.util.List;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.toennies.ci1429.app.hw10.client.HW10MSGTransformer;
import com.toennies.ci1429.app.hw10.processing.HW10CommandDispatcher.Event;
import com.toennies.ci1429.app.hw10.util.CommandParser;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.IDevice.DeviceState;
import com.toennies.ci1429.app.services.devices.IDevicesService;

/**
 * This class manages (allocates) the commands received from clients. It
 * extracts the information received from command(s), and identifies the devices
 * and function requested {@link #deviceService} and {@link #commandProcessors}.
 * Finally, it sends response to {@link HW10MSGTransformer} that will be
 * delivered to a client.
 *
 * @author renkenh
 */
@Component
public class HW10CommandEventProcessor implements IHW10EventProcessor
{

	private static final Logger logger = LogManager.getLogger();

	@Inject
	private List<IHW10ClientCommandMatcher> commandProcessors;

	@Autowired
	private IDevicesService deviceService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeviceResponse process(Event event)
	{
		/** Identification of device. */
		String command = String.valueOf(event.parameters[0]);
		logger.info("Command received: {} ", command);
		int deviceId = CommandParser.deviceIdParser(command);
		IDevice device = deviceService.getDeviceById(deviceId); 
		if (device == null)
			return new DeviceResponse(Status.BAD_NOT_FOUND, "Device "+deviceId+" not found.");
		if (device.getDeviceState() != DeviceState.CONNECTED)
			return DeviceResponse.BAD_NOT_CONNECTED;


		/** Matches the command requested. */
		IHW10ClientCommandMatcher processor = this.commandProcessors.stream().filter(m -> m.matchesRequest(command))
																			 .findFirst()
																			 .orElse(null);
		if (processor == null)
			return new DeviceResponse(Status.BAD_REQUEST, "No processor found for command "+command);

		/** Send a response for packing the message. */
		DeviceResponse response = processor.processRequest(event);
		logger.info("Response to the client: {}", response);
		return response;
	}
}