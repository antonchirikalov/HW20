/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.bizerba;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.network.message.ExtendedMessage;
import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.MessageTransformer;
import com.toennies.ci1429.app.network.message.Messages;
import com.toennies.ci1429.app.util.ASCII;

/**
 * {@link MessageTransformer} for the Bizerba ST protocol.
 * @author renkenh
 */
class BizerbaMSGTransformer extends MessageTransformer
{

	private final static Logger logger = LogManager.getLogger();

	private static final byte[] ACK = new byte[] { ASCII.ACK.code };
	private static final byte[] NAK = new byte[] { ASCII.NAK.code };
	private static final byte[] ENQ = new byte[] { ASCII.ENQ.code };

	private byte[] header;

	
	/**
	 * Constructor.
	 * Use the various setup methods to setup the transformer.
	 */
	public BizerbaMSGTransformer()
	{
		//do nothing - setup later
	}
	
	/**
	 * @param startControl
	 * @param endControl
	 */
	public BizerbaMSGTransformer(String startControl, String header, String sepControl, String endControl)
	{
		super(startControl, sepControl, endControl);
		this.setupHeader(header);
	}

	
	public void setup(String startControl, String header, String sepControl, String endControl)
	{
		super.setup(startControl, sepControl, endControl);
		this.setupHeader(header);
	}

	public void setup(byte startControl[], byte[] header, byte[] sepControl, byte[] endControl)
	{
		super.setup(startControl, sepControl, endControl);
		this.setupHeader(header);
	}
	
	public void setupHeader(String header)
	{
		this.setupHeader(header != null ? header.getBytes(CHARSET) : null);
	}
	
	public void setupHeader(byte[] header)
	{
		this.header = header != null && header.length > 0 ? header : null;
	}
	
	protected boolean hasHeader()
	{
		return this.header != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean msgHasStartControl(byte[] msg)
	{
		if (msg.length == 1 && (msg[0] == ACK[0] || msg[0] == NAK[0] || msg[0] == ENQ[0]))
			return true;
		return super.msgHasStartControl(msg);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int findNextMSGEnd(byte[] buffer, int startIndex)
	{
		return searchForPattern(buffer, startIndex, ACK, NAK, ENQ, this.endControl);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IExtendedMessage createMessageFromData(byte[] data)
	{
		if (Arrays.equals(data, ACK) || Arrays.equals(data, NAK) || Arrays.equals(data, ENQ))
			return new ExtendedMessage(data);
		return super.createMessageFromData(data);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] formatMessage(IMessage msg) throws IOException
	{
		if (Messages.isACK(msg) || Messages.isENQ(msg) || Messages.isNAK(msg))
			return msg.words().get(0);

		ByteArrayOutputStream stream = new ByteArrayOutputStream(10);
		if (this.hasStartControl())
			stream.write(this.startControl);
		if (this.hasHeader())
		{
			stream.write(this.header);
			if (this.hasSepControl())
				stream.write(this.sepControl);
		}
		
		List<byte[]> words = msg.words();
		// words[] is empty for weigh automatic
		if (words != null && !words.isEmpty())
		{
			stream.write(words.get(0));
			for (int i = 1; i < words.size(); i++)
			{
				if (this.hasSepControl())
					stream.write(this.sepControl);
				stream.write(words.get(i));
			}
		}
		else
		{
			// FIXME: This else block can be removed, if Hendrik accepted changes from here
			// http://stash.toennies.net/projects/SBAPP/repos/ci1429app/pull-requests/62
			logger.warn("Message is null or empty. This may cause errors.");
		}
		stream.write(this.endControl);
		return stream.toByteArray();
	}

}
