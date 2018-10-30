/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.radwag;

import java.nio.charset.Charset;

import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;

/**
 * Helper class that is used to parse simple scale responses.
 * 
 * @author renkenh
 */
class Responses
{

	private static final HardwareResponse ERROR_UNKNOWN_COMMAND_RESPONSE = new HardwareResponse(
			"Command does not exist or was not understood");

	private static final HardwareResponse ERROR_TIMEOUT_RESPONSE = new HardwareResponse(
			"Radwag response: Timeout while waiting for valid weight data");

	private static final HardwareResponse ERROR_MAX_RESPONSE = new HardwareResponse(
			"Radwag response: max sector is exceeded");

	private static final HardwareResponse ERROR_MIN_RESPONSE = new HardwareResponse(
			"Radwag response: min sector is exceeded");

	private static final Charset CHARSET = Charset.forName("US-ASCII");

	/**
	 * Maps simple responses to hardware responses.
	 * 
	 * @param msg
	 *            The message to parse.
	 * @return The mapped hardware response.
	 */
	public static final HardwareResponse map2Result(IMessage msg)
	{
		return map2Result(msg, null);
	}

	public static final HardwareResponse map2Result(IMessage msg, byte[] cmd)
	{
		if ("ES".equals(new String(msg.words().get(0), CHARSET)))
			return ERROR_UNKNOWN_COMMAND_RESPONSE;
		if (msg.words().size() != 1 || msg.words().get(0).length <= 2 || msg.words().get(0).length > 5)
			return null;

		HardwareResponse resp = validateReceipt(msg, cmd);
		if (resp != null)
			return resp;

		String receipt = new String(msg.words().get(0), CHARSET).split("\\s")[1];
		switch (receipt)
		{
			case "D":
			case "OK":
				return HardwareResponse.OK;
			case "I":
				return HardwareResponse.CANCELED;
			case "A":
				return HardwareResponse.WAIT;
			case "E":
				return ERROR_TIMEOUT_RESPONSE;
			case "^":
				return ERROR_MAX_RESPONSE;
			case "v":
				return ERROR_MIN_RESPONSE;
		}
		return new HardwareResponse("Unknown Message received: " + receipt);
	}

	private static final HardwareResponse validateReceipt(IMessage msg, byte[] cmd)
	{
		if (cmd == null || cmd.length == 0)
			return null;
		int length = getCommandLength(cmd);
		
		if (msg.words().get(0).length < length)
		{
			return new HardwareResponse("Unexpected response by Scale: " + new String(msg.words().get(0)));
		}
		
		for (int i = 0; i < length; i++)
		{
			if (msg.words().get(0)[i] != cmd[i])
			{
				return new HardwareResponse("Unexpected response by Scale: " + new String(msg.words().get(0)));
			}
		}
		return null;
	}

	private static final int getCommandLength(byte[] cmd)
	{
		for (int i = 0; i < cmd.length; i++)
		{
			if (cmd[i] == ' ')
			{
				return i;
			}
		}
		return cmd.length;
	}

	private Responses()
	{
		// no instance
	}

}
