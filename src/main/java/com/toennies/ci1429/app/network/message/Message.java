/**
 * 
 */
package com.toennies.ci1429.app.network.message;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.toennies.ci1429.app.util.ASCII;


/**
 * @author renkenh
 *
 */
public class Message implements IMessage
{

	private byte[][] words;
	
	
	protected Message()
	{
		//do nothing
	}


	public Message(byte... word)
	{
		this.setup(new byte[][] { word });
	}

	/**
	 * 
	 */
	public Message(byte[]... words)
	{
		this.setup(words);
	}

	protected void setup(byte[]... words)
	{
		this.words = words;
	}

	@Override
	public List<byte[]> words()
	{
		return this.words != null && this.words.length > 0 ? Arrays.asList(this.words) : Collections.emptyList();
	}

	@Override
	public int wordCount()
	{
		return this.words != null ? this.words.length : 0;
	}

	@Override
	public byte[] word(int index)
	{
		return this.words[index];
	}
	
	@Override
	public String toString()
	{
		return Arrays.stream(this.words).map((word) -> ASCII.formatHuman(word)).collect(Collectors.joining("|", "'", "'"));
	}

}