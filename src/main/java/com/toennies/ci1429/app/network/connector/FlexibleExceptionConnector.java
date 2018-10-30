/**
 * 
 */
package com.toennies.ci1429.app.network.connector;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.network.event.EventNotifierStub;
import com.toennies.ci1429.app.network.event.IEventHandler;
import com.toennies.ci1429.app.network.event.IEventNotifier;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;

/**
 * A connector that is used to log every entity that passes through.
 * 
 * @author renkenh
 */
public class FlexibleExceptionConnector<IN, OUT> extends AFlexibleWrapperTransformer<IN, OUT, IN, OUT>
		implements IEventNotifier
{

	public static final String EVENT_ERROR_OCCURRED = IDevice.EVENT_ERROR_OCCURRED;

	private final EventNotifierStub notifier = new EventNotifierStub(this);
	private volatile boolean isConnected = false;

	/**
	 * Constructor.
	 */
	public FlexibleExceptionConnector(IFlexibleConnector<IN, OUT> connector)
	{
		super(connector);
	}

	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		this.<Void>handleIO(() ->
		{
			super.connect(config);
			this.isConnected = true;
			return null;
		});
	}

	private <RETURN> RETURN handleIO(Callable<RETURN> call) throws IOException
	{
		try
		{
			return this.handleIOTO(call);
		}
		catch (TimeoutException e)
		{
			throw new RuntimeException(e);
		}
	}

	private <RETURN> RETURN handleIOTO(Callable<RETURN> call) throws IOException, TimeoutException
	{
		try
		{
			return call.call();
		}
		catch (IOException ex)
		{
			this.isConnected = false;
			this.notifier.publishEvent(IDevice.EVENT_STATE_CHANGED);
			throw (IOException) ex;
		}
		catch (TimeoutException ex)
		{
			this.isConnected = false;
			this.notifier.publishEvent(IDevice.EVENT_STATE_CHANGED);
			throw (TimeoutException) ex;
		}
		catch (NullPointerException exc)
		{
			return null;
		}
		catch (Exception ex)
		{
			this.isConnected = false;
			this.notifier.publishEvent(IDevice.EVENT_STATE_CHANGED);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public boolean isConnected()
	{
		return this.isConnected && super.isConnected();
	}

	@Override
	public OUT poll() throws IOException
	{
		return this.handleIO(super::poll);
	}

	@Override
	public OUT pop() throws IOException, TimeoutException
	{
		return this.handleIOTO(super::pop);
	}

	@Override
	public void push(IN entity) throws IOException
	{
		this.<Void>handleIO(() ->
		{
			super.push(entity);
			return null;
		});
	}

	@Override
	public void disconnect() throws IOException
	{
		this.<Void>handleIO(() ->
		{
			super.disconnect();
			return null;
		});
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
