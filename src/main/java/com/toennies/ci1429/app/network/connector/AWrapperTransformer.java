/**
 * 
 */
package com.toennies.ci1429.app.network.connector;

/**
 * A transformer that transforms from an IN type to the specified OUT type.
 * @author renkenh
 */
public abstract class AWrapperTransformer<IN, OUT> extends AFlexibleWrapperTransformer<IN, IN, OUT, OUT> implements IConnector<IN>
{

	/**
	 * 
	 */
	public AWrapperTransformer(IConnector<OUT> connector)
	{
		super(connector);
	}

}
