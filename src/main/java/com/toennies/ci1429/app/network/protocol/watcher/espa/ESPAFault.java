/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.watcher.espa;

import java.time.Instant;

import com.toennies.ci1429.app.model.watcher.Fault.Severity;
import com.toennies.ci1429.app.model.watcher.IFaultEvent;
import com.toennies.ci1429.app.network.protocol.watcher.AWatcherProtocol;
import com.toennies.ci1429.app.network.protocol.watcher.AWatcherProtocol.Mode;
import com.toennies.ci1429.app.network.protocol.watcher.espa.data.ESPACall;

/**
 * Special implementation of the {@link IFaultEvent} interface that takes an ESPA call and transforms to the {@link IFaultEvent} API.
 * @author renkenh
 */
public class ESPAFault implements IFaultEvent
{

	private final String systemId;
	private final ESPACall call;
	private final boolean isStateless;
	private final Instant timestamp;


	/**
	 * Constructor.
	 * Same as calling {@link #ESPAFault(String, ESPACall, boolean)} with isStateless = <code>false</code>.
	 * @param The id of the system to which this call is related.
	 * @param The call.
	 */
	public ESPAFault(String systemId, ESPACall call)
	{
		this(systemId, call, false);
	}

	/**
	 * Constructor.
	 * If set to {@link Mode#STATELESS}, the {@link EventType#UP} is overridden with {@link EventType#INFO}.
	 * @param The id of the system to which this call is related.
	 * @param The call.
	 * @param isStateless Whether the client runs in {@link AWatcherProtocol.Mode#STATELESS} mode or not.
	 */
	public ESPAFault(String systemId, ESPACall call, boolean isStateless)
	{
		this.systemId = systemId;
		this.call = call;
		this.timestamp = Instant.now();
		this.isStateless = isStateless;
	}



	@Override
	public String getId()
	{
		return this.call.getMessage();
	}

	@Override
	public String getSystemId()
	{
		return this.systemId;
	}

	@Override
	public Severity getSeverity()
	{
		if (this.getType() == EventType.DOWN)
			return Severity.NONE;
		
		switch (this.call.getPriority())
		{
			case ALARM:
				return Severity.CRITICAL;
			case HIGH:
				return Severity.MAJOR;
			case NORMAL:
				return Severity.MINOR;
			default:
				return Severity.NONE;
		}
	}

	@Override
	public Instant getTimestamp()
	{
		return this.timestamp;
	}

	@Override
	public String getMessage()
	{
		return this.call.getMessage();
	}

//	@Override
//	public byte[] getRawData()
//	{
//		return this.call.getRawData();
//	}
//
	@Override
	public EventType getType()
	{
		if (this.isStateless)
			return EventType.INFO;

		switch (this.call.getCallType())
		{
			case STANDARD_CALL:
				return EventType.UP;
			case RESET_CALL:
				return EventType.DOWN;
			default:
				return EventType.INFO;
		}
	}

}

