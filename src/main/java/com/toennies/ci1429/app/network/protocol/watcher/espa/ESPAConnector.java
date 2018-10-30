package com.toennies.ci1429.app.network.protocol.watcher.espa;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import com.toennies.ci1429.app.network.connector.AFlexibleWrapperTransformer;
import com.toennies.ci1429.app.network.connector.IConnector;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Message;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.protocol.watcher.espa.data.ESPACall;
import com.toennies.ci1429.app.util.ASCII;
import com.toennies.ci1429.app.util.IExecutors;
import com.toennies.ci1574.lib.helper.Generics;


public class ESPAConnector<IN> extends AFlexibleWrapperTransformer<IN, IN, IMessage, IExtendedMessage> implements IConnector<IN>
{

	enum ESPAState
	{
		CLEAN,
		MASTER,
		OTHER_MASTER,
		SLAVE,
		SLAVE_CALL_RECIEVED
	}
	
	private static final IMessage MSG_NAK = new Message(ASCII.NAK.code);
	private static final IMessage MSG_EOT = new Message(ASCII.EOT.code);
	private static final IMessage MSG_ACK = new Message(ASCII.ACK.code);
	

	
	private boolean isCrudeMaster = false;
	private String slaveAddress = null;

	private final ReentrantLock lock = new ReentrantLock();
	private ESPAState state = ESPAState.CLEAN;
	private IMessage callToProof = null;
	private final ArrayDeque<IN> queue = new ArrayDeque<>();
	private int timeout;


	public ESPAConnector(IFlexibleConnector<IMessage, IExtendedMessage> connector)
	{
		super(connector);
	}


	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		this.state = ESPAState.CLEAN;
		this.isCrudeMaster = Boolean.getBoolean(config.getEntry(ESPAProtocol.PARAM_CRUDE_MASTER));
		this.slaveAddress = config.getEntry(ESPAProtocol.PARAM_ADDRESS);
		this.timeout = config.getIntEntry(IFlexibleConnector.PARAM_TIMEOUT);
		super.connect(config);
	}

	
	@Override
	public IN poll() throws IOException
	{
		this.lock.lock();
		try
		{
			IMessage msg = this.connector.poll();
			while (msg != null)
			{
				this.pollAndSelect(msg);
				msg = this.connector.poll();
			}
			
			return this.queue.poll();
		}
		finally
		{
			this.lock.unlock();
		}
	}

	@Override
	public IN pop() throws IOException, TimeoutException
	{
		IN obj = this.poll();
		if (obj != null)
			return obj;

		Callable<IN> popCall = () ->
		{
			this.lock.lock();
			try
			{
				while (this.queue.isEmpty())
				{
					IMessage msg = this.connector.pop();
					this.pollAndSelect(msg);
				}
				return this.queue.poll();
			}
			finally
			{
				this.lock.unlock();
			}
		};
		
		Future<IN> future = null;
		ExecutorService service = Executors.newSingleThreadExecutor(IExecutors.NETWORK_FACTORY);
		try
		{
			future = service.submit(popCall);
			return future.get(5 * timeout, TimeUnit.MILLISECONDS);
		}
		catch (ExecutionException e)
		{
			if (future != null)
				future.cancel(true);
			Throwable cause = e.getCause();
			if (cause instanceof IOException)
				throw (IOException) cause;
			if (cause instanceof TimeoutException)
				throw (TimeoutException) cause;
			throw new IOException(e.getCause());
		}
		catch (TimeoutException e)
		{
			if (future != null)
				future.cancel(true);
			throw e;
		}
		catch (InterruptedException e)
		{
			throw new IOException("Could not get data in time.", e);
		}
		finally
		{
			service.shutdownNow();
		}
	}

	
	private void pollAndSelect(IMessage msg) throws IOException
	{
		if (!ESPAProtocolUtils.isEOT(msg))
		{
			switch (this.state)
			{
				case CLEAN:
					if (ESPAProtocolUtils.isMeSelected(msg, this.slaveAddress))
					{
						this.state = ESPAState.MASTER;
						//if requests were send, send them now. Build request from current information here.
						this.connector.push(MSG_NAK);
					}
					else if (ESPAProtocolUtils.isOtherSelected(msg, this.slaveAddress))
						this.state = ESPAState.OTHER_MASTER;
					break;
				case MASTER:
					//nop - this will not happen, as we do not get messages during MASTER state
					//FIXME publish MINOR event - this is not according to the ESPA protocol
					this.connector.push(MSG_EOT);
					break;
				case OTHER_MASTER:
					if (ESPAProtocolUtils.isMeSelected(msg, this.slaveAddress))
					{
						this.state = ESPAState.SLAVE;
						if (!this.isCrudeMaster)
							this.connector.push(MSG_ACK);
					}
					break;
				case SLAVE:
					//respond to what every we get
					if (ESPAProtocolUtils.isDatablock(msg))
					{
						if (!this.isCrudeMaster)
							this.connector.push(MSG_ACK);
						this.state = ESPAState.SLAVE_CALL_RECIEVED;
					}
					else
						this.connector.push(MSG_NAK);
						
					if (ESPAProtocolUtils.isCall(msg))
					{
						this.callToProof = msg;
					}
					else if (ESPAProtocolUtils.isRequest(msg))
					{
						//get referenced call - queue answer. on next master call - send requested information
						//FIXME publish MINOR event - we havn't implemented this yet
					}
					break;
				case SLAVE_CALL_RECIEVED:
					int bccMaster = ESPAProtocolUtils.extractBCC(msg);
					if (bccMaster >= 0)
					{
						//get BCC - check BCC against current call, EOT is send directly.
						int bccLocal = ESPAProtocolUtils.computeBCC(this.callToProof);
						if (bccMaster == bccLocal)
						{
							try
							{
								ESPACall call = new ESPACall(this.callToProof.words().get(0));
								this.queue.add(Generics.convertUnchecked(call));
							}
							catch (ParseException e)
							{
								; //FIXME publish MAJOR EVENT error 
							}
						}
						else
							; //FIXME publish EVENT error with message text
					}
					else
						; //FIXME publish MINOR event + the original message - protocol not fully implemented
					//in this implementation, bcc is send extra and with EOT (which is not processed separately).
					//therefore, reset everything here (as if EOT would have been processed separately).
					this.state = ESPAState.CLEAN;
					this.callToProof = null;
					break;
			}
		}
		if (ESPAProtocolUtils.containsEOT(msg))	//Reset communication state
		{
			this.state = ESPAState.CLEAN;
			this.callToProof = null;
		}
	}


	@Override
	protected IN transformToOut(IExtendedMessage entity)
	{
		throw new UnsupportedOperationException("Should never happen");
	}

	@Override
	protected IMessage transformToConIn(IN entity)
	{
		throw new UnsupportedOperationException("Should never happen");
	}

}
