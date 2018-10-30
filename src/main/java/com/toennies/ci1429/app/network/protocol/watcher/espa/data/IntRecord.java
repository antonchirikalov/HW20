package com.toennies.ci1429.app.network.protocol.watcher.espa.data;

import java.nio.charset.StandardCharsets;

import com.toennies.ci1429.app.network.message.IMessage;

/**
 * A record in a call that has an integer value as payload.
 * @author renkenh
 */
public final class IntRecord implements IRecord
{

	private final RecordType type;
	private final int data;

	/**
	 * Basic constructor that interprets the second word in the message as an integer value.
	 * @param msg The message to parse.
	 */
	public IntRecord(IMessage msg)
	{
		this(RecordType.valueOf(msg), Integer.parseInt(new String(msg.words().get(1), StandardCharsets.US_ASCII)));
	}

	/**
	 * Simple constructor.
	 * @param type The type of the record.
	 * @param data The payload.
	 */
	public IntRecord(RecordType type, int data)
	{
		this.type = type;
		this.data = data;
	}

	@Override
	public RecordType getType()
	{
		return this.type;
	}
	
	/**
	 * @return The payload.
	 */
	public int getData()
	{
		return this.data;
	}

}