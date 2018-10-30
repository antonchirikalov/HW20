/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.watcher.espa.data;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.protocol.watcher.espa.data.IRecord.RecordType;
import com.toennies.ci1429.app.util.ASCII;

/**
 * Type that represents a "call" over the ESPA protocol. This type contains methods to parse a given
 * message object.
 * @author renkenh
 */
public class ESPACall
{
	
	/** ESPACall type according to the documentation. */
	public enum EventType
	{
		UNKNOWN,
		CALL_TO_PAGER,
		STATUS_INFORMATION,
		STATUS_REQUEST,
		CALL_TO_SUBCRIBER,
		OTHER;
		

		/**
		 * @return The ascii char representing the number returned by {@link #code()}.
		 */
		public char ascii()
		{
			return Character.forDigit(this.code(), 10); //10er System
		}
		
		/**
		 * @return The code (or id) of this type. According to the ESPA protocol documentation.
		 */
		public int code()
		{
			return this.ordinal();
		}

		/**
		 * Based on the given code, returns the Event type.
		 * @param code The code from the data.
		 * @return The corresponding event type. {@link EventType#UNKNOWN} if the given code is unknown.
		 */
		public static final EventType valueOf(int code)
		{
			for (EventType may : values())
				if (may.code() == code)
					return may;
			return EventType.UNKNOWN;
		}
	}
	
	/**
	 * The type of a call. See ESPA protocol specs.
	 */
	public enum CallType
	{
		RESERVED,
		RESET_CALL,
		SPEECH_CALL,
		STANDARD_CALL,
		UNKNOWN
	}

	/**
	 * The priority a call can habe. See ESPA protocol specs.
	 */
	public enum Priority
	{
		UNKNOWN,
		ALARM,
		HIGH,
		NORMAL;
		

		/**
		 * @return The code (or id) if this priority. According to the ESPA protocol documentation.
		 */
		public int code()
		{
			return this.ordinal();
		}
		
		/**
		 * Based on the given code, returns the priority.
		 * @param code The code from the data.
		 * @return The corresponding priority. {@link RecordType#UNKNOWN} if the given code is unknown.
		 */
		public static final Priority valueOf(int code)
		{
			for (Priority p : values())
				if (p.code() == code)
					return p;
			return Priority.UNKNOWN;
		}

		/**
		 * Returns the priority based on the given message.
		 * @param msg The message to parse.
		 * @return The priority according of the given message.
		 */
		public static final Priority valueOf(IMessage msg)
		{
			return valueOf(Character.getNumericValue((char) msg.words().get(0)[0]));
		}

	}
	
	private final EventType type;
	private final Map<RecordType, IRecord> records = new HashMap<>();
	private final byte[] rawData;


	/**
	 * Parsing Constructor. Gets raw data and parses that.
	 */
	public ESPACall(byte[] rawData) throws ParseException
	{
		this.type = extractEventType(rawData);
		this.rawData = rawData;
		extractRecords(rawData).stream().forEach((r) -> this.records.put(r.getType(), r));
	}


	/**
	 * @return The event type of this call.
	 */
	public EventType getType()
	{
		return this.type;
	}

	/**
	 * @return The priority - if no priority record is found {@link Priority#UNKNOWN} is returned.
	 */
	public Priority getPriority()
	{
		IntRecord record = (IntRecord) this.records.get(RecordType.PRIORITY);
		if (record != null)
			return Priority.valueOf(record.getData());
		return Priority.UNKNOWN;
	}

	/**
	 * @return The message - if this call includes a record of type {@link RecordType#MESSAGE}. Otherwise <code>null</code>.
	 */
	public String getMessage()
	{
		DataRecord record = (DataRecord) this.records.get(RecordType.MESSAGE);
		if (record != null)
			return record.getData();
		return null;
	}

	/**
	 * @return The type of the call if a {@link RecordType#CALL_TYPE} record is found. Otherwise {@link CallType#UNKNOWN}.
	 */
	public CallType getCallType()
	{
		IntRecord record = (IntRecord) this.records.get(RecordType.CALL_TYPE);
		if (record != null && record.getData() < CallType.values().length)
			return CallType.values()[record.getData()];
		return CallType.UNKNOWN;
	}

	/**
	 * @return The raw data of this call.
	 */
	public byte[] getRawData()
	{
		return this.rawData;
	}

	private static final char DELIMITER = '|';
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(this.getPriority());
		sb.append(DELIMITER);
		sb.append(this.getCallType());
		sb.append(DELIMITER);
		sb.append(this.getType());
		sb.append(DELIMITER);
		sb.append(this.getMessage());
		return sb.toString();
	}


	private static final EventType extractEventType(byte[] rawData) throws ParseException
	{
		if (rawData[0] != ASCII.SOH.code)
			throw new ParseException("Header start not found in '"+ASCII.formatHuman(rawData)+"'", 0);
		int stxIndex = stxIndex(rawData);
		if (stxIndex < 0)
			throw new ParseException("Header end not found in '"+ASCII.formatHuman(rawData)+"'", stxIndex);
		String number = new String(rawData, 1, stxIndex-1, StandardCharsets.US_ASCII);
		try
		{
			return EventType.valueOf(Integer.parseInt(number));
		}
		catch (NumberFormatException ex)
		{
			throw new ParseException("Could not parse header for event type: '"+number+"'", -1);
		}
	}
	
	private static final List<IRecord> extractRecords(byte[] rawData) throws ParseException
	{
		int stxIndex = stxIndex(rawData);
		if (stxIndex < 0)
			throw new ParseException("Header end not found in '"+ASCII.formatHuman(rawData)+"'", stxIndex);
		RecordMSGTransformer transformer = new RecordMSGTransformer();
		byte[] data = new byte[rawData.length-stxIndex-1];
		System.arraycopy(rawData, stxIndex+1, data, 0, data.length);
		List<IExtendedMessage> rawRecords = transformer.parseData(data);
		List<IRecord>  records = new ArrayList<>();
		for (IMessage msg : rawRecords)
		{
			if (RecordType.valueOf(msg).isDataRecord())
				records.add(new DataRecord(msg));
			else
				records.add(new IntRecord(msg));
		}
		return records;
	}
	
	private static final int stxIndex(byte[] rawData)
	{
		for (int i = 0; i < rawData.length; i++)
			if (rawData[i] == ASCII.STX.code)
				return i;
		return -1;
	}
}

