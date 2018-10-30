/**
 * 
 */
package com.toennies.ci1429.app.network.connector;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.network.event.EventNotifierStub;
import com.toennies.ci1429.app.network.event.IEventHandler;
import com.toennies.ci1429.app.network.event.IEventNotifier;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;

/**
 * Connector that occasionally tries to reconnect to the hardware (if disconnected).
 * @author renkenh
 */
public class FlexibleReConnector<IN, OUT> extends AFlexibleWrappedExecTransformer<IN, OUT, IN, OUT> implements IEventNotifier
{
	
	private class Reconnect implements Runnable
	{
		@Override
		public void run()
		{
			//needed to have a (more or less) atomic operation
			IConfigContainer config = FlexibleReConnector.this.config;
			if (config != null && !FlexibleReConnector.super.isConnected())
			{
				try
				{
					FlexibleReConnector.this.connector.connect(config);
				}
				catch (IOException e)
				{
					logger.error("Could not connect to device: {}.", e.getMessage());
					FlexibleReConnector.this.notifier.publishEvent(IDevice.EVENT_ERROR_OCCURRED, "Could not connect to device. Retrying.");
				}
			}
			FlexibleReConnector.this.notifier.publishEvent(IDevice.EVENT_STATE_CHANGED);
		}

	}


	private final EventNotifierStub notifier = new EventNotifierStub(this);

	private volatile IConfigContainer config;


	/**
	 * Constructor.
	 */
	public FlexibleReConnector(IFlexibleConnector<IN, OUT> connector)
	{
		super(connector);
	}


	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		super.connect(config);
		this.config = config;
	}

	@Override
	protected void schedule(ScheduledExecutorService service)
	{
		service.scheduleWithFixedDelay(new Reconnect(), 1, 5, TimeUnit.SECONDS);
	}


	@Override
	public boolean isConnected()
	{
		return this.config != null && super.isConnected();
	}

	@Override
	protected void shutdownExecute()
	{
		this.config = null;
		super.shutdownExecute();
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


	@Override
	protected OUT transformToOut(OUT entity)
	{
		return entity;
	}

	@Override
	protected IN transformToConIn(IN entity)
	{
		return entity;
	}

}
