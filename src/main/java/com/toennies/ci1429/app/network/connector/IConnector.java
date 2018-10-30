/**
 * 
 */
package com.toennies.ci1429.app.network.connector;

/**
 * Specialization of the {@link IFlexibleConnector}.
 * Defines IN and OUT of the flexible connector to be the same.
 * @author renkenh
 */
public interface IConnector<T> extends IFlexibleConnector<T, T>
{
	//nothing
}
