/**
 * 
 */
package com.toennies.ci1429.app.model.watcher;

/**
 * A system event. Indicates, that the state of a system has changed.
 * This type currently is only for forced separation from {@link IFaultEvent}s.
 * @author renkenh
 */
public interface ISystemEvent extends IWatchEvent
{
	//nothing - separate from IFaultEvent
}
