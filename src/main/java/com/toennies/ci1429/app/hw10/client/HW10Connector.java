package com.toennies.ci1429.app.hw10.client;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeoutException;

import com.toennies.ci1429.app.network.connector.AFlexibleWrappedExecTransformer;
import com.toennies.ci1429.app.network.connector.IConnector;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Messages;

/**
 * 
 * 
 * @author renkenh
 */
class HW10Connector extends AFlexibleWrappedExecTransformer<IMessage, IMessage, IMessage, IExtendedMessage> implements IConnector<IMessage>
{
	private final class ResponseSender implements Callable<Void>
	{

		private final IMessage response;

		public ResponseSender(IMessage response)
		{
			this.response = response;
		}

		/**
		 * This {@link Runnable} dispatches all messages to be sent to ProFood
		 * or Meatline.
		 * <p>
		 * If the message sent was not an [ACK] or a [NAK] it waits for an [ACK]
		 * or [NAK] to be received by the {@link CommandReceiver}.
		 */
		@Override
		public Void call() throws Exception
		{
			if (HW10Connector.super.connector.isConnected())
			{
				if (!Messages.isACK(this.response) && !Messages.isNAK(this.response))
				{
					HW10Connector.this.sendSemaphore.acquire();
				}
				HW10Connector.super.connector.push(this.response);
				return null;
			}
			throw new IllegalStateException("Connector is disconnected.");
		}
	}

	private final Semaphore sendSemaphore = new Semaphore(1);
	private ScheduledExecutorService executor;

	public HW10Connector(IFlexibleConnector<IMessage, IExtendedMessage> connector)
	{
		super(connector);
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	protected void schedule(ScheduledExecutorService service)
	{
		this.executor = service;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void push(IMessage entity) throws IOException
	{
		this.executor.submit(new ResponseSender(entity));
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public IMessage poll() throws IOException
	{
		IMessage msg = super.poll();
		while (this.handleACKNAK(msg))
			msg = super.poll();
		return msg;
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public IMessage pop() throws IOException, TimeoutException
	{
		IMessage msg = super.pop();
		while (this.handleACKNAK(msg))
		{
			msg = super.pop();
		}
		return msg;
	}

	private boolean handleACKNAK(IMessage msg)
	{
		if (Messages.isACK(msg) || Messages.isNAK(msg))
		{
			this.sendSemaphore.release();
			return true;
		}
		return false;
	}

	@Override
	protected IExtendedMessage transformToOut(IExtendedMessage entity) throws IOException
	{
		return entity;
	}

	@Override
	protected IMessage transformToConIn(IMessage entity) throws IOException
	{
		return entity;
	}
}