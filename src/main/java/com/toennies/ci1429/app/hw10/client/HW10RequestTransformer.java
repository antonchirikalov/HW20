package com.toennies.ci1429.app.hw10.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.toennies.ci1429.app.network.connector.AWrapperTransformer;
import com.toennies.ci1429.app.network.connector.IConnector;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Message;
import com.toennies.ci1429.app.util.ASCII;

/**
 * This class is used to control the Message-transformation received from remote
 * clients.
 *
 * @author renkenh
 */
public class HW10RequestTransformer extends AWrapperTransformer<String, IMessage>
{

	/**
	 * Constructor.
	 * 
	 * @param wrapped
	 *            this connector is inherited from {@link IConnector} in order
	 *            to send (OUT) and receive (IN) remote data.
	 */
	public HW10RequestTransformer(IConnector<IMessage> wrapped)
	{
		super(wrapped);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String transformToOut(IMessage entity)
	{
		return new String(entity.words().get(0), StandardCharsets.US_ASCII);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void push(String entity) throws IOException
	{
		for (IMessage m : this.multiTransformToConIn(entity))
			this.connector.push(m);
	}

	private List<IMessage> multiTransformToConIn(String entity)
	{
		List<IMessage> msgs = new ArrayList<>(2);
		int start = 0;
		int end = this.indexOfEnd(entity, start);
		while (end != -1)
		{
			String msg = entity.substring(start, end);
			msgs.add(this.transformToConIn(msg));
			start = end;
			end = this.indexOfEnd(entity, start);
		}
		return msgs;
	}

	/**
	 * TODO
	 */
	private int indexOfEnd(String entity, int start)
	{
		if (start == entity.length())
			return -1;
		int indexOf = this.indexOfACKNAK(entity, start);
		if (indexOf == -1)
			return entity.length();
		if (start < indexOf)
			return indexOf;
		return start + 1;
	}

	/**
	 * TODO
	 */
	private int indexOfACKNAK(String entity, int start)
	{
		final int ack = entity.indexOf(ASCII.ACK.c, start);
		final int nak = entity.indexOf(ASCII.NAK.c, start);
		if (ack == -1 && nak == -1)
			return -1;
		if (ack != -1 && nak != -1)
			return Math.min(ack, nak);
		if (ack != -1)
			return ack;
		return nak;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IMessage transformToConIn(String entity)
	{
		return new Message(entity.getBytes(StandardCharsets.US_ASCII));
	}
}