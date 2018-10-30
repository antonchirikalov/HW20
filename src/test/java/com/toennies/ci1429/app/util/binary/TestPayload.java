/**
 * 
 */
package com.toennies.ci1429.app.util.binary;

import com.toennies.ci1429.app.util.binary.model.BField.BType;
import com.toennies.ci1429.app.util.binary.pojo.AtBField;
import com.toennies.ci1429.app.util.binary.pojo.AtDynBField;
import com.toennies.ci1429.app.util.binary.pojo.AtFixedBField;
import com.toennies.ci1429.app.util.binary.pojo.AtTypedBField;

/**
 * @author renkenh
 *
 */
public class TestPayload
{

	@AtBField
	private byte abyte;
	@AtBField
	private short ashort;
	@AtBField
	private int aint;
	@AtBField
	private float afloat;
	@AtBField
	private double adouble;
	@AtDynBField(BType.USHORT)
	private String astring;
	@AtFixedBField(5)
	private byte[] raw;
	@AtBField
	private char achar;
	@AtBField
	private long along;
	@AtTypedBField(BType.UBYTE)
	private short ubyte;
	@AtTypedBField(BType.USHORT)
	private int ushort;
	@AtTypedBField(BType.UINT)
	private long uint;


	public byte getAbyte()
	{
		return abyte;
	}
	public void setAbyte(byte abyte)
	{
		this.abyte = (byte) abyte;
	}
	public short getAshort()
	{
		return ashort;
	}
	public void setAshort(int ashort)
	{
		this.ashort = (short) ashort;
	}
	public int getAint()
	{
		return aint;
	}
	public void setAint(int aint)
	{
		this.aint = (int) aint;
	}
	public float getAfloat()
	{
		return afloat;
	}
	public void setAfloat(float afloat)
	{
		this.afloat = afloat;
	}
	public double getAdouble()
	{
		return adouble;
	}
	public void setAdouble(double adouble)
	{
		this.adouble = adouble;
	}
	public String getAstring()
	{
		return astring;
	}
	public void setAstring(String astring)
	{
		this.astring = astring;
	}
	public byte[] getRaw()
	{
		return raw;
	}
	public void setRaw(byte[] raw)
	{
		this.raw = raw;
	}
	public char getAchar()
	{
		return achar;
	}
	public void setAchar(char achar)
	{
		this.achar = achar;
	}
	public long getAlong()
	{
		return along;
	}
	public void setAlong(long along)
	{
		this.along = along;
	}
	public short getUbyte()
	{
		return ubyte;
	}
	public void setUbyte(short ubyte)
	{
		this.ubyte = ubyte;
	}
	public int getUshort()
	{
		return ushort;
	}
	public void setUshort(int ushort)
	{
		this.ushort = ushort;
	}
	public long getUint()
	{
		return uint;
	}
	public void setUint(long uint)
	{
		this.uint = uint;
	}
	
}
