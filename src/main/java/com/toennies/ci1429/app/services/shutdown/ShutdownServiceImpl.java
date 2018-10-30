package com.toennies.ci1429.app.services.shutdown;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ShutdownServiceImpl implements ShutdownService {

	private final static Logger logger = LogManager.getLogger();

	@Autowired
	private ApplicationContext context;

	@Override
	public void shutdown() {
		logger.debug("Trying to shutdown ApplicationContext via gui");
		if (context instanceof AbstractApplicationContext) {
			Thread thread = new Thread(() -> ((AbstractApplicationContext) context).close());
			thread.setDaemon(true);
			thread.start();
		} else {
			logger.error("Can't shutdown ApplicationContext");
		}
	}

}
