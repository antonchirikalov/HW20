/**
 * 
 */
package com.toennies.ci1429.app.model.watcher;

import java.time.Instant;
import java.util.Comparator;

/**
 * Common Event type. Describes an event happened on a watched {@link ISystem}.
 * From this {@link IFaultEvent}s and {@link ISystemEvent}s are derived.
 * @author renkenh
 */
public interface IWatchEvent
{

	/** Comparator for chronological ordering. */
	static final Comparator<IWatchEvent> CHRONOLOGICAL_DESC = (f1, f2) -> -1 * f1.getTimestamp().compareTo(f2.getTimestamp());


	/** The type of a single event. */
	public enum EventType
	{
		/** The event describes an activation of some sort (e.g. a new fault occurred). */
		UP,
		/** The event describes an info for a specific system (or fault). */
		INFO,
		/** The event describes a deactivation (e.g. a fault has been resolved). */
		DOWN
	}

	
	/**
	 * @return The id of the system on which this event occurred. Never <code>null</code>.
	 */
	public String getSystemId();

	/**
	 * @return The type of the event.
	 */
	public EventType getType();

	/**
	 * The timestamp when the event happened.
	 * @return The timestamp.
	 */
	public Instant getTimestamp();
	
	/**
	 * A parsed message of the event. That can be a specific extracted message or a protocol based constant that the client is
	 * able to understand. <code>null</code> is returned when parsing is not implemented for the watched device.
	 * @return A parsed message or <code>null</code>.
	 */
	public String getMessage();
	
//	/**
//	 * The raw data of the event. If the standard interface does not return enough information the client can parse the data
//	 * by itself.
//	 * @return The raw data. If the original data is string based, the string is returned in UTF-8 format.
//	 */
//	public byte[] getRawData();
	
}
