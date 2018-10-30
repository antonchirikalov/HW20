/**
 * 
 */
package com.toennies.ci1429.app.hw10.processing;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.toennies.ci1429.app.hw10.client.HW10Client;
import com.toennies.ci1429.app.hw10.processing.events.HW10ClientEventProcessor;
import com.toennies.ci1429.app.hw10.processing.events.HW10CommandEventProcessor;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.util.IExecutors;

/**
 * Implementation to processes all events received from client(s). It is used to
 * dispatch and control all received commands {@link Event#call()}.
 * <p>
 * Three events are defined: {@link HW10EventType#NEW_CLIENT} is invoked when
 * new client is connected to server.
 * <p>
 * {@link HW10EventType#NEW_COMMAND} is invoked when new command is received
 * from client(s).
 * <p>
 * {@link HW10EventType#CLIENT_SHUTDOWN} is invoked when shutdown event is
 * received in {@link HW10ClientEventProcessor#process(Event)} and stop all
 * executive task in {@link HW10Client#shutdown()}.
 * 
 * @author renkenh
 */
@Component
public class HW10CommandDispatcher
{
	
	private static final Logger logger = LogManager.getLogger();


	public enum HW10EventType
	{
		/**
		 * Offered when a new client connects. Source is the HW10Client object.
		 * No Parameters.
		 */
		NEW_CLIENT,
		/**
		 * Offered when a new command is send by a client. Source is the
		 * HW10Client object. One Parameter: The command as a string
		 */
		NEW_COMMAND,
		/**
		 * Offered when a client disconnects. Source is the HW10Client object.
		 * No Parameters.
		 */
		CLIENT_SHUTDOWN
	}

	public final class Event implements Callable<DeviceResponse>
	{
		public final HW10EventType eventType;
		public final Object source;
		public final Object[] parameters;

		public Event(HW10EventType eventType, Object source, Object... parameters)
		{
			this.eventType = eventType;
			this.source = source;
			this.parameters = parameters;
		}

		@Override
		public DeviceResponse call()
		{
			// process events
			try
			{
				switch (this.eventType)
				{
					case CLIENT_SHUTDOWN:
						return HW10CommandDispatcher.this.newClientEventProcessor.process(this);
					case NEW_CLIENT:
						return HW10CommandDispatcher.this.newClientEventProcessor.process(this);
					case NEW_COMMAND:
						return HW10CommandDispatcher.this.commandEventProcessor.process(this);
					default:
						return null;
				}
			}
			finally
			{
				semaphore.release();
			}
		}
	}

	/** Maximum number of events are arbitrary chosen {@link #MAX_EVENTS} */
	private static final int MAX_EVENTS = 100;

	/** {@link #TIMEOUT} set to retrieve clients and devices response. */
	private static final int TIMEOUT = 60000;

	@Autowired
	private HW10ClientEventProcessor newClientEventProcessor;

	@Autowired
	private HW10CommandEventProcessor commandEventProcessor;

	/**
	 * A counting {@link #Semaphore} maintains a set of permits i.e.,
	 * {@link #MAX_EVENTS} at a time.
	 */
	private final Semaphore semaphore = new Semaphore(MAX_EVENTS);

	/**
	 * An {@link #executor} provides methods that can produce a Future
	 * {@link #offer(HW10EventType, Object, Object...)} for tracking progress of
	 * asynchronous task(s). It provides methods to manage their termination
	 * {@link #stop()}.
	 */
	private final ExecutorService executor = Executors.newSingleThreadExecutor(IExecutors.NETWORK_FACTORY);

	public DeviceResponse offer(HW10EventType eventType, Object source, Object... parameters)
	{
		if (this.executor.isShutdown())
			return null;

		try
		{
			this.semaphore.tryAcquire(TIMEOUT, TimeUnit.MILLISECONDS);
			Future<DeviceResponse> future = this.executor.submit(new Event(eventType, source, parameters));
			return future.get(TIMEOUT, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException e)
		{
			return new DeviceResponse(Status.BAD_DEVICE_RESPONSE, "Device busy.");
		}
		catch (TimeoutException ex)
		{
			return new DeviceResponse(Status.BAD_DEVICE_RESPONSE, "Device busy.");
		}
		catch (Exception e)
		{
			logger.error("Unexpected Error: ", e);
			return new DeviceResponse(Status.BAD_SERVER, "Got exception while processing data: " + e.getClass() + ":" + e.getMessage());
		}
	}

	/**
	 * This method attempts to stop executive task(s)
	 * {@link ExecutorService#isShutdown()}.
	 * <p>
	 * Initiates an orderly shutdown in which previously submitted tasks are
	 * executed, but no new tasks will be accepted.
	 */
	public synchronized void stop()
	{
		this.executor.shutdown();
	}

	/**
	 * This method invokes the {@link ExecutorService#stop()} to stop exectuive
	 * tasks(s).
	 */
	protected void finalize()
	{
		this.stop();
	}
}