package com.toennies.ci1429.app.network.protocol.watcher.espa.data;

import java.nio.charset.StandardCharsets;

import com.toennies.ci1429.app.network.message.IMessage;

/**
 * A record in an ESPA call that has (string) data as payload.
 */
public final class DataRecord implements IRecord
{
	private final RecordType type;
	private final String data;

	/**
	 * Constructor. Parses the second word as payload.
	 * @param msg The message containing the payload.
	 */
	public DataRecord(IMessage msg)
	{
		this(RecordType.valueOf(msg), new String(msg.words().get(1), StandardCharsets.US_ASCII));
	}

	/**
	 * Simple constructor.
	 * @param type The type.
	 * @param data The payload.
	 */
	public DataRecord(RecordType type, String data)
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
	 * @return The data payload.
	 */
	public String getData()
	{
		return this.data;
	}
}