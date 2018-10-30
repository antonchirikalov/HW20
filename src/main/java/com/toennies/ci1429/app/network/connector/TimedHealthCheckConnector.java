/**
 * 
 */
package com.toennies.ci1429.app.network.connector;

/**
 * @author renkenh
 *
 */
public class TimedHealthCheckConnector<T> extends FlexibleTimedHealthCheckTransformer<T, T> implements IConnector<T>
{

	/**
	 * @param connector
	 * @param healthCheck
	 */
	public TimedHealthCheckConnector(IConnector<T> connector, ITimedHealthCheck<T, T> healthCheck)
	{
		super(connector, healthCheck);
	}

}
