/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.watcher;

import java.time.Instant;

import com.toennies.ci1429.app.model.watcher.Fault.Severity;
import com.toennies.ci1429.app.model.watcher.IFaultEvent;

/**
 * Simple, immutable implementation of the {@link IFaultEvent} interface.
 * @author renkenh
 */
public class WatchFaultEvent implements IFaultEvent
{
	
	private final String systemId;
	private final EventType eventType;
	private final Instant timestamp;
	private final String message;
//	private final byte[] rawData;
	private final Severity severity;
	private final String faultId;


	/**
	 * Basic Constructor.
	 */
	public WatchFaultEvent(String faultId, String systemId, EventType eventType, Severity severity, String message)//, byte[] rawData)
	{
		this.systemId = systemId;
		this.eventType = eventType;
		this.timestamp = Instant.now();
		this.message = message;
//		this.rawData = rawData;
		this.severity = severity;
		this.faultId  = faultId;
	}


	@Override
	public String getId()
	{
		return this.faultId;
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
	public Severity getSeverity()
	{
		if (this.eventType == EventType.DOWN)
			return Severity.NONE;
		return this.severity;
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
