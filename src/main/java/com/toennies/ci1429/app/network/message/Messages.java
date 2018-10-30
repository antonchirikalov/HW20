/**
 * 
 */
package com.toennies.ci1429.app.network.message;

import java.util.Arrays;
import java.util.Iterator;

import com.toennies.ci1429.app.util.ASCII;

/**
 * @author renkenh
 *
 */
public class Messages
{

	public static final boolean isENQ(IMessage msg)
	{
		return checkChar(msg, ASCII.ENQ);
	}

	public static final boolean isACK(IMessage msg)
	{
		return checkChar(msg, ASCII.ACK);
	}

	public static final boolean isNAK(IMessage msg)
	{
		return checkChar(msg, ASCII.NAK);
	}

	public static final boolean isSYN(IMessage msg)
	{
		return checkChar(msg, ASCII.SYN);
	}

	private static final boolean checkChar(IMessage msg, ASCII ascii)
	{
		if (msg == null || msg.words().size() != 1)
			return false;
		byte[] word = msg.words().get(0);
		return word.length == 1 && word[0] == ascii.code;
	}

	/**
	 * This method checks whether two messages are the same regarding reference
	 * and content.
	 * 
	 * @return true if a and b reference the same object (or a and b are null)
	 *         and true if a and b have the same message content. False
	 *         otherwise.
	 */
	public static final boolean equals(IMessage a, IMessage b)
	{
		if (a == b)
			return true;
		if (a == null || b == null)
			return false;
		if (a.words().size() != b.words().size())
			return false;

		Iterator<byte[]> ai = a.words().iterator();
		Iterator<byte[]> bi = b.words().iterator();
		while (ai.hasNext())
		{
			byte[] ab = ai.next();
			byte[] bb = bi.next();
			if (!Arrays.equals(ab, bb))
				return false;
		}
		return true;
	}

	private Messages()
	{
		// no instance
	}

}
