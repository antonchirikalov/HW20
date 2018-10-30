/**
 * 
 */
package com.toennies.ci1429.app.network.connector;

import com.toennies.ci1429.app.network.message.IMessageTransformer;

/**
 * @author renkenh
 *
 */
public class DataTransformer extends ADataTransformer
{

	private final IMessageTransformer transformer;
	

	/**
	 * @param connector
	 */
	public DataTransformer(IMessageTransformer transformer, IConnector<byte[]> connector)
	{
		super(connector);
		this.transformer = transformer;
	}

	@Override
	protected IMessageTransformer transformer()
	{
		return this.transformer;
	}

}
