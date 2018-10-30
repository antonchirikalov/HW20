package com.toennies.ci1429.app.network.protocol.watcher.espa.data;

import com.toennies.ci1429.app.network.message.IMessage;

/**
 * Type identifying a record within a call.
 */
public interface IRecord
{
	
	/**
	 * The record type definition according to the ESPA protocol.
	 * @author renkenh
	 */
	public enum RecordType
	{
		UNKNOWN(true),
		CALL_ADDRESS(true),
		MESSAGE(true),
		BEEP_CODING,
		CALL_TYPE,
		NUMBER_TRANSMISSIONS,
		PRIORITY,
		CALL_STATUS,
		SYSTEM_STATUS;
		
		private final boolean isDataRecord;

		private RecordType()
		{
			this(false);
		}
		
		private RecordType(boolean isDataRecord)
		{
			this.isDataRecord = isDataRecord;
		}
		
		/**
		 * @return The code (or id) if this type. According to the ESPA protocol documentation.
		 */
		public int code()
		{
			return this.ordinal();
		}
		
		/**
		 * @return Whether this record contains additional data or not.
		 */
		public boolean isDataRecord()
		{
			return this.isDataRecord;
		}

		/**
		 * Based on the given code, returns the Record type.
		 * @param code The code from the data.
		 * @return The corresponding record type. {@link RecordType#UNKNOWN} if the given code is unknown.
		 */
		public static final RecordType valueOf(int code)
		{
			for (RecordType may : values())
				if (may.code() == code)
					return may;
			return RecordType.UNKNOWN;
		}

		/**
		 * Returns the record type based on the given message.
		 * @param msg The message to parse.
		 * @return The record type according of the given message.
		 */
		public static final RecordType valueOf(IMessage msg)
		{
			return valueOf(Character.getNumericValue((char) msg.words().get(0)[0]));
		}

	}
	

	/** The type of the record. Can be used for further casting and processing. */
	public RecordType getType();
}