/**
 * 
 */
package com.toennies.ci1429.app.network.connector;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import com.toennies.ci1429.app.network.parameter.IConfigContainer;

/**
 * Different version of the health check that simply sends a prepared message (if connected) and 
 * validates the response. Disconnects the network stack if the expected response does not return
 * within a certain amount of time.
 * Can be controlled through {@link #PARAM_HEALTHCHECK}.
 * @author renkenh
 */
public class FlexibleTimedHealthCheckTransformer<IN, OUT> extends AFlexibleWrappedExecTransformer<IN, OUT, IN, OUT>
{
	
	/** Parameter to set the schedule of the health check. A value lower than 1 disables the health check altogether. */
	public static final String PARAM_HEALTHCHECK = "healthcheck";


	/**
	 * Plugin interface. Used by this health check implementation.
	 * @param <T> The type of the request and the response.
	 */
	public interface ITimedHealthCheck<IN, OUT>
	{
		
		/**
		 * Returns the entity to be send through the network stack.
		 * @return An entity to send.
		 */
		public OUT healthcheckMessage();

		/**
		 * Is called whenever an entity passes through the associated {@link FlexibleTimedHealthCheckConnector}.
		 * If the response is a valid health check the message is discarded by the underlying connector.
		 * @param response The response to check.
		 * @return <code>true</code> if the response is a valid health check, <code>false</code> otherwise.
		 */
		public boolean validateResponse(IN response);
	}


	private class HealthCheckRunnable implements Runnable
	{
		
		
		@Override
		public void run()
		{
//			System.out.println("Started Healthcheck");
			if (!FlexibleTimedHealthCheckTransformer.super.isConnected())
				return;
			
			try
			{
//				System.out.println("Pushed Healthcheck");
				FlexibleTimedHealthCheckTransformer.super.connector.push(healthChecker.healthcheckMessage());
			}
			catch (IOException e)
			{
				logger.error("Could not connect to device.", e);
			}
		}

	}

	private final HealthCheckRunnable healthCheckRunnable;
	private int healthCheckMS = 0;	//millisec
	private final ReentrantLock lastCheckLock = new ReentrantLock();
	private Instant lastCheck = null;
	private ITimedHealthCheck<OUT, IN> healthChecker;


	/**
	 * 
	 */
	public FlexibleTimedHealthCheckTransformer(IFlexibleConnector<IN, OUT> connector, ITimedHealthCheck<OUT, IN> healthCheck)
	{
		super(connector);
		this.healthCheckRunnable = new HealthCheckRunnable();
		this.healthChecker = healthCheck;
	}

	
	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		this.healthCheckMS = config.getIntEntry(PARAM_HEALTHCHECK);
		this.checkedSuccessful();
		super.connect(config);
	}
	
	
	@Override
	public OUT poll() throws IOException
	{
		OUT polled = super.connector.poll();
		while (this.healthChecker.validateResponse(polled))
		{
			this.checkedSuccessful();
			polled = super.connector.poll();
		}
		return this.transformToOut(polled);
	}

	@Override
	public OUT pop() throws IOException, TimeoutException
	{
		OUT popped = super.connector.pop();
		while (this.healthChecker.validateResponse(popped))
		{
			this.checkedSuccessful();
			popped = super.connector.pop();
		}
		return this.transformToOut(popped);
	}


	private void checkedSuccessful()
	{
		this.lastCheckLock.lock();
		try
		{
			this.lastCheck = Instant.now();
		}
		finally
		{
			this.lastCheckLock.unlock();
		}
	}
	
	private boolean checkHealth()
	{
		this.lastCheckLock.lock();
		try
		{
			if (this.healthCheckMS <= 0)
				return true;
			if (this.lastCheck == null)
				return false;
			Duration timeout = Duration.of((int) (this.healthCheckMS * 1.5f), ChronoUnit.MILLIS);
			return Duration.between(Instant.now(), this.lastCheck).abs().compareTo(timeout) <= 0;
		}
		finally
		{
			this.lastCheckLock.unlock();
		}
	}

	@Override
	public boolean isConnected()
	{
		return this.checkHealth() && super.isConnected();
	}

	@Override
	protected void schedule(ScheduledExecutorService service)
	{
		if (this.healthCheckMS > 0)
			service.scheduleWithFixedDelay(this.healthCheckRunnable, this.healthCheckMS, this.healthCheckMS, TimeUnit.MILLISECONDS);
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
