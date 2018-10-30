/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.isotcp.tpdu;

import java.io.IOException;

import com.toennies.ci1429.app.util.Binary;

/**
 * Disconnect request send by either communication partner. The other one may drop the connection after receiving.
 * @author renkenh
 */
public class DisconnectTPDU extends TPDU
{

	/**
	 * The raw data of a received tpdu.
	 * @param rawData The raw data.
	 */
	public DisconnectTPDU(byte[] rawData)
	{
		super(rawData);
	}

	/**
	 * Creates a disconnect tpdu with the given parameters.
	 * @param srcRef The src reference - should be the same as the one given during the connect.
	 * @param destRef The destination reference - should be the same as the one received from the server in the {@link ConnectResponseTPDU}.
	 * @param reason The reason why the disconnect takes place.
	 */
	public DisconnectTPDU(int srcRef, int destRef, short reason)
	{
		super(Type.DISCONNECT_REQUEST, 6, new byte[0]);
		Binary.putUShort(this.rawData, 6, srcRef);
		Binary.putUShort(this.rawData, 8, destRef);
		Binary.putUByte(this.rawData, 10, reason);
	}

	
	/**
	 * The src reference.
	 * @return The src reference.
	 */
	public int getSrcRef()
	{
		return Binary.getUShort(this.rawData, 6);
	}
	
	/**
	 * The destination reference.
	 * @return The destination reference.
	 */
	public int getDestRef()
	{
		return Binary.getUShort(this.rawData, 8);
	}

	/**
	 * The reason why the disconnect takes place.
	 * @return The reason for the disconnect.
	 */
	public short getReason()
	{
		return Binary.getUByte(this.rawData, 10);
	}

	@Override
	public boolean isValid()
	{
		return super.isValid() && this.getHeaderLength() == 6 && this.getReason() <= 4;
	}

	@Override
	public void validate() throws IOException
	{
		super.validate();
		
        if (this.getHeaderLength() != 6)
            throw new IOException("Header Length does not equal 6");
        
        if (this.getReason() > 4)
            throw new IOException("Syntax error: reason out of bound");
	}

}
