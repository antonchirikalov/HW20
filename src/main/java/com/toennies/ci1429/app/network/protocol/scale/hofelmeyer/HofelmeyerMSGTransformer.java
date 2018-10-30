package com.toennies.ci1429.app.network.protocol.scale.hofelmeyer;

import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.message.MessageTransformer;

public class HofelmeyerMSGTransformer extends MessageTransformer
{
	private static final String START_CONTROL = "<";

	private static final String END_CONTROL = ">";

	public HofelmeyerMSGTransformer()
	{
		this.setup(START_CONTROL, null, END_CONTROL);
	}

	public HofelmeyerMSGTransformer(String startControl, String endControl)
	{
		this.setup(startControl, null, endControl);
	}

	@Override
	public IExtendedMessage createMessageFromData(byte[] data)
	{
		//byte[] msgData = new byte[data.length - this.startControl.length - this.endControl.length];
		//System.arraycopy(data, this.startControl.length, msgData, 0, msgData.length);
		return super.createMessageFromData(data);
	}
}
