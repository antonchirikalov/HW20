package com.toennies.ci1429.app.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import com.toennies.ci1429.app.services.devices.DevicesService;

/**
 * This class is notified when spring container gets destroyed. Some cleanup is
 * done here.
 */
@Component
public class ContextClosedNotifier implements ApplicationListener<ContextClosedEvent> {

	@Autowired
	private DevicesService devicesService;

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		devicesService.shutdownService(); // Without this: memory leak!
//		ExecutorFactory.onContextClosedEvent(event); //not needed anymore - since all executors spawn deamon threads only 
	}

}
