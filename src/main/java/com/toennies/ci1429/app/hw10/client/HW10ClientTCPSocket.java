/**
 * 
 */
package com.toennies.ci1429.app.hw10.client;

import java.io.IOException;
import java.net.Socket;

import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.network.socket.ATCPSocket;
import com.toennies.ci1429.app.network.socket.ISocket;

/**
 * Implementation to connect to a TCP socket.
 * <p>
 * This type represents a "technical" TCP connection. No assumption about the
 * protocol is made. Use a protocol implementation {@link IProtocol} as a higher
 * level of logic how to communication with a device.
 * 
 * @author renkenh
 */
public class HW10ClientTCPSocket extends ATCPSocket implements ISocket
{

	public HW10ClientTCPSocket(Socket socket)
	{
		this.socket = socket;
	}

	@Override
	protected Socket _createSocket(IConfigContainer config) throws IOException
	{
		return this.socket;
	}

	@Override
	protected boolean _isConnected()
	{
		return this.socket != null && this.socket.isConnected() && !this.socket.isClosed() && super._isConnected();
	}
}