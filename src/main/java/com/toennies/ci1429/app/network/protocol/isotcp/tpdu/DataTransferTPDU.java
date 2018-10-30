/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.isotcp.tpdu;

import java.io.IOException;

import com.toennies.ci1429.app.util.Binary;

/**
 * TPDU used to transfer user data. User data must be split and send in several {@link DataTransferTPDU}s if too large.
 * @author renkenh
 */
public class DataTransferTPDU extends TPDU
{

	/** Flag code in header that specifies that this tpdu is the last one and completes the user data send. After this tpdu a new blob of user data can be send. */
	public static final short EOF = Type.DISCONNECT_REQUEST.code;


	/**
	 * Constructor that takes the raw data and parses it.
	 * @param rawData The raw data.
	 */
	public DataTransferTPDU(byte[] rawData)
	{
		super(rawData);
	}

	/**
	 * Used if user data should be send to the server.
	 * @param packetNrEOF The package number (must be counting) or - if this is the last package set this parameter to {@link #EOF}.
	 * @param payload The actual payload to send.
	 */
	public DataTransferTPDU(short packetNrEOF, byte[] payload)
	{
		super(Type.DATA_TRANSFER, 2, payload);
		Binary.putUByte(this.rawData, 6, packetNrEOF);
	}


	/**
	 * The package number.
	 * @return The package number.
	 */
	public short getNr()
	{
		return Binary.getUByte(this.rawData, 6);
	}
	
	/**
	 * Returns whether this is the last package of a user data blob or not.
	 * @return Whether this is the last package.
	 */
	public boolean isEOF()
	{
		return this.getNr() == Type.DISCONNECT_REQUEST.code;
	}

	@Override
	public boolean isValid()
	{
		return super.isValid() && this.getHeaderLength() == 2;
	}

	@Override
	public void validate() throws IOException
	{
		super.validate();
		
        if (this.getHeaderLength() != 2)
            throw new IOException("Header Length does not equal 2");
	}

}
