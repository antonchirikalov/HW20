/**
 * 
 */
package com.toennies.ci1429.app.network.event;

/**
 * Implement this interface if the type should notify others about events.
 * @author renkenh
 */
public interface IEventNotifier
{

	/**
	 * Registers a new handler for events pushed by this connector.
	 * @param handler The handler to register.
	 */
	public void registerEventHandler(IEventHandler handler);

	/**
	 * Unregisters a handler from events pushed by this connector. The method may be called several times, i.e.
	 * without the handler being registered.
	 * @param handler The handler to unregister.
	 */
	public void unregisterEventHandler(IEventHandler handler);

}
