/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.isotcp.tpdu;

import java.nio.charset.StandardCharsets;

/**
 * Connect request. Send by the client to the server. Contains several parameters that specify the transport capabilities of the client.
 * The server will answer with a {@link ConnectResponseTPDU} which contains parameters he accepts. These will never be higher than the parameters
 * specified by the client.
 * @author renkenh
 */
public class ConnectTPDU extends TPDU
{

	/**
	 * Creates a new connect request with a maximum size parameter of 16 and the src reference equal to 1.
	 * Src id and destination id are left blank.  
	 */
	public ConnectTPDU()
	{
		this((byte) 16, (short) 1, null, null);
	}
	
	/**
	 * Creates a new connect request with a maximum size parameter of 16 and the src reference equal to 1.
	 * @param srcID The source id.
	 * @param destID The destination id.
	 */
	public ConnectTPDU(String srcID, String destID)
	{
		this((byte) 16, (short) 1, srcID, destID);
	}
	
	/**
	 * Constructor with which all parameters can be specified at once.
	 */
	public ConnectTPDU(byte maxTPduSizeParam, short srcRef, String srcID, String destID)
	{
		super(Type.CONNECT_REQUEST, headerLength(srcID, destID), new byte[0]);
		//dest ref
		this.rawData[6] = 0;
		this.rawData[7] = 0;
		//src ref
		this.rawData[8] = (byte) (srcRef << 8);
		this.rawData[9] = (byte) (srcRef << 0);
		//transport class
		this.rawData[10] = 0;
		//max tpdu size proposal
		this.rawData[11] = (byte) 0xc0;
		this.rawData[12] = 1;
		this.rawData[13] = (byte) maxTPduSizeParam;

		int position = 14;
		if (destID != null)
		{
			this.rawData[position++] = (byte) 0xc2;
			this.rawData[position++] = (byte) destID.length();
			byte[] dest = destID.getBytes(StandardCharsets.US_ASCII);
			System.arraycopy(dest, 0, this.rawData, position, dest.length);
			position += dest.length;
		}
		if (srcID != null)
		{
			this.rawData[position++] = (byte) 0xc1;
			this.rawData[position++] = (byte) srcID.length();
			byte[] src = srcID.getBytes(StandardCharsets.US_ASCII);
			System.arraycopy(src, 0, this.rawData, position, src.length);
			position += src.length;
		}
		System.out.println("Length2 H:" + headerLength(srcID, destID));
	}
	
	private static final int headerLength(String srcID, String destID)
	{
		return 3 + 6 + (srcID != null ? 2 + srcID.length() : 0) + (destID != null ? 2 + destID.length() : 0);
	}
	
}
