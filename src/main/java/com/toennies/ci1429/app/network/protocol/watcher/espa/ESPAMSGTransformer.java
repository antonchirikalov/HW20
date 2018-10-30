/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.watcher.espa;

import java.util.Arrays;

import com.toennies.ci1429.app.network.message.ExtendedMessage;
import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.message.MessageTransformer;
import com.toennies.ci1429.app.util.ASCII;

/**
 * The message transformer. Does separate the byte stream according to the espa protocol.
 * @author renkenh
 */
public class ESPAMSGTransformer extends MessageTransformer
{

	
	private static final byte[] ENQ = new byte[] { ASCII.ENQ.code };
	private static final byte[] ETX = new byte[] { ASCII.ETX.code };
	private static final byte[] EOT = new byte[] { ASCII.EOT.code };
	

	{
		this.setup(null, null, ENQ);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean msgHasStartControl(byte[] msg)
	{
		if (msg.length == 1 && (msg[0] == ETX[0] || msg[0] == EOT[0]))
			return true;
		return super.msgHasStartControl(msg);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int findNextMSGEnd(byte[] buffer, int startIndex)
	{
		return searchForPattern(buffer, startIndex, EOT, ETX, this.endControl);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IExtendedMessage createMessageFromData(byte[] data)
	{
		if (Arrays.equals(data, ETX) || Arrays.equals(data, EOT))
			return new ExtendedMessage(data);
		return super.createMessageFromData(data);
	}

}
