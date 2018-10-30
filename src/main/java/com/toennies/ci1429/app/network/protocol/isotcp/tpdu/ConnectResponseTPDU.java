/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.isotcp.tpdu;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.toennies.ci1429.app.util.Binary;

/**
 * TPDU that contains the response from the rfc1006 server for a connect request ({@link ConnectTPDU}).
 * @author renkenh
 */
public class ConnectResponseTPDU extends TPDU
{
	
	private final int pos0xc0;
	private final int pos0xc1;
	private final int pos0xc2;


	/**
	 * Constructor. Takes the raw data and parses it.
	 * @param rawData The raw data.
	 */
	public ConnectResponseTPDU(byte[] rawData)
	{
		super(rawData);
		int position = 11;
		int tmp0xc0 = -1;
		int tmp0xc1 = -1;
		int tmp0xc2 = -1;
		while (position < this.rawData.length)
		{
			short c = Binary.getUByte(this.rawData, position);
			switch (c)
			{
				case 0xc0:
					tmp0xc0 = position;
					position += 2;
					break;
				case 0xc1:
					tmp0xc1 = position;
					position += 2 + Binary.getUByte(this.rawData, position);
					break;
				case 0xc2:
					tmp0xc2 = position;
					position += 2 + Binary.getUByte(this.rawData, position);
					break;
				default:
					throw new RuntimeException("Wrong Message format.");
			}
			position++;
		}
		this.pos0xc0 = tmp0xc0;
		this.pos0xc1 = tmp0xc1;
		this.pos0xc2 = tmp0xc2;
	}


	/**
	 * The src reference field. This is the reference provided by the connect request.
	 * @return The src reference.
	 */
	public int getSrcRef()
	{
		return Binary.getUShort(this.rawData, 6);
	}
	
	/**
	 * The destination reference field. This is the reference provided by the connect request.
	 * @return The destination reference.
	 */
	public int getDestRef()
	{
		return Binary.getUShort(this.rawData, 8);
	}

	/**
	 * The transportation class the server accepted. Usually, 0.
	 * @return The transportation class the server accepted.
	 */
	public byte getTransportClass()
	{
		return this.rawData[10];
	}

	/**
	 * The id of the source. This is the id provided by the client in the connect request.
	 * @return The source id.
	 */
	public String getSrcID()
	{
		if (this.pos0xc2 == -1)
			return null;
		int length = Binary.getUByte(this.rawData, this.pos0xc2+1);
		return new String(this.rawData, this.pos0xc2+2, length, StandardCharsets.US_ASCII);
	}
	
	/**
	 * The id of the destination. This is the id provided by the client in the connect request.
	 * @return The destination id.
	 */
	public String getDestID()
	{
		if (this.pos0xc1 == -1)
			return null;
		int length = Binary.getUByte(this.rawData, this.pos0xc1+1);
		return new String(this.rawData, this.pos0xc1+2, length, StandardCharsets.US_ASCII);
	}
	
	/**
	 * The maximum tpdu size parameter the server accepted. Messages must be shorter or equal to 2^param-1
	 * @return The maximum tpdu size parameter.
	 */
	public short getMaxTpduSizeParam()
	{
		if (this.pos0xc0 == -1)
			return -1;
		return Binary.getUByte(this.rawData, this.pos0xc0+2);
	}

	@Override
	public boolean isValid()
	{
		return super.isValid() && this.pos0xc0 != -1 && this.rawData[this.pos0xc0+1] == 1 && Binary.getUByte(this.rawData, this.pos0xc0+2) >= 7 && Binary.getUByte(this.rawData, this.pos0xc0+2) <= 16;
	}

	@Override
	public void validate() throws IOException
	{
		if (this.pos0xc0 == -1)
			throw new IOException("Header incomplete");

		if (this.rawData[this.pos0xc0+1] != 1)
			throw new IOException("maxTPduSizeParam field != 1");

		if (Binary.getUByte(this.rawData, this.pos0xc0+2) < 7)
			throw new IOException("maxTPduSizeParam < 7");

		if (Binary.getUByte(this.rawData, this.pos0xc0+2) > 16)
			throw new IOException("maxTPduSizeParam > 16");
	}

}
