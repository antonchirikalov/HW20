
package com.toennies.ci1429.app.hw10.client;

import java.io.IOException;
import java.util.Arrays;

import com.toennies.ci1429.app.network.message.ExtendedMessage;
import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.MessageTransformer;
import com.toennies.ci1429.app.network.message.Messages;
import com.toennies.ci1429.app.util.ASCII;

/**
 * The {@link #HW10MSGTransformer()} is used to create a message-transforming.
 * <p>
 * It uses the implementation of {@link MessageTransformer} to control and
 * create a message from data {@link #createMessageFromData(byte[])}.
 * 
 * @author renkenh
 */
public class HW10MSGTransformer extends MessageTransformer
{
	
	protected static final byte[] ACK = new byte[] { ASCII.ACK.code };
	protected static final byte[] NAK = new byte[] { ASCII.NAK.code };

	{
		this.setup(new byte[] { ASCII.SOH.code }, null , new byte[] { ASCII.ETX.code });
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean msgHasStartControl(byte[] msg)
	{

		if (msg.length == 1 && (msg[0] == ACK[0]) || msg[0] == NAK[0])
			return true;
		return super.msgHasStartControl(msg);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int findNextMSGEnd(byte[] buffer, int startIndex)
	{
		return searchForPattern(buffer, startIndex, ACK, this.endControl, NAK);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] formatMessage(IMessage msg) throws IOException
	{
		if (Messages.isACK(msg))
			return msg.words().get(0);
		if (Messages.isNAK(msg))
			return msg.words().get(0);
		return super.formatMessage(msg);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IExtendedMessage createMessageFromData(byte[] data)
	{
		if (Arrays.equals(data, ACK) || Arrays.equals(data, NAK))
			return new ExtendedMessage(data);
		return super.createMessageFromData(data);
	}
}