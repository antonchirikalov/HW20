package com.toennies.ci1429.app.network.protocol.scale.hofelmeyer;

import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Message;
import com.toennies.ci1429.app.network.message.MessageTransformer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class HofelmeyerMSGTransformerTest
{
	private MessageTransformer transformer;

	@Before
	public void setUp()
	{
		this.transformer = new HofelmeyerMSGTransformer();
	}

	@Test
	public void parseDataShallReturnResponseMessageWithoutControlSymbols()
	{
		List<IExtendedMessage> messages = this.transformer.parseData("<RESPONSE>\r\n".getBytes());

		assertEquals("RESPONSE", new String(messages.get(0).words().get(0)));
	}

	@Test
	public void parseDataShallHandleMultilineResponses()
	{
		List<IExtendedMessage> messages = this.transformer.parseData("<RESPONSE1>\r\n<RESPONSE2>\r\n".getBytes());

		assertEquals("RESPONSE1", new String(messages.get(0).words().get(0)));
		assertEquals("RESPONSE2", new String(messages.get(1).words().get(0)));
	}

	@Test
	public void formatMessageShallWrapCommandWithTheControlSymbols() throws IOException
	{
		byte[] message = this.transformer.formatMessage(new Message("COMMAND".getBytes()));

		assertEquals("<COMMAND>", new String(message));
	}

	@Test
	public void createMessageFromDataShallStripControlSymbols()
	{
		HofelmeyerMSGTransformerTestWrapper transformer = new HofelmeyerMSGTransformerTestWrapper();

		IMessage message = transformer.createMessageFromData("<MESSAGE>".getBytes());

		assertEquals("MESSAGE", new String(message.words().get(0)));
	}

	class HofelmeyerMSGTransformerTestWrapper extends HofelmeyerMSGTransformer
	{
		public IExtendedMessage createMessageFromData(byte[] data)
		{
			return super.createMessageFromData(data);
		}
	}
}
