/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.sps.bohrer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.toennies.ci1429.app.network.connector.AWrappedExecConnector;
import com.toennies.ci1429.app.network.connector.IConnector;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.protocol.sps.telegram.Telegram;

/**
 * @author renkenh
 *
 */
public class InfoCollector extends AWrappedExecConnector<Telegram> implements IConnector<Telegram>
{
	
	private class Repop implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				if (!InfoCollector.this.isConnected())
					return;
				Object event = InfoCollector.super.poll();
				while (event != null)
				{
					InfoCollector.this.handleEvent(event);
					event = InfoCollector.super.poll();
				}
			}
			catch (IOException ex)
			{
				logger.error("Could not connect to device.", ex);
				InfoCollector.this.handleEvent(ex);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	

	private final LinkedBlockingDeque<BohrerInfo> infos = new LinkedBlockingDeque<>();
	private final LinkedBlockingDeque<Object> others = new LinkedBlockingDeque<>();
	private ScheduledExecutorService executor;
	private int timeout;

	
	/**
	 * @param connector
	 */
	public InfoCollector(IConnector<Telegram> connector)
	{
		super(connector);
	}


	@Override
	protected void schedule(ScheduledExecutorService service)
	{
		this.executor = service;
		this.executor.scheduleAtFixedRate(new Repop(), 5, 2, TimeUnit.SECONDS);
	}

	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		super.connect(config);
		this.timeout = config.getIntEntry(PARAM_TIMEOUT);
	}

	@Override
	public Telegram poll() throws IOException
	{
		Object event = this.others.pollFirst();
		if (event instanceof IOException)
			throw (IOException) event;
		if (event instanceof Exception)
			throw new IOException((Exception) event);
		return (Telegram) event;
	}

	@Override
	public Telegram pop() throws IOException, TimeoutException
	{
		try
		{
			Object event = this.others.pollFirst(this.timeout, TimeUnit.MILLISECONDS);
			if (event == null)
				throw new TimeoutException("Could not get data from SPS in time.");
			if (event instanceof IOException)
				throw (IOException) event;
			if (event instanceof Exception)
				throw new IOException((Exception) event);
			return (Telegram) event;
		}
		catch (InterruptedException ex)
		{
			throw new TimeoutException("Interrupted.");
		}
	}

	public List<BohrerInfo> pollAvailableInfos()
	{
		ArrayList<BohrerInfo> list = new ArrayList<>();
		this.infos.drainTo(list);
		return list;
	}
	
	private void handleEvent(Object event)
	{
		if (event instanceof BohrerInfo)
			this.infos.add((BohrerInfo) event);
		else
			this.others.add(event);
	}
}
