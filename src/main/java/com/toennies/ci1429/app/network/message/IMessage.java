package com.toennies.ci1429.app.network.message;

import java.util.List;

public interface IMessage
{
	/**
	 * The number of words this message contains.
	 * @return The number of words.
	 */
	public int wordCount();

	public byte[] word(int index);

	/**
	 * An unmodifiable list of words. The message is separated into its different words if a separator is available.
	 * If there is no separator, the list contains exactly one word.
	 * The words do NOT contain any separator or start- or end-pattern which where used to identify a message.
	 * @return A list of words.
	 */
	public List<byte[]> words();

}