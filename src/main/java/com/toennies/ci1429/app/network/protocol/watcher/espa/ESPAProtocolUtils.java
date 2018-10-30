/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.watcher.espa;

import java.nio.charset.StandardCharsets;

import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.protocol.watcher.espa.data.ESPACall;
import com.toennies.ci1429.app.util.ASCII;

/**
 * Utils class to parse ESPA protocol messages.
 * @author renkenh
 */
final class ESPAProtocolUtils
{
	
	/**
	 * Returns whether in the given message EOT is contained.
	 * @param msg The message.
	 * @return <code>true</code> if {@link ASCII#EOT} is contained somewhere in the message. <code>false</code> otherwise.
	 */
	static final boolean containsEOT(IMessage msg)
	{
		for (byte[] word : msg.words())
			for (byte b : word)
				if (b == ASCII.EOT.code)
					return true;
		return false;
	}

	/**
	 * Returns whether the message contains a singe byte which represents {@link ASCII#EOT}.
	 * @param msg The message to check.
	 */
	static final boolean isEOT(IMessage msg)
	{
		byte[] word = msg.words().get(0);
		return word.length == 1 && word[0] == ASCII.EOT.code;
	}

	/**
	 * Returns whether the message is a select message according to the espa protocol.
	 * @param msg The message to check.
	 */
	static final boolean isSelect(IMessage msg)
	{
		byte[] word = msg.words().get(0);
		return word.length > 0 && word[word.length-1] == ASCII.ENQ.code;
	}
	
	/**
	 * Returns whether the message is a select message and furthermore contains the given address.
	 * @param msg The message to check.
	 */
	static final boolean isMeSelected(IMessage msg, String address)
	{
		byte[] word = msg.words().get(0);
		if (isSelect(msg))
		{
			byte[] addressArray = address.getBytes(StandardCharsets.US_ASCII);
			for (int i = 0; i < addressArray.length; i++)
				if (word[i] != addressArray[i])
					return false;
			return true;
		}
		return false;
	}

	/**
	 * Returns whether the message is a select message that does not reference the given address.
	 * @param msg The message to check.
	 */
	static final boolean isOtherSelected(IMessage msg, String address)
	{
		return isSelect(msg) && !isMeSelected(msg, address);
	}

	/**
	 * Returns whether the message is a datablock or not (i.e. {@link ASCII#ETX} must be the last character.
	 * @param msg The message to check.
	 */
	static final boolean isDatablock(IMessage msg)
	{
		byte[] word = msg.words().get(0);
		return word.length > 0 && word[word.length-1] == ASCII.ETX.code;
	}
	
	/**
	 * Returns whether the message contains a BCC code. This is a special method that only works for the ESPA
	 * protocol provided by the BMZs.
	 * @param msg The message to check.
	 */
	static final boolean isBCC(IMessage msg)
	{
		byte[] word = msg.words().get(0);
		return word.length == 2 && word[word.length-1] == ASCII.EOT.code;
	}
	
	/**
	 * Returns the given BCC of an ESPA call. Each ESPA call provides a BCC for validation. This BCC is extracted.
	 * To compute the BCC for a given call use {@link #computeBCC(IMessage)}. 
	 * @param msg The message containing the call.
	 * @return The bcc provided by the espa call.
	 */
	static final int extractBCC(IMessage msg)
	{
		if (!isBCC(msg))
			return -1;
		byte[] word = msg.words().get(0);
		return word[0];
	}
	
	/**
	 * Returns whether this message contains a call or not.
	 * @param msg The message that may be a call.
	 * @return Whether the message represents a call or not.
	 */
	static final boolean isCall(IMessage msg)
	{
		if (!isDatablock(msg))
			return false;
		byte[] word = msg.words().get(0);
		return word[1] == ESPACall.EventType.CALL_TO_PAGER.ascii();
	}
	
	/**
	 * @param msg The message to check.
	 * @return Whether the given message is a request or not.
	 */
	static final boolean isRequest(IMessage msg)
	{
		if (!isDatablock(msg))
			return false;
		byte[] word = msg.words().get(0);
		return word[1] == ESPACall.EventType.STATUS_REQUEST.ascii();
	}
	
	/**
	 * Computes the BCC for a given call. If you want to extract the bcc given with the call, use {@link #extractBCC(IMessage)}.
	 * @param msg The message for which to compute the bcc.
	 * @return The _computed_ bcc for the given message.
	 */
	static byte computeBCC(IMessage msg)
	{
		if (!isDatablock(msg))
			return -1;
		byte[] word = msg.words().get(0);
		return bcc(word);
	}

	/**
	 * The actual computation bcc algorithm according to espa protocol standard.
	 */
	private static byte bcc(byte... packet)
	{
		int csum = 0;
		for (int i = 1; i < packet.length; i++)
			csum ^= packet[i];
		
		return (byte) (csum & 0x7f);
	}
	
	
	private ESPAProtocolUtils()
	{
		//no instance
	}

}
