/**
 * 
 */
package com.toennies.ci1429.app.hw10.processing.events;

import com.toennies.ci1429.app.hw10.processing.HW10CommandDispatcher.Event;
import com.toennies.ci1429.app.model.DeviceResponse;

/**
 * This class allows {@link HW10ClientEventProcessor#process(Event)},
 * {@link HW10CommandEventProcessor#process(Event)}, and
 * {@link HW10ClientEventProcessor#process(Event)} to process the events
 * received. These classes need to implement this interface in order to process
 * the events.
 *
 * @author renkenh
 */
public interface IHW10EventProcessor
{

	/**
	 * This method is called from
	 * {@link HW10ClientEventProcessor#process(Event)},
	 * {@link HW10CommandEventProcessor#process(Event)}, and
	 * {@link HW10ClientEventProcessor#process(Event)} to process the events
	 * received.
	 */
	public DeviceResponse process(Event event);
}
