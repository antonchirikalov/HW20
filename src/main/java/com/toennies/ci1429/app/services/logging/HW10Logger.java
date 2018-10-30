/**
 * 
 */
package com.toennies.ci1429.app.services.logging;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.spring.events.EventBus;

import com.toennies.ci1429.app.hw10.client.HW10Client;
import com.toennies.ci1429.app.hw10.processing.events.HW10ClientEventProcessor;
import com.toennies.ci1429.app.network.event.IEventHandler;
import com.toennies.ci1429.app.services.logging.LogEvent.EventType;
import com.toennies.ci1429.app.util.LogbookUtil;

/**
 * @author stenzelk
 *
 */
@Component
public class HW10Logger implements IEventHandler
{

	private static final String SYSTEM_ID = "HW10";


	@Autowired
	private HW10ClientEventProcessor clientProcessor;
	@Autowired
	private ILogbookService logbook;
	@Autowired
	private EventBus.ApplicationEventBus eventBus;

	
	@PostConstruct
	private void init()
	{
		this.clientProcessor.registerEventHandler(this);
	}



	@Override
	public void handleEvent(String eventID, Object source, Object... params)
	{
		switch (eventID)
		{
			case HW10ClientEventProcessor.EVENT_NEW_CLIENT:
				HW10Client client = (HW10Client) params[0];
				this.logbook.registerEventSource(SYSTEM_ID + "." + client.getID());
				this.logbook.logEvent(SYSTEM_ID, client.getID(), EventType.INFO, client.getExe() + " CONNECTED");
				client.registerEventHandler(this);
				break;
			case HW10ClientEventProcessor.EVENT_CLIENT_SHUTDOWN:
				client = (HW10Client) params[0];
				client.unregisterEventHandler(this);
				this.logbook.logEvent(SYSTEM_ID, client.getID(), EventType.INFO, client.getExe() + " DISCONNECTED");
				this.logbook.unregisterEventSource(SYSTEM_ID + "." + client.getID());
				break;
			case HW10Client.EVENT_DATA_RECEIVED:
				client = (HW10Client) source;
				this.logbook.logEvent(SYSTEM_ID, client.getID(), EventType.IN, LogbookUtil.convertToString(params[0]));
				break;
			case HW10Client.EVENT_DATA_SEND:
				client = (HW10Client) source;
				this.logbook.logEvent(SYSTEM_ID, client.getID(), EventType.OUT, LogbookUtil.convertToString(params[0]));
				break;
		}
		this.eventBus.publish(eventID, source, params[0]);
	}
	
}
