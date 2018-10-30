/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.radwag;

import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Message;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;
import com.toennies.ci1429.app.network.protocol.scale.IHardwareRequest;

/**
 * Simple request/response parser.
 * 
 * @author renkenh
 */
class SimpleRequest implements IHardwareRequest
{

	private final IMessage request;

	/**
	 * Constructor.
	 */
	public SimpleRequest(String request)
	{
		this(request.getBytes(CHARSET));
	}

	/**
	 * Constructor.
	 */
	public SimpleRequest(byte[]... words)
	{
		this.request = new Message(words);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMessage getRequestMessage()
	{
		return this.request;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HardwareResponse handleResponse(IMessage message)
	{

		HardwareResponse resp = Responses.map2Result(message, request.words().get(0));
		if (resp == null)
			return new HardwareResponse("Unexcpected Message: " + message);
		return resp;
	}

}
