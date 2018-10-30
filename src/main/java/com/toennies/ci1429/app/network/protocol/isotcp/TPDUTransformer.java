/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.isotcp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.toennies.ci1429.app.network.message.ExceptionMessage;
import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.IMessageTransformer;
import com.toennies.ci1429.app.network.protocol.isotcp.tpdu.TPDU;
import com.toennies.ci1429.app.util.Binary;

/**
 * {@link IMessageTransformer} implementation that splits the incoming data stream into tpdus.
 * @author renkenh
 */
public class TPDUTransformer implements IMessageTransformer
{
	
	private byte[] buffer = new byte[0];

	@Override
	public List<IExtendedMessage> parseData(byte[] read) throws IOException
	{
		if (this.buffer.length + read.length > MAX_MSG_SIZE)
		{
			IOException ex = new IOException("Datastream does not contain any Messages. MAX_SIZE = "+ MAX_MSG_SIZE +" byte reached.");
			return Arrays.asList(new ExceptionMessage(ex));
		}

		List<IExtendedMessage> msgs = new ArrayList<>(1);
		byte[] tmp = new byte[this.buffer.length+read.length];
		System.arraycopy(this.buffer, 0, tmp, 0, this.buffer.length);
		System.arraycopy(read, 0, tmp, this.buffer.length, read.length);
		this.buffer = tmp;

		int endIndex = findNextMSGEnd(this.buffer);
		while (endIndex >= 0)
		{
			final byte[] msg = new byte[endIndex];
			System.arraycopy(this.buffer, 0, msg, 0, msg.length);
			
			msgs.add(TPDU.createTPDU(msg));
			
			tmp = new byte[this.buffer.length - msg.length];
			System.arraycopy(this.buffer, msg.length, tmp, 0, tmp.length);
			this.buffer = tmp;
			endIndex = findNextMSGEnd(this.buffer);
		}
		return msgs;

	}
	
	private static final int findNextMSGEnd(byte[] buffer) throws IOException
	{
		if (buffer.length >= 6)
		{
			int packetLength = Binary.getUShort(buffer, 2);
			if (packetLength <= 7)
                throw new IOException("Syntax error: packet length parameter <= 7");
			
			if (buffer.length >= packetLength);
				return packetLength;
		}
		return -1;
	}



	@Override
	public byte[] formatMessage(IMessage msg) throws IOException
	{
		if (msg instanceof TPDU)
			return ((TPDU) msg).getRawData();
		
		int length = msg.words().stream().map((b) -> b.length).reduce(0, (i1, i2) -> i1.intValue() + i2.intValue());
		byte[] arr = new byte[length];
		int pos = 0;
		for (byte[] word : msg.words())
		{
			System.arraycopy(word, 0, arr, pos, word.length);
			pos += word.length;
		}
		return arr;
	}


	@Override
	public void clear()
	{
		this.buffer = new byte[0];
	}

}
