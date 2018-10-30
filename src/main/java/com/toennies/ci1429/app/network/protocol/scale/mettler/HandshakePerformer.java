/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.mettler;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import com.toennies.ci1429.app.network.connector.AFlexibleWrapperTransformer;
import com.toennies.ci1429.app.network.connector.IConnector;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Message;
import com.toennies.ci1429.app.network.message.Messages;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.util.ASCII;

/**
 * Performs the handshake for mettler IND690 scales. Does also handle the scale initiated handshake.
 * @author renkenh
 */
class HandshakePerformer extends AFlexibleWrapperTransformer<IMessage, IMessage, IMessage, IExtendedMessage> implements IConnector<IMessage>
{
	
	private enum HandShake
	{
		NONE, SCALESYN, SCALEACK, FINISHED
	}
	
	

	private final ReentrantLock hsStateLock = new ReentrantLock();
	private volatile HandShake hsState = HandShake.NONE;

	/**
	 * @param transformer
	 * @param connector
	 */
	public HandshakePerformer(IFlexibleConnector<IMessage, IExtendedMessage> connector)
	{
		super(connector);
	}


	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		this.setHandShake(HandShake.NONE);
		super.connect(config);
		this.runHandshake();
	}
	
	
	private void runHandshake() throws IOException
	{
		this.runHandshake(null);
	}
	
	/**
	 * this method should only return after a full handshake has been performed. Otherwise this method
	 * should die with an exception.
	 */
	private void runHandshake(IMessage msg) throws IOException
	{
		this.performHandshake(msg);
		try
		{
			while (this.getHandShake() != HandShake.FINISHED || this.getHandShake() != HandShake.NONE)
			{
				this.performHandshake(super.pop());
			}
		}
		catch (TimeoutException e)
		{
			this.setHandShake(HandShake.NONE);
			throw new IOException("Handshake did not finish.", e);
		}
		catch (IOException e)
		{
			this.setHandShake(HandShake.NONE);
			throw e;
		}
	}
	
	private void performHandshake(IMessage message) throws IOException
	{
		try
		{
			HandShake newState = HandShake.NONE;
			byte code = -1;
			switch (this.getHandShake())
			{
				case NONE:
				case FINISHED:
					if (message == null)
					{
						newState = HandShake.SCALEACK;
						code = ASCII.SYN.code;
						break;
					}
					if (Messages.isSYN(message))
					{
						newState = HandShake.SCALESYN;
						code = ASCII.SYN.code;
						break;
					}
					throw new IOException("Message does not initialize handshake. " + message);
				case SCALESYN:
					if (Messages.isSYN(message))
					{
						newState = HandShake.SCALEACK;
						break;
					}
					throw new IOException("Failed on second SYN. " + message);
				case SCALEACK:
					if (Messages.isACK(message))
					{
						code = ASCII.ACK.code;
						newState = HandShake.FINISHED;
						break;
					}
					throw new IOException("Failed on final ACK. " + message);
			}
			
			this.setHandShake(newState);
			if (code != -1)
				super.push(new Message(code));
		}
		catch (Exception ex)
		{
			if (this.hsState != HandShake.FINISHED)
				this.hsState = HandShake.NONE;
			throw ex;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected()
	{
		return super.isConnected() && this.getHandShake() == HandShake.FINISHED;
	}

	//every time a SYN is send - start a new handshake :(
	//check if SYN - then start handshake (again) - maybe use executor to permanently watch incoming
	//messages - queue should not get that large - throw IOException when queue gets to big (overflow error)
	@Override
	public IMessage poll() throws IOException
	{
		IMessage polled = super.poll();
		if (Messages.isSYN(polled))
		{
			this.runHandshake(polled);
			return super.poll();
		}
		return polled;
	}

	@Override
	public IMessage pop() throws IOException, TimeoutException
	{
		IMessage popped = super.pop();
		if (Messages.isSYN(popped))
		{
			this.runHandshake(popped);
			return super.pop();
		}
		return popped;
	}

	@Override
	public void disconnect() throws IOException
	{
		this.setHandShake(HandShake.NONE);
		super.disconnect();
	}
	
	@Override
	public void shutdown()
	{
		this.setHandShake(HandShake.NONE);
		super.shutdown();
	}


	private void setHandShake(HandShake newState)
	{
		this.hsStateLock.lock();
		try
		{
			this.hsState = newState;
		}
		finally
		{
			this.hsStateLock.unlock();
		}
	}

	private HandShake getHandShake()
	{
		this.hsStateLock.lock();
		try
		{
			return this.hsState;
		}
		finally
		{
			this.hsStateLock.unlock();
		}
	}


	@Override
	protected IMessage transformToOut(IExtendedMessage entity) throws IOException
	{
		return entity;
	}

	@Override
	protected IMessage transformToConIn(IMessage entity) throws IOException
	{
		return entity;
	}


}
