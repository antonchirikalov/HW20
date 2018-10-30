/**
 * 
 */
package com.toennies.ci1429.app.network.connector;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.network.parameter.IConfigContainer;

/**
 * Implementation of the {@link IFlexibleConnector} interface. Allows the stacking of other connectors.
 * @author renkenh
 */
public abstract class AFlexibleWrapperTransformer<IN, OUT, CON_IN, CON_OUT> implements IFlexibleConnector<IN, OUT>, IConnectorWrapper<IFlexibleConnector<CON_IN, CON_OUT>, CON_IN, CON_OUT>
{
	
	/** Logger instance. */
	protected static final Logger logger = LogManager.getLogger();

	/** wrapped connector. */
	protected final IFlexibleConnector<CON_IN, CON_OUT> connector;


	/**
	 * Constructor.
	 */
	public AFlexibleWrapperTransformer(IFlexibleConnector<CON_IN, CON_OUT> connector)
	{
		this.connector = connector;
	}

	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		this.connector.connect(config);
	}
	
	@Override
	public IFlexibleConnector<CON_IN, CON_OUT> getWrappedConnector()
	{
		return this.connector;
	}
	
	@Override
	public boolean isConnected()
	{
		return this.connector.isConnected();
	}

	@Override
	public OUT poll() throws IOException
	{
		return this.transformToOut(this.connector.poll());
	}

	@Override
	public OUT pop() throws IOException, TimeoutException
	{
		return this.transformToOut(this.connector.pop());
	}

	@Override
	public void push(IN entity) throws IOException
	{
		this.connector.push(this.transformToConIn(entity));
	}
	
	/**
	 * Used to transform between the wrapped connector and the output of this connector.
	 * This method is called by {@link #pop()} and {@link #poll()}. 
	 * @param entity The entity to transform.
	 * @return The result of the transformation. May not be <code>null</code>
	 */
	protected abstract OUT transformToOut(CON_OUT entity) throws IOException;
	
	/**
	 * Used to transform between the wrapped connector and the input of this connector.
	 * This method is called by {@link #push()}. 
	 * @param entity The entity to transform.
	 * @return The result of the transformation. May not be <code>null</code>
	 */
	protected abstract CON_IN transformToConIn(IN entity) throws IOException;

	@Override
	public void disconnect() throws IOException
	{
		this.connector.disconnect();
	}

	@Override
	public void shutdown()
	{
		this.connector.shutdown();
	}

}
