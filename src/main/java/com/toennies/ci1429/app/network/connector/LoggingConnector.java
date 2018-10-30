/**
 * 
 */
package com.toennies.ci1429.app.network.connector;

import com.toennies.ci1429.app.network.event.EventNotifierStub;
import com.toennies.ci1429.app.network.event.IEventHandler;
import com.toennies.ci1429.app.network.event.IEventNotifier;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.util.ASCII;

/**
 * A connector that is used to log every entity that passes through.
 * @author renkenh
 */
public class LoggingConnector<T> extends AWrappedConnector<T> implements IEventNotifier
{
	/**
	 * Returns when data has been send to the hardware device.
	 * The event usually has a byte[] or an {@link IMessage} object as payload.
	 */
	public static final String EVENT_DATA_SEND = "EVENT_DATA_SEND";
	/**
	 * Returns when data has been received from the hardware device.
	 * The event usually has a byte[] or an {@link IMessage} object as payload.
	 */
	public static final String EVENT_DATA_RECEIVED = "EVENT_DATA_RECEIVED";


	private final EventNotifierStub notifier = new EventNotifierStub(this);
	

	/**
	 * Constructor.
	 */
	public LoggingConnector(IConnector<T> connector)
	{
		super(connector);
	}


	@Override
	protected T transformToOut(T entity)
	{
		if (entity != null)
		{
			String print = (entity instanceof byte[]) ? ASCII.formatHuman((byte[]) entity) : String.valueOf(entity);
			logger.debug("[<--][{}]", print);
			this.notifier.publishEvent(EVENT_DATA_SEND, entity);
		}
		return super.transformToOut(entity);
	}
	
	@Override
	protected T transformToConIn(T entity)
	{
		if (entity != null)
		{
			String print = (entity instanceof byte[]) ? ASCII.formatHuman((byte[]) entity) : String.valueOf(entity);
			logger.debug("[-->][{}]", print);
			this.notifier.publishEvent(EVENT_DATA_RECEIVED, entity);
		}
		return super.transformToConIn(entity);
	}


	@Override
	public void registerEventHandler(IEventHandler handler)
	{
		this.notifier.registerEventHandler(handler);
	}

	@Override
	public void unregisterEventHandler(IEventHandler handler)
	{
		this.notifier.unregisterEventHandler(handler);
	}

}
