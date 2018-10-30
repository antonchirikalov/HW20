/**
 * 
 */
package com.toennies.ci1429.app.network.event;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract implementation that does all the listener management. However, does not provide any method to publish events.
 * Must be implemented by the derived class or {@link EventNotifierStub} must be used.
 * @author renkenh
 */
public abstract class AEventNotifier implements IEventNotifier
{
	
	protected static final Logger LOGGER = LogManager.getLogger();


	private final ReentrantLock handlerLock = new ReentrantLock();
	private final Set<IEventHandler> handlers = Collections.synchronizedSet(new LinkedHashSet<>());

	
	/**
	 * Can be overwritten to customize source identifier in events.
	 * @return The source that is used when publishing events.
	 */
	protected Object getSource()
	{
		return this;
	}
	
	/**
	 * Allows thread safe sending of events to registered handlers.
	 * @param eventID The event ID.
	 * @param parameters The parameters for the event to send.
	 */
	protected void publishEvent(String eventID, Object... parameters)
	{
//		System.out.println("Publish:" + this.getClass().getName() + ":" + eventID);
		IEventHandler[] toProcess = new IEventHandler[0];
		this.handlerLock.lock();
		try
		{
			toProcess = this.handlers.stream().toArray(IEventHandler[]::new);
		}
		finally
		{
			this.handlerLock.unlock();
		}
		for (IEventHandler handler : toProcess)
		{
			try
			{
				handler.handleEvent(eventID, this.getSource(), parameters);
			}
			catch (Exception ex)
			{
				LOGGER.error("Handler {} threw an exception.", handler, ex);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerEventHandler(IEventHandler handler)
	{
		this.handlerLock.lock();
		try
		{
			this.handlers.add(handler);
		}
		finally
		{
			this.handlerLock.unlock();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unregisterEventHandler(IEventHandler handler)
	{
		this.handlerLock.lock();
		try
		{
			this.handlers.remove(handler);
		}
		finally
		{
			this.handlerLock.unlock();
		}
	}

}
