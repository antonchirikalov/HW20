/**
 * 
 */
package com.toennies.ci1429.app.network.connector;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.IMessageTransformer;
import com.toennies.ci1429.app.network.message.MessageTransformer;

/**
 * Uses a given {@link MessageTransformer} to transform a stream of bytes into
 * {@link IMessage} objects.
 * 
 * @author renkenh
 */
public abstract class ADataTransformer extends AFlexibleWrapperTransformer<IMessage, IExtendedMessage, byte[], byte[]>
{
	
	/** Specify the separator to identify parts of a message. */
	public static final String PARAM_FRAME_START = "framestart";
	/** Specify the separator to identify parts of a message. */
	public static final String PARAM_FRAME_SEP = "framesep";
	/** Specify the end characters of a scanned string. */
	public static final String PARAM_FRAME_END = "frameend";

	protected static final Logger logger = LogManager.getLogger();


	private final ArrayDeque<IExtendedMessage> msgQueue = new ArrayDeque<>();


	/**
	 * Uses the information from the config to create a standard {@link MessageTransformer}.
	 * @param connector
	 */
	public ADataTransformer(IConnector<byte[]> connector)
	{
		super(connector);
	}

	protected abstract IMessageTransformer transformer();
	

	@Override
	public final IExtendedMessage poll() throws IOException
	{
		byte[] rawData = this.connector.poll();
		while (rawData != null)
		{
			this.msgQueue.addAll(this.transformer().parseData(rawData));
			rawData = this.connector.poll();
		}
		return this.msgQueue.poll();
	}

	@Override
	public final IExtendedMessage pop() throws IOException, TimeoutException
	{
		IExtendedMessage msg = this.poll();
		if (msg != null)
			return msg;

		while (this.msgQueue.isEmpty())
		{
			byte[] rawData = this.connector.pop();
			this.msgQueue.addAll(this.transformer().parseData(rawData));
		}
		return this.msgQueue.poll();
	}

	@Override
	protected final IExtendedMessage transformToOut(byte[] entity) throws IOException
	{
		//method not used in pop() or poll()
		return null;
	}
	
	@Override
	protected final byte[] transformToConIn(IMessage entity) throws IOException
	{
		return this.transformer().formatMessage(entity);
	}
	
	@Override
	public final void disconnect() throws IOException
	{
		this.transformer().clear();
		this.connector.disconnect();
	}

	@Override
	public final void shutdown()
	{
		this.transformer().clear();
		this.connector.shutdown();
	}

}