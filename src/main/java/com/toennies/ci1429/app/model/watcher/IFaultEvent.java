/**
 * 
 */
package com.toennies.ci1429.app.model.watcher;

import com.toennies.ci1429.app.model.watcher.Fault.Severity;

/**
 * Represents a change on a fault. The fault can occur {@link #getType()} equals to {@value EventType#UP}, additional information
 * can be published ({@value EventType#INFO}), or the fault can be resolved ({@value EventType#DOWN}). 
 * @author renkenh
 */
public interface IFaultEvent extends IWatchEvent
{

	/**
	 * Returns a name for the fault. The name is used to connect different events for this fault together, e.g.
	 * to detect corresponding UP, DOWN events.
	 * @return A unique name of this fault.
	 */
	public String getId();
	
	/**
	 * The severity of the event. If the severity is not applicable (e.g. on {@value EventType#DOWN} events for faults) 
	 * or unknown (because parsing failed) {@value Severity#NONE} is returned.
	 * @return The severity
	 */
	public Severity getSeverity();

}
