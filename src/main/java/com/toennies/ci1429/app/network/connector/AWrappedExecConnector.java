package com.toennies.ci1429.app.network.connector;

/**
 * Convenient class of the {@link AFlexibleWrappedExecTransformer} where input and output are of the same type.
 * @author renkenh
 *
 * @param <T> The type of data going through this connector.
 */
public abstract class AWrappedExecConnector<T> extends AFlexibleWrappedExecTransformer<T, T, T, T> implements IConnector<T>
{

	public AWrappedExecConnector(IConnector<T> connector)
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
