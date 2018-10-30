/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.mettler;

import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Message;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;
import com.toennies.ci1429.app.network.protocol.scale.IHardwareRequest;


/**
 * Generic request implementation for simple commands that do return a generic "ok" or "error" message.
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
		byte[] word = request.getBytes(CHARSET);
		this.request = new Message(word);
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
	public HardwareResponse handleResponse(IMessage response)
	{
		HardwareResponse result = Responses.checkCommand(response, this.request);
		if (result == HardwareResponse.OK)
			result = Responses.map2Result(response);
		if (result != null)
			return result;
		return new HardwareResponse("Could not parse given message. " + response.toString());
	}

}
