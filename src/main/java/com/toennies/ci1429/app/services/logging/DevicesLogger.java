/**
 * 
 */
package com.toennies.ci1429.app.services.logging;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.spring.events.Event;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventBusListener;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.IDevice.DeviceState;
import com.toennies.ci1429.app.network.event.IEventHandler;
import com.toennies.ci1429.app.services.devices.IDevicesService;
import com.toennies.ci1429.app.services.logging.LogEvent.EventType;
import com.toennies.ci1429.app.util.LogbookUtil;

/**
 * @author stenzelk
 *
 */
@Component
public class DevicesLogger implements EventBusListener<Object>, IEventHandler
{

	private static final String SYSTEM_ID = "Device";

	@Autowired
	private EventBus.ApplicationEventBus eventBus;
	@Autowired
	private ILogbookService logbook;
	@Autowired
	private IDevicesService service;

	@PostConstruct
	private void init()
	{
		this.eventBus.subscribe(this);
		this.service.getAllDevices().forEach((device) ->
		{
			this.registerDevice(device);
		});
	}

	@Override
	public void onEvent(Event<Object> event)
	{
		switch (event.getTopic())
		{
			case IDevicesService.EVENT_NEW_DEVICE:
				IDevice payload = (IDevice) event.getPayload();
				this.registerDevice(payload);
				break;
			case IDevicesService.EVENT_DEVICE_DELETED:
				payload = (IDevice) event.getPayload();
				this.unregisterDevice(payload);
				break;
		}
	}

	@Override
	public void handleEvent(String eventID, Object source, Object... params)
	{
		IDevice payload = (IDevice) source;
		EventType type = null;
		switch (eventID)
		{
			case IDevice.EVENT_STATE_CHANGED:
				type = EventType.INFO;
				break;
			case IDevice.EVENT_DATA_SEND:
				type = EventType.OUT;
				break;
			case IDevice.EVENT_DATA_RECEIVED:
				type = EventType.IN;
				break;
			case IDevice.EVENT_ERROR_OCCURRED:
				type = EventType.ERROR;
				break;
			case IDevice.EVENT_PARAMS_UPDATED:
				return;
		}
		this.logDeviceEvent(payload.getDeviceID(), type, params[0]);
		this.eventBus.publish(eventID, source, params[0]);
	}

	private void registerDevice(IDevice device)
	{
		this.logbook.registerEventSource(SYSTEM_ID + "." + String.valueOf(device.getDeviceID()));
		device.registerEventHandler(this);
	}

	private void logDeviceEvent(int deviceID, EventType type, Object payload)
	{
		String message = "N/A";
		if (payload instanceof DeviceState)
			message = String.valueOf(payload);
		else if (payload instanceof DeviceResponse && ((DeviceResponse) payload).getPayload() instanceof String)
			message = ((DeviceResponse) payload).getPayload().toString();
		else
			message = LogbookUtil.convertToString(payload);
		this.logbook.logEvent(SYSTEM_ID, String.valueOf(deviceID), type, message);
	}

	private void unregisterDevice(IDevice device)
	{
		this.logbook.unregisterEventSource(SYSTEM_ID + "." + String.valueOf(device.getDeviceID()));
		device.unregisterEventHandler(this);
	}

	private static final long serialVersionUID = 1L;

}
