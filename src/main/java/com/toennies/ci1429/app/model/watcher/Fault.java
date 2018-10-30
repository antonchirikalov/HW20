/**
 * 
 */
package com.toennies.ci1429.app.model.watcher;

import java.time.Instant;
import java.util.Comparator;

/**
 * Represents a fault in an {@link ISystem}.
 * @author renkenh
 */
public class Fault
{
	
	static final Comparator<Fault> CHRONOLOGICAL_DESC = (f1, f2) -> -1 * f1.getTimestamp().compareTo(f2.getTimestamp());


	public enum Severity
	{
		CRITICAL,
		MAJOR,
		MINOR,
		NONE
	}

	
	private final Severity severity;
	private final String id;
	private final String message;
	private final Instant timestamp;


	/**
	 * Copy Constructor.
	 */
	Fault(Fault fault, ISystem system)
	{
		this(system.getName() + "." + fault.getId(), fault.getSeverity(), fault.getMessage(), fault.getTimestamp());
	}

	/**
	 * Constructor. Creates a new fault.
	 * @param id A unique ID for this fault. The id helps to identify the fault in a system (e.g. to remove it later on).
	 * @param severity The severity of the fault.
	 * @param message A (usually) human readable message. May be <code>null</code>.
	 * @param timestamp The timestamp when this fault occurred.
	 */
	public Fault(String id, Severity severity, String message, Instant timestamp)
	{
		this.id = id;
		this.severity = severity;
		this.message = message;
		this.timestamp = timestamp;
	}

	
	/**
	 * @return A unique ID for this fault. The id helps to identify the fault in a system (e.g. to remove it later on).
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 * @return The severity. Not <code>null</code>
	 */
	public Severity getSeverity()
	{
		return severity;
	}

	/**
	 * @return A (usually) human readable message. May be <code>null</code>.
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * @return point in time when this fault started/occurred. Not <code>null</code>.
	 */
	public Instant getTimestamp()
	{
		return timestamp;
	}

}
