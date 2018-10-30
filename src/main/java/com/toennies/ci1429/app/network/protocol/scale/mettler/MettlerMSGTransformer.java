/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.mettler;

import java.util.Arrays;

import com.toennies.ci1429.app.network.message.ExtendedMessage;
import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.message.MessageTransformer;
import com.toennies.ci1429.app.util.ASCII;

/**
 * {@link MessageTransformer} for the mettler scale network stack.
 * @author renkenh
 */
class MettlerMSGTransformer extends MessageTransformer
{

	
	private static final byte[] ACK = new byte[] { ASCII.ACK.code };
	private static final byte[] SYN = new byte[] { ASCII.SYN.code };

	

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean msgHasStartControl(byte[] msg)
	{
		if (msg.length == 1 && (msg[0] == ACK[0] || msg[0] == SYN[0]))
			return true;
		return super.msgHasStartControl(msg);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int findNextMSGEnd(byte[] buffer, int startIndex)
	{
		return searchForPattern(buffer, startIndex, ACK, SYN, this.endControl);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IExtendedMessage createMessageFromData(byte[] data)
	{
		if (Arrays.equals(data, ACK) || Arrays.equals(data, SYN))
			return new ExtendedMessage(data);
		return super.createMessageFromData(data);
	}

}
