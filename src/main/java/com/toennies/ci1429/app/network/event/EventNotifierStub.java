/**
 * 
 */
package com.toennies.ci1429.app.network.event;

/**
 * Simple implementation of the {@link IEventNotifier} interface.
 * @author renkenh
 */
public class EventNotifierStub extends AEventNotifier
{
	
	private final Object source;


	public EventNotifierStub(Object source)
	{
		this.source = source;
	}
	
	
	@Override
	protected Object getSource()
	{
		return this.source;
	}
	
	/**
	 * Method to publish an event.
	 */
	public final void publishEvent(String eventID, Object... parameters)
	{
		super.publishEvent(eventID, parameters);
	}

}
