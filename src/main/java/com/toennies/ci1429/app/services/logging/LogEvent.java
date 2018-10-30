/**
 * 
 */
package com.toennies.ci1429.app.services.logging;

import java.time.Instant;

/**
 * @author stenzelk
 *
 */
public class LogEvent
{
	
	public enum EventType
	{
		INFO,
		IN,
		OUT,
		ERROR
	}
	

	public final String system;
    public final String sourceID;
    public final EventType type;
    public final Instant timestamp;
    public final String payload;


	public LogEvent(String system, String sourceID, EventType type, String payload)
	{
		this(system, sourceID, type, Instant.now(), payload);
	}
	
	public LogEvent(String system, String sourceID, EventType type, Instant timestamp, String payload)
	{
		this.system = system;
		this.sourceID = sourceID;
		this.type = type;
		this.timestamp = timestamp;
		this.payload = payload;
	}
	
	
	public String getSourceUID()
	{
		return this.system + "." + this.sourceID;
	}

}
