package com.toennies.ci1429.app.hw10.processing.events;

import com.toennies.ci1429.app.hw10.processing.HW10CommandDispatcher.Event;
import com.toennies.ci1429.app.model.DeviceResponse;

/**
 *
 * @author renkenh
 */
public interface IHW10ClientCommandMatcher
{

	/**
	 * This method checks whether a given request matches the provided functions
	 */
	boolean matchesRequest(String request);

	/**
	 * Processes the request defined in the {@link Event} object.
	 * 
	 * @return a String containing the device response.
	 */
	DeviceResponse processRequest(Event request);
}