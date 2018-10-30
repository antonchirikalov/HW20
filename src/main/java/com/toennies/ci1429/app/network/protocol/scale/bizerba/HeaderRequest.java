/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.bizerba;

import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;


/**
 * Internal request + response used by the Bizerba network stack to handle the inital setup of the header.
 * @author renkenh
 */
class HeaderRequest extends SimpleRequest
{
	
	/** The response. Payload is of type byte[]. Status equals {@link Status#OK_DATA}. */ 
	class HeaderResponse extends HardwareResponse
	{

		/** 
		 * Constructor.
		 */
		HeaderResponse(byte[] data)
		{
			super(Status.OK_DATA, data);
		}

		/**
		 * @return The header.
		 */
		public byte[] getHeader()
		{
			return (byte[]) this.payload;
		}
	}
	

	/**
	 * Constructor.
	 */
	public HeaderRequest()
	{
		super("II");
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public HardwareResponse handleResponse(IMessage message)
	{
		HardwareResponse response = Responses.map2Result(message);
		if (response != null)
			return response;

		//extract header
		if (message.words().size() > 0)
			return new HeaderResponse(message.words().get(0));
		return new HardwareResponse("Got wrong message. " + String.valueOf(message));
	}

}
