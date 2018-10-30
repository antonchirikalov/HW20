/**
 * 
 */
package com.toennies.ci1429.app.network.message;

import java.util.List;

/**
 * @author renkenh
 *
 */
public class ExceptionMessage implements IExtendedMessage
{
	
	private final Exception ex;
	

	public ExceptionMessage(Exception ex)
	{
		this.ex = ex;
	}
	
	
	public Exception cause()
	{
		return this.ex;
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<byte[]> words()
	{
		throw new RuntimeException(this.cause());
	}


	@Override
	public int wordCount()
	{
		throw new RuntimeException(this.cause());
	}


	@Override
	public byte[] word(int index)
	{
		throw new RuntimeException(this.cause());
	}


	@Override
	public byte[] getRawData()
	{
		throw new RuntimeException(this.cause());
	}

}