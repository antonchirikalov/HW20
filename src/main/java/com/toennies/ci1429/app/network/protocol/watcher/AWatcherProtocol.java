/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.watcher;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.watcher.Fault.Severity;
import com.toennies.ci1429.app.model.watcher.IWatchEvent;
import com.toennies.ci1429.app.model.watcher.IWatchEvent.EventType;
import com.toennies.ci1429.app.model.watcher.Watcher;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.protocol.AProtocol;

/**
 * Abstract type representing a protocol for a {@link Watcher} device.
 * Does all the handling regarding the connection state (registering a disconnected connection as fault).
 * @author renkenh
 */
@Parameter(name=AWatcherProtocol.PARAM_NAME, isRequired=true, toolTip="The name of the monitor. Must be unique.")
@Parameter //dummy parameter
public abstract class AWatcherProtocol<OUT> extends AProtocol<IFlexibleConnector<OUT, OUT>, OUT, OUT>
{
	
	/** Specify in which mode this protocol should work. Not implemented at the moment. */
	public static final String PARAM_MODE = "mode";
	/** The name of the watcher. This name is also used as the global system id. */
	public static final String PARAM_NAME = "name";

	/** The name of the fault representing the connection state. */
	public static final String FAULT_CONNECTION_NAME = "Connection Fault";
	/** Event Id that has as payload an object of type {@link IWatchEvent}. */
	public static final String EVENT_WATCH_EVENT = "EVENT_WATCH_EVENT";

	
	/** The protocol can operate in two modes. Not implemented at the moment. */
	public enum Mode
	{
		/** Only {@link EventType#INFO} are send. That means, only the event protocol is available. */
		STATELESS,
		/**
		 * In this mode {@link EventType#UP}, {@link EventType#DOWN} events are send. At any point in time, the current
		 * state of the systems can be queried.
		 */
		STATEFUL
	}

	
	private String globalSystemId;


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(Map<String, String> parameters) throws IOException
	{
		super.setup(parameters);
		this.globalSystemId = this.config().getEntry(PARAM_NAME);
	}

	
//	@Override
//	public void handleEvent(String eventID, Object source, Object... params)
//	{
//		super.handleEvent(eventID, source, params);
//		IWatchEvent event = this.transformEvent(eventID, params);
//		if (event != null)
//			super.handleEvent(EVENT_WATCH_EVENT, this, event);
//	}
	
	private IWatchEvent transformEvent(String eventID, Object... params)
	{
		switch (eventID)
		{
			case IDevice.EVENT_STATE_CHANGED:
				boolean isConnected = this.isConnected();
				EventType type = !isConnected ? EventType.UP : EventType.DOWN;
				Severity severity = !isConnected ? Severity.CRITICAL : null;
				return new WatchFaultEvent(FAULT_CONNECTION_NAME, this.globalSystemId, type, severity, null);
		}
		return null;
	}


	@Override
	protected Object _send(Object... params) throws IOException, TimeoutException
	{
		//do nothing
		return null;
	}

}
