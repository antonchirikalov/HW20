/**
 * 
 */
package com.toennies.ci1429.app.services.logging;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.buffer.CircularFifoBuffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.spring.events.EventBus;

import com.toennies.ci1429.app.services.logging.LogEvent.EventType;
import com.toennies.ci1574.lib.helper.Generics;

/**
 * Implementation of the {@link ILogbookService} interface.
 * @author renkenh
 */
@Component
public class LogbookService implements ILogbookService
{
	
	@Autowired
	private EventBus.ApplicationEventBus eventbus;
	
	private ReentrantLock lock = new ReentrantLock();
	private final Buffer latest = new CircularFifoBuffer(ILogbookService.MAX_EVENTS_PER_QUEUE);
	private final HashMap<String, Buffer> bufferBySource = new HashMap<>();
		
	
	@Override
	public void registerEventSource(String sourceUID)
	{
		this.lock.lock();
		try
		{
			this.bufferBySource.put(sourceUID, new CircularFifoBuffer(ILogbookService.MAX_EVENTS_PER_QUEUE));
		}
		finally
		{
			this.lock.unlock();
		}
	}

	@Override
	public Set<String> getEventSources()
	{
		this.lock.lock();
		try
		{
			return new HashSet<>(this.bufferBySource.keySet());
		}
		finally
		{
			this.lock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void unregisterEventSource(String sourceUID)
	{
		this.lock.lock();
		try
		{
			Buffer buffer = this.bufferBySource.remove(sourceUID);
			if (buffer != null)
				this.latest.removeAll(buffer);
		}
		finally
		{
			this.lock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public LogEvent[] latestEventsBySource(String sourceID)
	{
		this.lock.lock();
		try
		{
			Buffer buffer = this.bufferBySource.get(sourceID);
			if (buffer == null)
				return null;
			return Generics.convertUnchecked(buffer.stream().toArray(LogEvent[]::new));
		}
		finally
		{
			this.lock.unlock();
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public LogEvent[] latestEvents()
	{
		this.lock.lock();
		try
		{
			return Generics.convertUnchecked(this.latest.stream().toArray(LogEvent[]::new));
		}
		finally
		{
			this.lock.unlock();
		}
	}

	public void logEvent(String system, String source, EventType type, String message)
	{
		this.logEvent(new LogEvent(system, source, type, message));
	}
	
	@SuppressWarnings("unchecked")
	public void logEvent(LogEvent event)
	{
		this.lock.lock();
		try
		{
			this.latest.add(event);
			Buffer buffer = this.bufferBySource.get(event.getSourceUID());
			if (buffer != null)
				buffer.add(event);
		}
		finally
		{
			this.lock.unlock();
		}
		this.eventbus.publish(ILogbookService.EVENT_NEW_LOGEVENT, this, event);
	}

}

