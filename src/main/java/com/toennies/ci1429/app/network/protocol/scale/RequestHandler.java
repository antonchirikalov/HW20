/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import com.toennies.ci1429.app.network.connector.AFlexibleWrapperTransformer;
import com.toennies.ci1429.app.network.connector.IConnector;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;

/**
 * Scale protocol transformer that uses {@link IHardwareRequest}s and {@link HardwareResponse}s to parse client requests into
 * device specific messages.
 * @author renkenh
 */
public class RequestHandler extends AFlexibleWrapperTransformer<IHardwareRequest, HardwareResponse, IMessage, IMessage>
{

	private final Deque<IHardwareRequest> requestQueue = new ArrayDeque<>();


	/**
	 * Constructor.
	 */
	public RequestHandler(IConnector<IMessage> wrapped)
	{
		super(wrapped);
	}


	@Override
	public synchronized void connect(IConfigContainer config) throws IOException
	{
		this.requestQueue.clear();
		super.connect(config);
	}

	@Override
	public void push(IHardwareRequest entity) throws IOException
	{
		IMessage msg = this.transformToConIn(entity);
		if (msg != null)
			this.connector.push(msg);
	}

	@Override
	protected synchronized HardwareResponse transformToOut(IMessage entity)
	{
		IHardwareRequest request = this.requestQueue.pop();
		return request.handleResponse(entity);
	}

	@Override
	protected synchronized IMessage transformToConIn(IHardwareRequest entity)
	{
		this.requestQueue.push(entity);
		return entity.getRequestMessage();
	}

	
}
