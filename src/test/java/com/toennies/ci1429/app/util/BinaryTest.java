/**
 * 
 */
package com.toennies.ci1429.app.util;

import static org.junit.Assert.*;

import java.io.DataInputStream;
import java.io.IOException;

import org.apache.derby.iapi.services.io.ArrayInputStream;
import org.junit.Test;

/**
 * @author renkenh
 *
 */
public class BinaryTest
{

	/**
	 * Test method for {@link com.toennies.ci1429.app.util.Unsigned#uByte(byte[], int)}.
	 */
	@Test
	public void testGetUByte() throws IOException
	{
		byte[] arr = { (byte) 255 };
		DataInputStream in = new DataInputStream(new ArrayInputStream(arr));
		assertEquals(in.readUnsignedByte(), Binary.getUByte(arr, 0));
	}

	/**
	 * Test method for {@link com.toennies.ci1429.app.util.Unsigned#uByte(byte[], int, short)}.
	 */
	@Test
	public void testSetUByte() throws IOException
	{
		byte[] arr = { 0 };
		Binary.putUByte(arr, 0, (short) 255);
		DataInputStream in = new DataInputStream(new ArrayInputStream(arr));
		assertEquals(in.readUnsignedByte(), 255);
	}

	/**
	 * Test method for {@link com.toennies.ci1429.app.util.Unsigned#uShort(byte[], int)}.
	 */
	@Test
	public void testUShortByteArrayInt() throws IOException
	{
		byte[] arr = { (byte) 123, (byte) 567 };
		DataInputStream in = new DataInputStream(new ArrayInputStream(arr));
		assertEquals(in.readUnsignedShort(), Binary.getUShort(arr, 0));
	}

	/**
	 * Test method for {@link com.toennies.ci1429.app.util.Unsigned#uShort(byte[], int, int)}.
	 */
	@Test
	public void testUShortByteArrayIntInt() throws IOException
	{
		byte[] arr = { 0, 0 };
		Binary.putUShort(arr, 0, (short) 65000);
		DataInputStream in = new DataInputStream(new ArrayInputStream(arr));
		assertEquals(in.readUnsignedShort(), 65000);
	}

}
