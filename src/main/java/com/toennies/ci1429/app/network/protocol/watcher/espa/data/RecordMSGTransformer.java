/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.watcher.espa.data;

import com.toennies.ci1429.app.network.message.MessageTransformer;
import com.toennies.ci1429.app.util.ASCII;

/**
 * Message transformer to separate an esap call into its different records.
 * @author renkenh
 */
public class RecordMSGTransformer extends MessageTransformer
{

	private static final byte[] US = new byte[] { ASCII.US.code };
	private static final byte[] RS = new byte[] { ASCII.RS.code };
	private static final byte[] ETX = new byte[] { ASCII.ETX.code };

	{
		this.setup(null, US, ETX);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int findNextMSGEnd(byte[] buffer, int startIndex)
	{
		return searchForPattern(buffer, startIndex, RS, this.endControl);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int findNextMSGSegment(byte[] buffer, int startIndex)
	{
		return searchForPattern(buffer, startIndex, US, RS, this.endControl);
	}

}
