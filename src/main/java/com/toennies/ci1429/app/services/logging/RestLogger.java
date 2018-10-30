package com.toennies.ci1429.app.services.logging;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.toennies.ci1429.app.network.event.IEventHandler;
import com.toennies.ci1429.app.restcontroller.ADeviceSpecificRestController;
import com.toennies.ci1429.app.services.logging.LogEvent.EventType;
import com.toennies.ci1429.app.util.LogbookUtil;

@Component
public class RestLogger implements IEventHandler
{

	private final static String SYSTEM_ID = "Rest";


	@Autowired
	private ILogbookService logbook;

	@PostConstruct
	private void init()
	{
		this.logbook.registerEventSource(SYSTEM_ID);
	}


	@Override
	public void handleEvent(String eventID, Object source, Object... params)
	{
		switch (eventID)
		{
			case ADeviceSpecificRestController.EVENT_REQUEST_RECEIVED:
				Object[] param = (Object[]) params;
				this.logbook.logEvent(SYSTEM_ID, String.valueOf(source), EventType.IN, LogbookUtil.convertToString(param));
				break;
			case ADeviceSpecificRestController.EVENT_BATCH_REQUEST_RECEIVED:
				this.logbook.logEvent(SYSTEM_ID, String.valueOf(source), EventType.IN, "("+params[0]+")" + LogbookUtil.convertToString(params[1]));
				break;
			case ADeviceSpecificRestController.EVENT_RESPONSE_SEND:
				this.logbook.logEvent(SYSTEM_ID, String.valueOf(source), EventType.OUT, LogbookUtil.convertToString(params[0]));
				break;
		}
	}
	
}
