package com.toennies.ci1429.app.network.protocol.scale.hofelmeyer.requests;

import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Message;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;
import com.toennies.ci1429.app.network.protocol.scale.IHardwareRequest;
import com.toennies.ci1429.app.network.protocol.scale.hofelmeyer.Responses;

public class SimpleRequest implements IHardwareRequest
{
	private final IMessage request;

	public SimpleRequest(String request)
	{
		this(request.getBytes(CHARSET));
	}

	public SimpleRequest(byte[]... words)
	{
		this.request = new Message(words);
	}

	@Override
	public IMessage getRequestMessage()
	{
		return this.request;
	}

	@Override
	public HardwareResponse handleResponse(IMessage message)
	{
		HardwareResponse response = Responses.map2Result(message);
		if (response == null)
		{
			return new HardwareResponse("Unexpected Message: " + message);
		}
		return response;
	}
}
