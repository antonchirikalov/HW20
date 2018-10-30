/**
 * 
 */
package com.toennies.ci1429.app.network.message;

import java.util.Arrays;
import java.util.List;

/**
 * @author renkenh
 *
 */
public class ExtendedMessage implements IExtendedMessage
{

	private final byte[] data;


	/**
	 * 
	 */
	public ExtendedMessage(byte[] data)
	{
		this.data = data;
	}


	@Override
	public int wordCount()
	{
		return 1;
	}

	@Override
	public byte[] word(int index)
	{
		if (index == 0)
			return data;
		throw new IndexOutOfBoundsException("Only index 0 is allowed.");
	}

	@Override
	public List<byte[]> words()
	{
		return Arrays.asList(this.data);
	}

	@Override
	public byte[] getRawData()
	{
		return this.data;
	}

}
