/**
 * 
 */
package com.toennies.ci1429.app.util;

import java.nio.charset.StandardCharsets;

/**
 * Utilities to read and write values of different types from and into a byte array.
 * It even supports writing unsigned values to communicate with third-party hardware (where necessary).
 * The data is written in big endian format.
 * @author renkenh
 */
public class Binary
{

	/**
	 * Reads the value at given offset and returns whether the value is larger than zero (= true) otherwise false.
	 * @param b The byte array.
	 * @param off The offset.
	 */
	public static boolean getBool(byte[] b, int off)
	{
		return b[off] > 0;
	}

	/**
	 * Writes the given value at the given offset into the given array.
	 * For true, value 1 is written for false 0.
	 * @param b The array.
	 * @param off The offset.
	 * @param bool The value.
	 */
	public static void putBool(byte[] b, int off, boolean bool)
	{
		b[off] = bool ? (byte)1 : (byte)0;
	}

	/**
	 * Reads an unsigned byte from the given offset. Since java does not support unsigned values a short is returned.
	 * @param buffer The buffer from which to read.
	 * @param index The offset.
	 * @return The unsigned byte value as a short.
	 */
	public static short getUByte(byte[] buffer, int index)
	{
		return (short)(buffer[index] & 0xff);
	}

	/**
	 * Write an unsigned byte value into the given buffer. Does not check whether the given value is out of range (0 <= value <= 255).
	 * @param buffer The buffer into which to write the value.
	 * @param index The offset.
	 * @param value The value.
	 */
	public static void putUByte(byte[] buffer, int index, short value)
	{
		buffer[index] = (byte)(value & 0xff);
	}

	/**
	 * Reads a short value from the given array.
	 * @param buffer The buffer from which to read.
	 * @param index The offset.
	 * @return The read value
	 */
	public static short getShort(byte[] buffer, int index)
	{
		short b1 = buffer[index];
		short b2 = buffer[index+1];
		return (short) ((b1 << 8) + (b2 << 0));
	}

	/**
	 * Writes the given value into the array.
	 * @param buffer The array.
	 * @param index The offset.
	 * @param value The value
	 */
	public static void putShort(byte[] buffer, int index, short value)
	{
		buffer[index]   = (byte) (value >>> 8);
		buffer[index+1] = (byte) (value >>> 0);
	}
	
	/**
	 * Reads an unsigned short from the given offset. Since java does not support unsigned values an int is returned.
	 * @param buffer The buffer from which to read.
	 * @param index The offset.
	 * @return The unsigned short value as an int.
	 */
	public static int getUShort(byte[] buffer, int index)
	{
		int b1 = buffer[index] & 0xff;
		int b2 = buffer[index+1] & 0xff;
		return (b1 << 8) + (b2 << 0);
	}

	/**
	 * Write an unsigned short value into the given buffer. Does not check whether the given value is out of range (0 <= value <= 2^16-^).
	 * @param buffer The buffer into which to write the value.
	 * @param index The offset.
	 * @param value The value.
	 */
	public static void putUShort(byte[] buffer, int index, int value)
	{
		buffer[index]   = (byte)((value >>> 8) & 0xff);
		buffer[index+1] = (byte)((value >>> 0) & 0xff);
	}

	/**
	 * Reads an int value from the given array.
	 * @param buffer The buffer from which to read.
	 * @param index The offset.
	 * @return The read value
	 */
	public static int getInt(byte[] b, int off)
	{
		return ((b[off + 3] & 0xFF) << 0) + ((b[off + 2] & 0xFF) << 8) + ((b[off + 1] & 0xFF) << 16) + ((b[off + 0] & 0xFF) << 24);
	}

	/**
	 * Writes the given value into the array.
	 * @param b The array.
	 * @param off The offset.
	 * @param val The value
	 */
	public static void putInt(byte[] b, int off, int val)
	{
		b[off + 3] = (byte)(val >>> 0);
		b[off + 2] = (byte)(val >>> 8);
		b[off + 1] = (byte)(val >>> 16);
		b[off + 0] = (byte)(val >>> 24);
	}

	/**
	 * Reads an unsigned int from the given offset. Since java does not support unsigned values a long is returned.
	 * @param buffer The buffer from which to read.
	 * @param index The offset.
	 * @return The unsigned int value as a long.
	 */
	public static long getUInt(byte[] buffer, int index)
	{
		long b1 = buffer[index] & 0xff;
		long b2 = buffer[index+1] & 0xff;
		long b3 = buffer[index+2] & 0xff;
		long b4 = buffer[index+3] & 0xff;
		return (b1 << 24) + (b2 << 16) + (b3 << 8) + (b4 << 0);
	}

	/**
	 * Write an unsigned int value into the given buffer. Does not check whether the given value is out of range (0 <= value <= 2^32-^).
	 * @param buffer The buffer into which to write the value.
	 * @param index The offset.
	 * @param value The value.
	 */
	public static void putUInt(byte[] buffer, int index, long value)
	{
		buffer[index]   = (byte)((value >>> 24) & 0xff);
		buffer[index+1] = (byte)((value >>> 16) & 0xff);
		buffer[index+2] = (byte)((value >>> 8) & 0xff);
		buffer[index+3] = (byte)((value >>> 0) & 0xff);
	}

	/**
	 * Reads a long value from the given array.
	 * @param b The buffer from which to read.
	 * @param off The offset.
	 * @return The read value
	 */
	public static long getLong(byte[] b, int off)
	{
		return ((b[off + 7] & 0xFFL) << 0) + ((b[off + 6] & 0xFFL) << 8) + ((b[off + 5] & 0xFFL) << 16) + ((b[off + 4] & 0xFFL) << 24)
			+ ((b[off + 3] & 0xFFL) << 32) + ((b[off + 2] & 0xFFL) << 40) + ((b[off + 1] & 0xFFL) << 48) + ((b[off + 0] & 0xFFL) << 56);
	}

	/**
	 * Writes the given value into the array.
	 * @param b The array.
	 * @param off The offset.
	 * @param val The value
	 */
	public static void putLong(byte[] b, int off, long val)
	{
		b[off + 7] = (byte)(val >>> 0);
		b[off + 6] = (byte)(val >>> 8);
		b[off + 5] = (byte)(val >>> 16);
		b[off + 4] = (byte)(val >>> 24);
		b[off + 3] = (byte)(val >>> 32);
		b[off + 2] = (byte)(val >>> 40);
		b[off + 1] = (byte)(val >>> 48);
		b[off + 0] = (byte)(val >>> 56);
	}

	/**
	 * Reads a float value from the given array.
	 * @param b The buffer from which to read.
	 * @param off The offset.
	 * @return The read value
	 */
	public static float getFloat(byte[] b, int off)
	{
		return Float.intBitsToFloat(Binary.getInt(b, off));
	}

	/**
	 * Writes the given value into the array.
	 * @param b The array.
	 * @param off The offset.
	 * @param val The value
	 */
	public static void putFloat(byte[] b, int off, float val)
	{
		final int j = Float.floatToIntBits(val);
		b[off + 3] = (byte)(j >>> 0);
		b[off + 2] = (byte)(j >>> 8);
		b[off + 1] = (byte)(j >>> 16);
		b[off + 0] = (byte)(j >>> 24);
	}

	/**
	 * Reads a double value from the given array.
	 * @param b The buffer from which to read.
	 * @param off The offset.
	 * @return The read value
	 */
	public static double getDouble(byte[] b, int off)
	{
		return Double.longBitsToDouble(getLong(b,off));
	}

	/**
	 * Writes the given value into the array.
	 * @param b The array.
	 * @param off The offset.
	 * @param val The value
	 */
	public static void putDouble(byte[] b, int off, double val)
	{
		final long j = Double.doubleToLongBits(val);
		b[off + 7] = (byte)(j >>> 0);
		b[off + 6] = (byte)(j >>> 8);
		b[off + 5] = (byte)(j >>> 16);
		b[off + 4] = (byte)(j >>> 24);
		b[off + 3] = (byte)(j >>> 32);
		b[off + 2] = (byte)(j >>> 40);
		b[off + 1] = (byte)(j >>> 48);
		b[off + 0] = (byte)(j >>> 56);
	}

	/**
	 * Reads an array with a given size from the given array.
	 * @param b The buffer from which to read.
	 * @param off The offset.
	 * @param length The length of the array to read.
	 * @return The read value
	 */
	public static byte[] getArray(byte[] b, int off, int length)
	{
		byte[] value = new byte[length];
		System.arraycopy(b, off, value, 0, length);
		return value;
	}

	/**
	 * Writes the given value into the array.
	 * @param b The array.
	 * @param off The offset.
	 * @param val The value
	 */
	public static void putArray(byte[] b, int off, byte[] value)
	{
		System.arraycopy(value, 0, b, off, value.length);
	}

	/**
	 * Reads a string with a given size from the given array. Charset used for conversion is UTF-8.
	 * @param b The buffer from which to read.
	 * @param off The offset.
	 * @param length The length of the array to read - in bytes.
	 * @return The read value
	 */
	public static String getString(byte[] b, int off, int length)
	{
		return new String(getArray(b, off, length), StandardCharsets.UTF_8);
	}

	/**
	 * Writes the given value into the array. Charset used for conversion is UTF-8.
	 * @param b The array.
	 * @param off The offset.
	 * @param val The value
	 */
	public static void putString(byte[] b, int off, String value)
	{
		putArray(b, off, value.getBytes(StandardCharsets.UTF_8));
	}

}
