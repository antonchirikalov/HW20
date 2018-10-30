package com.toennies.ci1429.app.hw10.processing.events;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.toennies.ci1429.app.hw10.client.HW10Client;
import com.toennies.ci1429.app.hw10.processing.HW10CommandDispatcher;
import com.toennies.ci1429.app.hw10.processing.HW10CommandDispatcher.Event;
import com.toennies.ci1429.app.hw10.processing.devices.HW10ClientConsumer;
import com.toennies.ci1429.app.hw10.processing.devices.scanner.functions.HW10ScanRegistry;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.network.event.AEventNotifier;

/**
 * This class manages the new incoming clients
 * {@link #startNewClient(HW10Client)}. When shutdown event
 * {@link #shutdownClient(HW10Client)} is received, it attempts to disconnect
 * from remote device.
 * 
 * @author renkenh
 */
@Component
public class HW10ClientEventProcessor extends AEventNotifier implements IHW10EventProcessor
{
	
	public static final String EVENT_NEW_CLIENT = "NEW_HW10CLIENT";

	public static final String EVENT_CLIENT_SHUTDOWN = "HW10CLIENT_SHUTDOWN";


	private final Map<String, HW10ClientConsumer> consumerRegistry = new HashMap<>();

	@Autowired
	private HW10ScanRegistry scanRegister;
	
	@Autowired
	private HW10CommandDispatcher dispatcher;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeviceResponse process(Event event)
	{
		HW10Client client = (HW10Client) event.source;
		switch (event.eventType)
		{
			case NEW_CLIENT:
				this.startNewClient(client);
				break;
			case CLIENT_SHUTDOWN:
				this.shutdownClient(client);
				break;
			default:
				throw new IllegalArgumentException("Wrong event");
		}
		return DeviceResponse.OK;
	}

	private void startNewClient(HW10Client client)
	{
		HW10ClientConsumer consumer = new HW10ClientConsumer(client, dispatcher);
		this.consumerRegistry.put(client.getID(), consumer);
		consumer.start();
		this.publishEvent(EVENT_NEW_CLIENT, client);
	}

	private void shutdownClient(HW10Client client)
	{
		this.scanRegister.removeScanListenerById(client.getID());
		this.consumerRegistry.remove(client.getID()).shutdown();
		this.publishEvent(EVENT_CLIENT_SHUTDOWN, client);
	}

}