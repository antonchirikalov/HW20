/**
 * 
 */
package com.toennies.ci1429.app.network.connector;

/**
 * @author renkenh
 *
 */
public class ExceptionConnector<TYPE> extends FlexibleExceptionConnector<TYPE, TYPE> implements IConnector<TYPE>
{

	/**
	 * 
	 */
	public ExceptionConnector(IConnector<TYPE> connector)
	{
		super(connector);
	}

}
