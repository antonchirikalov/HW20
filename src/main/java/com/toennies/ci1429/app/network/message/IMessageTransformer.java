package com.toennies.ci1429.app.network.message;

import java.io.IOException;
import java.util.List;

public interface IMessageTransformer
{

	/** This is the maximum size a message from a device can be. Arbitrarily chosen. */
	public static final int MAX_MSG_SIZE = 8096; //8KB


	public void clear();

	public List<IExtendedMessage> parseData(byte[] read) throws IOException;

	public byte[] formatMessage(IMessage msg) throws IOException;

}