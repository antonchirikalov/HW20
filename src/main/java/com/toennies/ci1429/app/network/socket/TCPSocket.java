/**
 * 
 */
package com.toennies.ci1429.app.network.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.util.Utils;



/**
 * Implementation to connect to a TCP socket.
 * This type represents a "technical" TCP connection. No assumption about the protocol is made.
 * Use a protocol implementation {@link IProtocol} as a higher level of logic how to communication with a device.
 * @author renkenh
 */
@AtSocket("TCP Socket")
@Parameter(name=TCPSocket.PARAM_HOST, isRequired=true, toolTip="The host address. May be a DNS name or an IP address.")
@Parameter(name=TCPSocket.PARAM_PORT, isRequired=true, typeInformation="int:0..65535", toolTip="The port on the server.")
public class TCPSocket extends ATCPSocket implements ISocket
{
	
	/** The host to connect to. */
	public static final String PARAM_HOST = "host";
	/** The port on which to connect. */
	public static final String PARAM_PORT = "port";
	
	@Override
	protected Socket _createSocket(IConfigContainer config) throws IOException
	{
		String host = config.getEntry(PARAM_HOST);
		int port = config.getIntEntry(PARAM_PORT);
		int timeout = config.getIntEntry(IFlexibleConnector.PARAM_TIMEOUT);
		return connectTo(host, port, timeout);
	}
	

	@Override
	protected boolean _isConnected()
	{
		Socket instance = this.socket;
		return instance != null && instance.isConnected() && !instance.isClosed() && super._isConnected();
	}

	@Override
	protected void _setdownWireSocket()
	{
		if (this.socket != null)
			Utils.close(this.socket);
		this.socket = null;
		super._setdownWireSocket();
	}

	
	/**
	 * Convenient method to create a socket with specific parameters.
	 * This method especially sets
	 * {@link Socket#setKeepAlive(boolean)} to true
	 * {@link Socket#setSoLinger(boolean, int)} to true with the given timeout
	 * {@link Socket#setSoTimeout(int)} to the given timeout.
	 * @param host The host to connect to.
	 * @param port The port on which to connect.
	 * @param timeout The timeout used for the different settings (see above).
	 * @return A socket that is connected.
	 * @throws IOException If the socket could not be created or connected.
	 */
	static final Socket connectTo(String host, int port, int timeout) throws IOException
	{
		Socket socket = new Socket();
		try
		{
			socket.setKeepAlive(true);
			socket.setSoLinger(true, timeout);
			socket.setSoTimeout(timeout);
			socket.connect(new InetSocketAddress(host, port), timeout / 2);
		}
		catch (SocketTimeoutException ex)
		{
			throw new IOException("Could not establish connection to device.", ex);
		}
		return socket;
	}

}
