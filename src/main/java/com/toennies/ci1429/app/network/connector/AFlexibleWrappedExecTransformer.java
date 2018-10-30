/**
 * 
 */
package com.toennies.ci1429.app.network.connector;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.ReentrantLock;

import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.util.IExecutors;


/**
 * Extends the {@link AFlexibleWrapperTransformer} with scheduling functionality. This allows additional processes to run on the
 * network stack (e.g. automatic reconnect).
 * @author renkenh
 */
public abstract class AFlexibleWrappedExecTransformer<IN, OUT, CON_IN, CON_OUT> extends AFlexibleWrapperTransformer<IN, OUT, CON_IN, CON_OUT>
{
	
	private final ReentrantLock lock = new ReentrantLock();
	private ScheduledExecutorService executor;


	/**
	 * Constructor
	 */
	public AFlexibleWrappedExecTransformer(IFlexibleConnector<CON_IN, CON_OUT> connector)
	{
		super(connector);
	}

	
	/**
	 * Is called during {@link #connect(IConfigContainer)}. Use the executor service to
	 * schedule tasks. The scheduler is re-created (new) on every call of {@link #connect(IConfigContainer)}. 
	 * @param service The service to execute tasks on. 
	 */
	protected abstract void schedule(ScheduledExecutorService service);

	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		this.lock.lock();
		try
		{
			//re!start executor
			this.shutdownExecute();
			this.executor = Executors.newSingleThreadScheduledExecutor(IExecutors.NETWORK_FACTORY);
		}
		finally
		{
			this.lock.unlock();
		}
		this.schedule(this.executor);
		super.connect(config);
	}
	
	@Override
	public void shutdown()
	{
		this.shutdownExecute();
		super.shutdown();
	}

	/**
	 * Shuts down the current executor (if available).
	 */
	protected void shutdownExecute()
	{
		this.lock.lock();
		try
		{
			if (this.executor != null)
				this.executor.shutdownNow();
			this.executor = null;
		}
		finally
		{
			this.lock.unlock();
		}
	}
	
	@Override
	protected void finalize()
	{
		this.shutdownExecute();
	}

}
