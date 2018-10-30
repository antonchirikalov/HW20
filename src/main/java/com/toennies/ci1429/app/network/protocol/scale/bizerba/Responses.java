/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.bizerba;

import java.nio.charset.Charset;

import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;

/**
 * Helper class that is used to parse simple scale responses.
 * @author renkenh
 */
class Responses
{
	
	private static final Charset CHARSET = Charset.forName("US-ASCII");

	/**
	 * Maps simple responses to hardware responses.
	 * @param msg The message to parse.
	 * @return The mapped hardware response.
	 */
	public static final HardwareResponse map2Result(IMessage msg)
	{
		if (msg.words().size() != 2)
			return null;

		String receipt = new String(msg.words().get(1), CHARSET).toLowerCase();
		switch (receipt)
		{
			case "w0":
				return HardwareResponse.OK;
			case "w6":
			case "w9":
				return new HardwareResponse("Bizerba Response: " + receipt);
			case "w1":
			case "w3":
				return HardwareResponse.CANCELED;
			case "w5":
				return HardwareResponse.WAIT;
		}
		return null;
	}


	private Responses()
	{
		//no instance
	}

}
