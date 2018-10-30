/**
 * 
 */
package com.toennies.ci1429.app.network.connector;

/**
 * A special interface that can be used to navigate through the network stack.
 * @author renkenh
 */
public interface IConnectorWrapper<CON extends IFlexibleConnector<IN, OUT>, IN, OUT>
{

	/**
	 * Returns the wrapped (subsequent) network stack. Please note, that the returned object
	 * may not implement this interface.
	 * @return The wrapped (subsequent) network stack.
	 */
	public CON getWrappedConnector();
}
