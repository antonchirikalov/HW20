/**
 * 
 */
package com.toennies.ci1429.app.network.socket;

import com.toennies.ci1429.app.network.connector.IConnector;

/**
 * A special connector interface that defines the end of a network stack.
 * The socket holds the actual connection to the hardware device.
 * 
 * Implementations should be annotated with {@link AtSocket}.
 * @author renkenh
 */
public interface ISocket extends IConnector<byte[]>
{
	//nothing - just parameterize IConnector
}
