/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.mettler;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.toennies.ci1429.app.network.connector.AWrappedExecConnector;
import com.toennies.ci1429.app.network.connector.IConnector;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.message.ExceptionMessage;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Message;
import com.toennies.ci1429.app.network.message.Messages;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.util.ASCII;

/**
 * Connector for IND690 mettler scales.
 * Only the CL-Handshake for both sending directions is supported and implemented.
 * @author renkenh
 */
class MettlerINDConnector extends AWrappedExecConnector<IMessage>
{
	private static final IMessage ACK = new Message(ASCII.ACK.code);

	/** Max Queue Size - arbitrarily chosen. */
	private static final int MAX_QUEUE_SIZE = 100;
	

	private class Repop implements Runnable
	{
		@Override
		public void run()
		{
			if (!MettlerINDConnector.super.isConnected())
			{
				MettlerINDConnector.this.executor.schedule(this, 1, TimeUnit.SECONDS);
				return;
			}

			try
			{
				IMessage popped = MettlerINDConnector.super.pop();
				if (!MettlerINDConnector.this.deque.offer(popped, MettlerINDConnector.this.timeout, TimeUnit.MILLISECONDS))
					throw new IOException("Buffer Overrun");
			}
			catch (IOException | TimeoutException | InterruptedException e)
			{
				logger.error("Error while retrieving data from device.", e);
				MettlerINDConnector.this.deque.pollLast();
				MettlerINDConnector.this.deque.addFirst(new ExceptionMessage(e));
			}
			
			ExecutorService service = MettlerINDConnector.this.executor;
			if (!service.isTerminated())
				service.execute(new Repop());
		}

	}



	private final LinkedBlockingDeque<IMessage> deque = new LinkedBlockingDeque<>(MAX_QUEUE_SIZE);
	private ScheduledExecutorService executor;
	private int timeout;


	/**
	 * @param transformer
	 * @param connector
	 */
	public MettlerINDConnector(IConnector<IMessage> connector)
	{
		super(connector);
	}


	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		this.timeout = config.getIntEntry(IFlexibleConnector.PARAM_TIMEOUT);
		this.deque.clear();
		super.connect(config);
	}
	
	
	@Override
	protected void schedule(ScheduledExecutorService service)
	{
		this.executor = service;
		this.executor.submit(new Repop());
	}

	@Override
	public void push(IMessage entity) throws IOException
	{
		MettlerINDConnector.super.push(entity);
		try
		{
			IMessage next = this.pop();
			if (Messages.isACK(next))
				return;
			throw new IOException("Expected ACK. Message has wrong content. " + String.valueOf(next));
		}
		catch (TimeoutException e)
		{
			throw new IOException("Didn't get ACK on time.", e);
		}
	}

	@Override
	public IMessage poll() throws IOException
	{
		IMessage msg = this.deque.poll();
		MettlerINDConnector.super.push(ACK);
		return msg;
	}

	@Override
	public IMessage pop() throws IOException, TimeoutException
	{
		try
		{
			IMessage msg = this.deque.poll(this.timeout, TimeUnit.MILLISECONDS);
			MettlerINDConnector.super.push(ACK);
			return msg;
		}
		catch (InterruptedException e)
		{
			throw new TimeoutException("pop() did not return in time. Was interruped.");
		}
	}
	
	@Override
	public void disconnect() throws IOException
	{
		this.deque.clear();
		super.disconnect();
	}
	
}
