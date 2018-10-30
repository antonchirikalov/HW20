/**
 * 
 */
package com.toennies.ci1429.app.model.watcher;

import java.time.Instant;
import java.util.List;

/**
 * Simple structure to represent a watched system.
 * @author renkenh
 */
public interface ISystem
{

	public enum SystemState
	{
		/** The system is active an running. */
		ACTIVE,
		/** The system is not shutdown, but faulty. {@link ISystem#getCurrentFaults()} returns at least one fault. */
		FAULTY,
		/** The system is shutdown. */
		SHUTDOWN
	}
	
	
	/**
	 * @return A unique id for this system. Within a hierarchy of systems, this uniquely identifies the system.
	 */
	public String getId();

	/**
	 * @return The name of the system. Is unique among its siblings only.
	 */
	public String getName();

	/**
	 * If at least one {@link Fault} is active, {@link #getSystemState()} returns {@link SystemState#FAULTY}.
	 * @return The current system state.
	 */
	public SystemState getSystemState();

	/**
	 * @return A list of faults currently active.
	 */
	public List<Fault> getCurrentFaults();

	/**
	 * @return Timestamp when the last change (fault, state change) was registered.
	 */
	public Instant lastChange();

	/**
	 * @return A message that describes the last change.
	 */
	public String lastMessage();

	/**
	 * @return Creates an immutable copy of the current system state.
	 */
	public ISystem createSnapshot();

}
