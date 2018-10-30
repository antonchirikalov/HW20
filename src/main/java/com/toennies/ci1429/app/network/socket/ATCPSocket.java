/**
 * 
 */
package com.toennies.ci1429.app.network.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.util.IExecutors;

/**
 * Implementation to connect to a TCP socket. This type represents a "technical"
 * TCP connection. No assumption about the protocol is made. Use a protocol
 * implementation {@link IProtocol} as a higher level of logic how to
 * communication with a device.
 * 
 * @author renkenh
 */
@AtSocket("TCP Socket")
@Parameter(name = ATCPSocket.PARAM_PING, isRequired = true, typeInformation = "boolean", value = "true")
@Parameter(name = IFlexibleConnector.PARAM_TIMEOUT, isRequired = true, value = "1000")
public abstract class ATCPSocket extends AWireSocket implements ISocket
{

	/** The port on which to connect. */
	public static final String PARAM_PING = "Check Ping";

	private static final int PERIOD_HEALTHCHECK = 5000;
	private static final int TIMEOUT_HEALTHCHECK = 3000;

	private Runnable healthcheck = () ->
	{
		try
		{
			Socket socket = this.socket;
			if (socket != null)
				this.isReachable = socket.getInetAddress().isReachable(TIMEOUT_HEALTHCHECK);
		}
		catch (IOException e)
		{
			this.isReachable = false;
		}
		if (!this.isReachable)
			this.disconnect();
	};

	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(IExecutors.NETWORK_FACTORY);
	protected volatile Socket socket;
	private ScheduledFuture<?> healthCheckTask;
	private volatile boolean isReachable = false;

	@Override
	protected void _setupWireSocket(IConfigContainer config) throws IOException
	{
		int timeout = config.getIntEntry(IFlexibleConnector.PARAM_TIMEOUT);

		this.socket = _createSocket(config);
		setupSo(this.socket, timeout);
		if (this.healthCheckTask != null)
		{
			this.healthCheckTask.cancel(true);
			this.healthCheckTask = null;
		}
		if (config.getBooleanEntry(PARAM_PING))
			this.healthCheckTask = this.executor.scheduleAtFixedRate(this.healthcheck, PERIOD_HEALTHCHECK, PERIOD_HEALTHCHECK, TimeUnit.SECONDS);
	}

	protected abstract Socket _createSocket(IConfigContainer config) throws IOException;

	/**
	 * @return Whether the wire specific connection thinks it is connected or
	 *         not.
	 */
	protected boolean _isConnected()
	{
		return this.healthCheckTask == null || this.isReachable;
	}

	@Override
	protected InputStream getInputStream() throws IOException
	{
		return this.socket.getInputStream();
	}

	@Override
	protected OutputStream getOutputStream() throws IOException
	{
		return this.socket.getOutputStream();
	}

	@Override
	protected void _setdownWireSocket()
	{
		if (this.healthCheckTask != null)
		{
			this.healthCheckTask.cancel(true);
			this.healthCheckTask = null;
		}
	}

	/**
	 * Setups the given socket. Sets linger, timeout and keep alive (to true).
	 * 
	 * @param socket
	 *            Teh socket to setup.
	 * @param timeout
	 *            The timeout to set for
	 *            {@link Socket#setSoLinger(boolean, int)} and
	 *            {@link Socket#setSoTimeout(int)}.
	 * @return The socket which was given.
	 * @throws IOException
	 *             If the setup goes wrong.
	 */
	static final Socket setupSo(Socket socket, int timeout) throws IOException
	{
		socket.setKeepAlive(true);
		socket.setSoLinger(true, timeout);
		socket.setSoTimeout(timeout);
		return socket;
	}

}
