/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.isotcp.tpdu;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.util.Binary;

/**
 * Message envelope for custom content according to rfc1006. A tpdu consists of three parts:
 * * A message header of four bytes. These are always defined.
 * * A tpdu type specific header.
 * * A tpdu type specific payload.
 * @author renkenh
 */
public class TPDU implements IExtendedMessage
{

	/** The types a tpdu can have. */ 
	public enum Type
	{
		DATA_TRANSFER(0xf0, DataTransferTPDU.class),
		ERROR(0x70),
		DISCONNECT_REQUEST(0x80, DisconnectTPDU.class),
		CONNECT_REQUEST(0xe0, ConnectTPDU.class),
		CONNECT_RESPONSE(0xd0, ConnectResponseTPDU.class),
		UNKNOWN(-1);
		
		/** The code that defines the tpdu type. Is specified in the tpdu header. */
		public final short code;
		private final Class<? extends TPDU> clazz;

		private Type(int code)
		{
			this(code, TPDU.class);
		}
		
		private Type(int code, Class<? extends TPDU> clazz)
		{
			this.code = (short) code;
			this.clazz = clazz;
		}

		/**
		 * Find the type by its code. Can be used while reading the header.
		 * @param code The code from the tpdu header.
		 * @return The type. If the type is unkown - {@link Type#UNKNOWN} is returned.
		 */
		public static final Type findBy(short code)
		{
			for (Type t : Type.values())
				if (t.code == code)
					return t;
			return UNKNOWN;
		}

		/**
		 * Uses the class definition from the type to create a new tpdu of that class. If the class could not be instanciated a simple {@link TPDU} is returned.
		 * @param rawData The raw data that (should) contains the tpdu information.
		 * @return The tpdu created. 
		 */
		protected final TPDU create(byte[] rawData)
		{
			try
			{
				return this.clazz.getConstructor(byte[].class).newInstance(rawData);
			}
			catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e)
			{
				return new TPDU(rawData);
			}
		}
	}
	
	
//	public static final String FIELD_VERSION     = "version";
//	public static final String FIELD_RESERVED    = "reserved";
//	public static final String FIELD_TPDU_LENGTH = "tpdu_length";
//	public static final String FIELD_HEAD_LENGTH = "head_length";
//	public static final String FIELD_TYPE_CODE   = "tpdu_code";
//	
//	private static final BField[] DEFAULT_FIELDS =
//	{
//		new BField(BType.BYTE, FIELD_VERSION),
//		new BField(BType.BYTE, FIELD_RESERVED),
//		new BField(BType.USHORT, FIELD_TPDU_LENGTH),
//		new BField(BType.UBYTE, FIELD_HEAD_LENGTH),
//		new BField(BType.UBYTE, FIELD_TYPE_CODE)
//	};


	/** The raw data. */
	protected final byte[] rawData;


	/**
	 * Constructor that parses the given raw data.
	 */
	public TPDU(byte[] rawData)
	{
		this.rawData = rawData;
	}

	/**
	 * Creates a new tpdu with the given parameters.
	 * @param type The type of the tpdu.
	 * @param headerLength The header length.
	 * @param payload The payload size.
	 */
	protected TPDU(Type type, int headerLength, byte[] payload)
	{
		this.rawData = new byte[5 + headerLength + payload.length];
		this.rawData[0] = 3;	//version
		this.rawData[1] = 0;	//reserved
		Binary.putUShort(this.rawData, 2, this.rawData.length);	//packet length
		Binary.putUByte(this.rawData, 4, (short) headerLength);	//header length
		Binary.putUByte(this.rawData, 5, (short) type.code);		//tpdu type
		System.arraycopy(payload, 0, this.rawData, 5 + headerLength, payload.length);
	}


	/**
	 * Returns the version info from the header.
	 * @return The version info.
	 */
	public byte getVersion()
	{
		return this.rawData[0];
	}
	
	/**
	 * Returns the reserved byte from the header - usually 0.
	 * @return The reserved byte.
	 */
	public byte getReserved()
	{
		return this.rawData[1];
	}
	
	private int getDefinedLength()
	{
		return Binary.getUShort(this.rawData, 2);
	}
	
	/**
	 * Returns the header length. This specifies the length of the tpdu specific header only.
	 * @return The header length.
	 */
	public short getHeaderLength()
	{
		return Binary.getUByte(this.rawData, 4);
	}

	/**
	 * The code of the type defined in the header. Use {@link Type#findBy(short)} to parse the type.
	 * @return The code of the type - as specified in rfc1006.
	 */
	public short getTypeCode()
	{
		return Binary.getUByte(this.rawData, 5);
	}

	/**
	 * Uses the code from {@link #getTypeCode()} and finds the {@link Type} by calling {@link Type#findBy(short)}.
	 * @return The type of the tpdu.
	 */
	public Type getType()
	{
		return Type.findBy(this.getTypeCode());
	}

	/**
	 * Returns the payload length.
	 * @return The payload length.
	 */
	public int getPayloadLength()
	{
		return this.getDefinedLength() - this.getHeaderLength() - 5;
	}
	
	/**
	 * Returns the payload of the tpdu.
	 * @return The payload.
	 */
	public byte[] getPayload()
	{
		byte[] arr = new byte[this.getPayloadLength()];
		System.arraycopy(this.rawData, 5 + this.getHeaderLength(), arr, 0, this.getPayloadLength());
		return arr;
	}
	
	/**
	 * Returns the first four bytes of the tpdu header. These four bytes are always defined. Everything else depends on the tpdu type.
	 * @return The actual (fixed) header.
	 */
	protected byte[] getPacketHeader()
	{
		byte[] arr = new byte[4];
		System.arraycopy(this.rawData, 0, arr, 0, arr.length);
		return arr;
	}

	/**
	 * Returns the header of the tpdu type. This header (and its content) varies, based on the tpdu type.
	 * @return The tpdu type specific header.
	 */
	protected byte[] getTPDUHeader()
	{
		byte[] arr = new byte[this.getHeaderLength()+1];
		System.arraycopy(this.rawData, 4, arr, 0, this.getHeaderLength()+1);
		return arr;
	}
	
	/**
	 * Returns whether the tpdu is valid or not.
	 * @return Whether the tpdu is valid or not.
	 */
	public boolean isValid()
	{
		return this.getVersion() == 3 && this.getReserved() == 0 && this.getDefinedLength() > 7;
	}
	
	/**
	 * Method to validate the header and to provoke an exception if it is not valid.
	 * @throws IOException An expection if the tpdu is invalid.
	 */
	public void validate() throws IOException
	{
        if (this.getVersion() != 3)
            throw new IOException("Syntax error at beginning of RFC1006 header: version not equal to 3");

        if (this.getReserved() != 0)
            throw new IOException("Syntax error at beginning of RFC1006 header: reserved not equal to 0");

        if (this.getDefinedLength() <= 7)
            throw new IOException("Syntax error: packet length parameter <= 7");
	}

	@Override
	public List<byte[]> words()
	{
		return Arrays.asList(this.getPacketHeader(), this.getTPDUHeader(), this.getPayload());
	}

	@Override
	public int wordCount()
	{
		return 3;
	}

	@Override
	public byte[] word(int index)
	{
		switch (index)
		{
			case 0:
				return this.getPacketHeader();
			case 1:
				return this.getTPDUHeader();
			case 2:
				return this.getPayload();
		}
		throw new IndexOutOfBoundsException("Only indices from (0..2) are allowed.");
	}

	@Override
	public byte[] getRawData()
	{
		byte[] data = new byte[this.rawData.length];
		System.arraycopy(this.rawData, 0, data, 0, this.rawData.length);
		return data;
	}
	
	/**
	 * Creates a new tpdu from the given raw data. The returns instance usually is a subclass of {@link TPDU}. The method
	 * determines the correct tpdu type and the implementation to use based on the information from the tpdu header.
	 * @param rawData The raw data.
	 * @return A tpdu instance. May be a subclass.
	 */
	public static final TPDU createTPDU(byte[] rawData)
	{
		short code = Binary.getUByte(rawData, 5);
		Type type = Type.findBy(code);
		return type.create(rawData);
	}

}
