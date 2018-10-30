package com.toennies.ci1429.app.network.protocol.scale.hofelmeyer.requests;

import com.toennies.ci1429.app.model.scale.Commands;
import com.toennies.ci1429.app.network.protocol.scale.IHardwareRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RequestFactory
{
	protected static final Logger logger = LogManager.getLogger();

	public IHardwareRequest getRequest(Object... params)
	{
		Commands.Command command = (Commands.Command) params[0];
		logger.debug("Got Command {}", command);

		switch (command)
		{
			case TARE:
				return new SimpleRequest("TA");

			case TARE_WITH_VALUE:
				return new TareWithValueRequest(params[1]);

			case CLEAR_TARE:
				return new SimpleRequest("TC");

			case ZERO:
				return new SimpleRequest("SZ");

			case WEIGH:
				return new WeightDataRequest("RN");

			case WEIGH_AUTOMATIC:
				return new WeightDataRequest("RM");

			default:
				return null;
		}
	}
}
