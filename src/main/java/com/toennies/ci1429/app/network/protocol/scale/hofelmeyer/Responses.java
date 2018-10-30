package com.toennies.ci1429.app.network.protocol.scale.hofelmeyer;

import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;

public class Responses
{
	private static final String SUCCESS = "00";

	public static final HardwareResponse map2Result(IMessage msg)
	{
		if (msg.words().get(0).length != 2)
			return null;

		String responseCode = new String(msg.words().get(0));
		switch (responseCode)
		{
			case SUCCESS:
				return HardwareResponse.OK;
			default:
				return null;
		}
	}

	private Responses()
	{
		// no instance
	}
}
