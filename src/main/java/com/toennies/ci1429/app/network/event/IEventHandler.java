package com.toennies.ci1429.app.network.event;

import com.toennies.ci1429.app.network.connector.IConnector;

/**
 * Type that describes an API to receive events send out by a connector.
 * @author renkenh
 */
public interface IEventHandler
{
	/**
	 * Called by the thread that originally called the connector to push an event to the handler.
	 * The handler must ensure the thread safety of his implementation!
	 * @param eventID The event ID. See {@link IConnector} for IDs and their description.
	 * @param params The parameters belonging to the given event. The parameters are immutable.
	 */
	public void handleEvent(String eventID, Object source, Object... params);
}