/**
 * 
 */
package com.toennies.ci1429.app.network.connector;

/**
 * Connector that occasionally tries to reconnect to the hardware (if disconnected).
 * Sub-implemention of {@link FlexibleReConnector} to implement the {@link IConnector} interface
 * for easy integration into the pipeline.
 * @author renkenh
 */
public class ReConnector<IN> extends FlexibleReConnector<IN, IN> implements IConnector<IN>
{
	
	/**
	 * Constructor.
	 */
	public ReConnector(IConnector<IN> connector)
	{
		super(connector);
	}

}
