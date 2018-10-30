package com.toennies.ci1429.app.network.protocol.watcher;

import java.time.Instant;

import com.toennies.ci1429.app.model.watcher.ISystemEvent;

/**
 * Simple immutable implementation of the {@link ISystemEvent} type.
 * @author renkenh
 */
public class WatchSystemEvent implements ISystemEvent
{
	
	private final String systemId;
	private final EventType eventType;
	private final Instant timestamp;
	private final String message;
//	private final byte[] rawData;


	/**
	 * Basic constructor.
	 */
	public WatchSystemEvent(String systemId, EventType eventType, Instant timestamp, String message)//, byte[] rawData)
	{
		this.systemId = systemId;
		this.eventType = eventType;
		this.timestamp = timestamp;
		this.message = message;
//		this.rawData = rawData;
	}


	@Override
	public String getSystemId()
	{
		return this.systemId;
	}

	@Override
	public EventType getType()
	{
		return this.eventType;
	}

	@Override
	public Instant getTimestamp()
	{
		return this.timestamp;
	}

	@Override
	public String getMessage()
	{
		return this.message;
	}

//	@Override
//	public byte[] getRawData()
//	{
//		return this.rawData;
//	}

}
