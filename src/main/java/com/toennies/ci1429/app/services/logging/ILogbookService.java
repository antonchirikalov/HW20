/**
 * 
 */
package com.toennies.ci1429.app.services.logging;

import java.util.Set;

import com.toennies.ci1429.app.services.logging.LogEvent.EventType;

/**
 * "Logbook" service that allows the management of sources for which to hold entries.
 * Sources are registered by their name. The implementation accesses an event bus. If
 * an event occurs on the event bus from a source with a known sourceID the event is saved in a fixed sized FIFO buffer.
 * @author renkenh
 */
public interface ILogbookService
{
	
	public static final String EVENT_NEW_LOGEVENT = "NEW_LOGEVENT";


	/** The maximum number of events that are hold for a source. */
	public static final int MAX_EVENTS_PER_QUEUE = 50;
	//	private static final Logger LOGGER = LogManager.getLogger();


	/** Register a new source by its name. */
	public void registerEventSource(String sourceUID);

	/** Get all known sourceIDs. */
	public Set<String> getEventSources();

	/** Unregister a sourceID. All events for this source are discarded. */
	public void unregisterEventSource(String sourceUID);


	public void logEvent(String system, String source, EventType type, String message);
	
	public void logEvent(LogEvent event);

	/** Returns all known latest events. */
	public LogEvent[] latestEvents();

	/** Returns the latest events for a given source. */
	public LogEvent[] latestEventsBySource(String sourceUID);
	
}
