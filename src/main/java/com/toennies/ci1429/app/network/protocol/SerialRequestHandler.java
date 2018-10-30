/**
 * 
 */
package com.toennies.ci1429.app.network.protocol;

import java.io.IOException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.util.Compute;
import com.toennies.ci1429.app.util.IExecutors;

/**
 * @author renkenh
 *
 */
public class SerialRequestHandler<P extends AProtocol<?, ?, ?>>
{
	
	private static final Logger logger = LogManager.getLogger();


	@FunctionalInterface
	public interface ISend
	{
		public Object send(Object[] params) throws IOException, TimeoutException;
	}
	
	
	private final class Request
	{
		private final Compute compute = new Compute();
		private final Object[] params;

		public Request(Object[] params)
		{
			this.params = params;
		}
		
		
		public void execute() throws InterruptedException
		{
			try
			{
				Object response = SerialRequestHandler.this.sender.send(this.params);
				if (!this.compute.hasWaiters())
					logger.warn("Protocol response was returned while no one expected it. Response was {}", response);
				this.compute.put(response);
			}
			catch (Exception e)
			{
				this.compute.error(e);
			}
		}
		
		
		public Object getResponse() throws Exception
		{
			return this.compute.get(SerialRequestHandler.this.requestTimeout);
		}
		
		public void cancel()
		{
			this.compute.put(DeviceResponse.CANCELED_REQUEST);
		}
	}

	private class RequestExecutor implements Runnable
	{
		
		private volatile boolean interrupted = false;
		
		@Override
		public void run()
		{
			while (!this.interrupted)
			{
				Request request = null;
				try
				{
					request = SerialRequestHandler.this.requestQueue.takeFirst();
					request.execute();
				}
				catch (InterruptedException ex)
				{
					//cancel request if interrupted
					if (request != null)
						request.cancel();
				}
			}
		}

	}
	
	private final ISend sender;
	private final int requestTimeout;
	private final RequestExecutor requestExec = new RequestExecutor();
	private final Thread executor = IExecutors.NETWORK_FACTORY.newThread(this.requestExec);
	private final ReentrantLock executorLock = new ReentrantLock();
	private final BlockingDeque<Request> requestQueue = new LinkedBlockingDeque<>();
	
	
	public SerialRequestHandler(ISend sender, int requestTimeout)
	{
		this.sender = sender;
		this.requestTimeout = requestTimeout; //protocol.config().getIntEntry(AProtocol.PARAM_REQUEST_TIMEOUT);
		this.executorLock.lock();
		this.executor.start();
	}


	public Object send(Object... params) throws IOException, TimeoutException
	{
		Request request = new Request(params);
		boolean success = this.requestQueue.offer(request);
		if (!success)
			return new DeviceResponse(Status.BAD_DEVICE_RESPONSE, "Device is busy");
		
		try
		{
			Object response = request.getResponse();
			return response;
		}
		catch (TimeoutException ex)
		{
			//this timeout (usually) means that the pipeline did not answer in time - it should cancel the request only - and not take the pipeline into a faulty state
			if (Compute.TIMEOUT_MESSAGE.equals(ex.getMessage()))
			{
				this.requestQueue.remove(request);
				return new DeviceResponse(Status.BAD_DEVICE_RESPONSE, "Device is busy");
			}
			//DO not catch - instead throw to device to go into error state - this exception is an exception from the pipeline
			throw ex;
		}
		//DO not catch - instead throw to device to go into error state - this exception is an exception from the pipeline
		catch (IOException ex)
		{
			throw ex;
		}
		//DO not catch - instead throw to device to go into error state - this exception is an exception from the pipeline
		catch (Exception e)
		{
			//put into IOexception
			throw new IOException(e);
		}
	}

	public void shutdown()
	{
		this.requestExec.interrupted = true;
		this.executor.interrupt();
		Request request = this.requestQueue.poll();
		while (request != null)
		{
			request.cancel();
			request = this.requestQueue.poll();
		}
	}
	
}
