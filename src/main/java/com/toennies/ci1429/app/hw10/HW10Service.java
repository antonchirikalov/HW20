package com.toennies.ci1429.app.hw10;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.toennies.ci1429.app.hw10.client.HW10Server;

/**
 * Implementation to initialize HW1.0 Service {@link HW10Server#start()} and
 * stop HW1.0 Service {@link HW10Server#stop()} .
 * 
 * @author renkenh
 */
@Component
public class HW10Service implements ApplicationListener<ApplicationContextEvent>
{

	private static final Logger logger = LogManager.getLogger();

	@Autowired
	private HW10Server server;

	@Override
	public void onApplicationEvent(ApplicationContextEvent event)
	{
		if (event instanceof ContextRefreshedEvent)
		{
			server.start();
			logger.debug("HW1.0 Service started!");
		}
		else if (event instanceof ContextClosedEvent)
		{
			server.stop();
			logger.debug("HW1.0 Service has been closed!");
		}
	}
}