/**
 * 
 */
package com.toennies.ci1429.app.network.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.toennies.ci1429.app.util.ASCII;

public class MessageTransformer implements IMessageTransformer
{

	protected class Message extends com.toennies.ci1429.app.network.message.Message implements IExtendedMessage
	{
		
		private final byte[] rawData;

		protected Message(byte[] rawMSG)
		{
			super(extractWords(rawMSG));
			this.rawData = rawMSG;
		}
		
		@Override
		public byte[] getRawData()
		{
			return this.rawData;
		}
	}

	protected static final Charset CHARSET = Charset.forName("UTF-8");

	protected byte[] startControl;
	protected byte[] endControl;
	protected byte[] sepControl;

	private byte[] buffer = new byte[0];

	public MessageTransformer()
	{
		// do nothing - setup later
	}

	public MessageTransformer(String endControl)
	{
		this(null, null, endControl);
	}

	public MessageTransformer(byte[] endControl)
	{
		this(null, null, endControl);
	}

	public MessageTransformer(String startControl, String sepControl, String endControl)
	{
		this.setup(startControl, sepControl, endControl);
	}

	public MessageTransformer(byte startControl[], byte[] sepControl, byte[] endControl)
	{
		this.setup(startControl, sepControl, endControl);
	}

	public void setup(String startControl, String sepControl, String endControl)
	{
		this.setup(startControl != null ? startControl.getBytes(CHARSET) : null,
				sepControl != null ? sepControl.getBytes(CHARSET) : null,
				endControl != null ? endControl.getBytes(CHARSET) : null);
	}

	public void setup(byte startControl[], byte[] sepControl, byte[] endControl)
	{
		this.startControl = startControl != null && startControl.length > 0 && startControl[0] != 0x00 ? startControl : null;
		this.sepControl = sepControl != null && sepControl.length > 0 && sepControl[0] != 0x00 ? sepControl : null;
		this.endControl = endControl != null && endControl.length > 0 ? endControl : null;
		this.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear()
	{
		this.buffer = new byte[0];
	}

	protected boolean hasStartControl()
	{
		return this.startControl != null;
	}

	protected boolean hasSepControl()
	{
		return this.sepControl != null;
	}

	protected boolean hasEndControl()
	{
		return this.endControl != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IExtendedMessage> parseData(byte[] read)
	{
		// For a MSGSeparator that works without end control sequence
		// we need a timeout, since we are not able to detect the last message
		// if there is a long gap between messages
		if (!this.hasEndControl())
			throw new IllegalArgumentException("This transformer needs an end control character to work properly.");

		if (this.buffer.length + read.length > MAX_MSG_SIZE)
		{
			IOException ex = new IOException(
					"Datastream does not contain any Messages. MAX_SIZE = " + MAX_MSG_SIZE + " byte reached.");
			return Arrays.asList(new ExceptionMessage(ex));
		}

		List<IExtendedMessage> msgs = new ArrayList<>(1);
		byte[] tmp = new byte[this.buffer.length + read.length];
		System.arraycopy(this.buffer, 0, tmp, 0, this.buffer.length);
		System.arraycopy(read, 0, tmp, this.buffer.length, read.length);
		this.buffer = tmp;

		int endIndex = this.findNextMSGEnd(this.buffer, this.buffer.length - read.length);
		while (endIndex >= 0)
		{
			byte[] msg = new byte[endIndex];
			System.arraycopy(this.buffer, 0, msg, 0, msg.length);
			if (this.hasStartControl() && !this.msgHasStartControl(msg))
			{
				StringBuilder errorMsg = new StringBuilder();
				errorMsg.append("I/O-Error! Bad MSG Format.");
				errorMsg.append("Received [");
				errorMsg.append(ASCII.formatHuman(msg));
				errorMsg.append("]. Does not comply to [");
				errorMsg.append(ASCII.formatHuman(this.startControl));
				errorMsg.append("...");
				if (this.hasSepControl())
				{
					errorMsg.append(ASCII.formatHuman(this.sepControl));
					errorMsg.append("...");
				}
				errorMsg.append(ASCII.formatHuman(this.endControl));
				IOException ex = new IOException(errorMsg.toString()); // throws exception if start control is not present Likely. IO-Error
				
				msgs.add(new ExceptionMessage(ex));
				return msgs;
			}

			msgs.add(this.createMessageFromData(msg));

			tmp = new byte[this.buffer.length - msg.length];
			System.arraycopy(this.buffer, msg.length, tmp, 0, tmp.length);
			this.buffer = tmp;
			endIndex = this.findNextMSGEnd(this.buffer, 0);
		}
		return msgs;
	}

	protected IExtendedMessage createMessageFromData(byte[] data)
	{
		return new Message(data);
	}
	
	protected final byte[][] extractWords(byte[] data)
	{
		ArrayList<byte[]> list = new ArrayList<>(1);
		int start = MessageTransformer.this.hasStartControl() ? MessageTransformer.this.startControl.length : 0;
		int endIndex = MessageTransformer.this.findNextMSGSegment(data, start);
		while (endIndex > 0)
		{
			byte[] word = new byte[endIndex - start - (endIndex == data.length ? MessageTransformer.this.endControl.length : MessageTransformer.this.sepControl.length)];
			System.arraycopy(data, start, word, 0, word.length);

			list.add(word);
			start = endIndex;
			endIndex = MessageTransformer.this.findNextMSGSegment(data, start);
		}
		return list.toArray(new byte[list.size()][]);
	}

	protected boolean msgHasStartControl(byte[] msg)
	{
		if (!this.hasStartControl())
			return true;
		if (msg.length < this.startControl.length)
			throw new IllegalStateException("MSG to check shorter than start control sequence.");
		for (int i = 0; i < this.startControl.length; i++)
			if (msg[i] != this.startControl[i])
				return false;
		return true;
	}

	protected int findNextMSGEnd(byte[] buffer, int startIndex)
	{
		return searchForPattern(buffer, startIndex, this.endControl);
	}

	protected int findNextMSGSegment(byte[] buffer, int startIndex)
	{
		if (this.hasSepControl())
			return searchForPattern(buffer, startIndex, this.sepControl, this.endControl);
		return this.findNextMSGEnd(buffer, startIndex);
	}

	protected static final int searchForPattern(byte[] buffer, int start, byte[]... patterns)
	{
		int minFound = Integer.MAX_VALUE;
		byte[] patternFound = null;
		for (byte[] pattern : patterns)
		{
			// search for pattern
			for (int i = Math.max(pattern.length - 1, start); i < buffer.length; i++)
			{
				boolean found = true;
				for (int j = 0; j < pattern.length; j++)
				{
					found &= buffer[i - j] == pattern[pattern.length - 1 - j];
					if (!found)
						break;
				}
				if (found && (patternFound == null || patternFound.length < pattern.length || i < minFound))
				{
					minFound = Math.min(minFound, i + 1);
					patternFound = pattern;
				}
			}
		}
		return minFound != Integer.MAX_VALUE ? minFound : -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] formatMessage(IMessage msg) throws IOException
	{
		// For a MSGSeparator that works without end control sequence
		// we need a timeout, since we are not able to detect the last message
		// if there is a long gap between messages
		if (!this.hasEndControl())
			throw new IllegalArgumentException("This separator needs and end control character to work properly.");

		ByteArrayOutputStream stream = new ByteArrayOutputStream(10);
		if (this.hasStartControl())
			stream.write(this.startControl);
		List<byte[]> words = msg.words();
		stream.write(words.get(0));
		for (int i = 1; i < words.size(); i++)
		{
			if (this.hasSepControl())
				stream.write(this.sepControl);
			stream.write(words.get(i));
		}
		stream.write(this.endControl);
		return stream.toByteArray();
	}

}
