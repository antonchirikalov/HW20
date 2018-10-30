package com.toennies.ci1429.app.network.protocol.scale.bizerba;

import java.io.IOException;
import java.util.List;

import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.IMessageTransformer;
import com.toennies.ci1429.app.util.ASCII;

public class BizerbaMSGTransformerDemo
{



	public static final void main(String[] args) throws Exception
	{
		BizerbaMSGTransformer parser = new BizerbaMSGTransformer();
		parser.setup(as(ASCII.SOH.code), as(ASCII.ETX.code), as(ASCII.ETB.code));
		log(parser.parseData(new byte[]{ ASCII.ACK.code }));
		log(parseHuman(parser, "[SOH]1[ETX]253[ETX]253[ETX]I!LV00|LW01|30173|LX02[ETB]"));
	}
	
	private static final byte[] as(byte... arr)
	{
		return arr;
	}
	
	private static final List<IExtendedMessage> parseHuman(IMessageTransformer parser, String msg) throws IOException
	{
		return parser.parseData(ASCII.parseHuman(msg).getBytes());
	}
	
	private static final void log(List<IExtendedMessage> msgs)
	{
		System.out.println("Message Count: " + msgs.size());
		for (IMessage msg : msgs)
		{
			System.out.println(msg);
		}
		System.out.println("------------------------------------");
	}

}
