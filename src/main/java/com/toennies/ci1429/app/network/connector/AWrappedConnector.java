/**
 * 
 */
package com.toennies.ci1429.app.network.connector;

/**
 * Simplified version of the {@link AWrapperTransformer} where both, input and output of network stack is
 * of the same type.
 * @author renkenh
 */
public abstract class AWrappedConnector<T> extends AWrapperTransformer<T, T> implements IConnector<T>
{
	
	/**
	 * Constructor.
	 */
	public AWrappedConnector(IConnector<T> connector)
	{
		super(connector);
	}


	@Override
	protected T transformToOut(T entity)
	{
		return entity;
	}

	@Override
	protected T transformToConIn(T entity)
	{
		return entity;
	}

}
