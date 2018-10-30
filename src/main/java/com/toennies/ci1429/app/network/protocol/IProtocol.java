package com.toennies.ci1429.app.network.protocol;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.event.IEventNotifier;
import com.toennies.ci1429.app.network.socket.ISocket;


/**
 * This type represents a vendor specific protocol.
 * Derived implementations implement vendor specific message construction and parsing.
 * Implementations should be annotated with {@link AtProtocol}.
 * 
 * @author renkenh
 */
public interface IProtocol extends IEventNotifier
{

	/** The {@link ISocket} to use at the end of the pipeline. */
	public static final String PARAM_SOCKET = "socket";

	/** Timeout for a request to the device pipeline. */
	public static final String PARAM_REQUEST_TIMEOUT = "request_timeout";

	/** Standardized parameter for devices to specify a default response. Not provided automatically. */
	public static final String PARAM_DEFAULT_RESPONSE = "Default Response";


	/**
	 * Setups the network stack with the given parameters. The given parameters are merged with the default
	 * parameters and then validated against the parameter specification. If this validation fails, the protocol
	 * is not {@link #isInitialized()}.
	 * 
	 * @param parameters The parameters to use for setup.
	 */
	public void setup(Map<String, String> parameters) throws IOException;

	/**
	 * Returns whether {@link #setup(Map)} has been called at least once without calling {@link #shutdown()} afterwards.
	 * @return Whether the protocol is initialized and attempts are made to connect to the hardware.
	 */
	public boolean isInitialized();
	
	/**
	 * @return Whether the network stack is connected to the hardware or not. Connection attempts are done automatically in the
	 * background.
	 */
	public boolean isConnected();

	/**
	 * Sends a request through the network stack to the hardware device.
	 * @param params The params of the request to send. See protocol implementations for advice.
	 * @return An object containing the result. See protocol implementations for advice.
	 * @throws IOException If something went wrong during sending.
	 * @throws TimeoutException if the sending could not be performed within a specific amount of time (see {@link IFlexibleConnector#PARAM_TIMEOUT}.
	 */
	public Object send(Object... params) throws IOException, TimeoutException;
	
	public Map<String, String> getConfig();

	/**
	 * Disconnects the network stack. 
	 */
	public void shutdown();
}
