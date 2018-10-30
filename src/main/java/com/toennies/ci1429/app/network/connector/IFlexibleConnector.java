/**
 * 
 */
package com.toennies.ci1429.app.network.connector;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.toennies.ci1429.app.network.parameter.IConfigContainer;

/**
 * The most generic part in a network stack. It can have different types for IN and OUT.
 * @author renkenh
 */
public interface IFlexibleConnector<IN, OUT>
{

	/**
	 * Timeout for blocking operations, such as {@link #connect(IConfigContainer)} and {@link #pop()} and {@link #disconnect()}.
	 */
	public static final String PARAM_TIMEOUT = "timeout";


	/**
	 * Implement this method to connect to the remote system. The implementor must have no concerns regarding
	 * thread safety or timeout detection. This is all taken care of by the calling implementation.
	 * @throws IOException If the connection could not be established.
	 */
	public void connect(IConfigContainer config) throws IOException;
	
	/**
	 * Implement this method to return information about the health of the connection to the remote system.
	 * The implementor must have no concerns regarding thread safety or timeout detection.
	 * This is all taken care of by the calling implementation.
	 */
	public boolean isConnected();

	/**
	 * Non-blocking method to get OUT data. If no data is available <code>null</code> is returned.
	 * @return Data if available, otherwise <code>null</code>.
	 * @throws IOException If the underlying network stack throws an exception.
	 */
	public OUT poll() throws IOException;
	
	/**
	 * Blocking (for {@link #PARAM_TIMEOUT} amount of time).
	 * @return data if available within the specified amount of time. 
	 * @throws IOException If the underlying network stack throws an exception.
	 * @throws TimeoutException If the specified amount of time notably elapses before data got available.
	 */
	public OUT pop() throws IOException, TimeoutException;
	
	/**
	 * Implement this method to send data to the remote system. The implementor must have no concerns regarding
	 * thread safety or timeout detection. This is all taken care of by the calling implementation.
	 * @throws IOException If the connection could not be established.
	 */
	public void push(IN entity) throws IOException;
	
	/**
	 * Implement this method to disconnect from the remote system. The implementor must have no concerns regarding
	 * thread safety or timeout detection. This is all taken care of by the calling implementation.
	 *
	 * Disconnection supports two modes. One to cleanly shutdown and one
	 * to enforce the shutdown. Please note, that the enforced shutdown may never be blocked by other calls
	 * or thread problems. If the method is called with force = true, just quietly kill everything (remove references, etc.)
	 * Circumvent any calls to the original connection or any locking mechanisms. 
	 * @param force Defines, whether the disconnect should be done cleanly or dirty.
	 */
	public void disconnect() throws IOException;
	
	/**
	 * Implement this method to disconnect from the remote system. The implementor must have no concerns regarding
	 * thread safety or timeout detection. This is all taken care of by the calling implementation.
	 *
	 * Disconnection supports two modes. One to cleanly shutdown and one
	 * to enforce the shutdown. Please note, that the enforced shutdown may never be blocked by other calls
	 * or thread problems. If the method is called with force = true, just quietly kill everything (remove references, etc.)
	 * Circumvent any calls to the original connection or any locking mechanisms. 
	 * @param force Defines, whether the disconnect should be done cleanly or dirty.
	 */
	public void shutdown();

}
