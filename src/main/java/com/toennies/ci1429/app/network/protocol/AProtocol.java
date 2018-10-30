/**
 * 
 */
package com.toennies.ci1429.app.network.protocol;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.event.IEventHandler;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.parameter.EmptyConfigContainer;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.socket.ISocket;
import com.toennies.ci1429.app.util.Utils;


/**
 * Abstract implementation of the {@link IProtocol} interface.
 * Contains three threads:
 * 
 * Execution Service
 * that queues and executes all call to the connector. This has been added to be able to
 * detect timeouts on calls to the connector. The calling thread watches the timeout and eventually resets the protocol
 * while the executing service is blocked by the original call.
 * 
 * Reconnect Runner
 * A thread that occasionally checks whether the connection to the device is still available. If not, the thread tries
 * to reconnect to the device. This reconnect runner is also used to initialize the device on the first try. To perform
 * a handshake with the remote device implement the method {@link AProtocol#performHandshake()}.
 * 
 * Healthcheck Runner
 * A thread that occasionally (can be parameterized by {@link AProtocol#HEALTHCHECK}) triggers a protocol-based health
 * check. Usually, that means the protocol will send a request to the device and check if it gets an appropriate response
 * back. The method to override to implement the health check is {@link AProtocol#performHealthcheck()}.
 * If the health check fails it is up to the implementation to perform steps to shutdown the current connection (e.g. by
 * calling {@link AProtocol#shutdown()}.
 * 
 * @author renkenh
 */
@Parameter(name=IProtocol.PARAM_SOCKET, isRequired=true, value="", typeInformation="java:com.toennies.ci1429.app.network.parameter.typeinformation.SocketTypeValidator", toolTip="Specify which Socket implementation should be used for connecting to the hardware.")
@Parameter(name=IProtocol.PARAM_REQUEST_TIMEOUT, isRequired=true, value="1500", typeInformation="int:0..", toolTip="Request timeout in milliseconds. The time interval until which the request must have been processed by the server.")
@Parameter(name=IFlexibleConnector.PARAM_TIMEOUT, isRequired=true, value="1000", typeInformation="int:0..", toolTip="Protocol timeout in milliseconds. The time interval until a protocol response must be returned by the hardware device.")
public abstract class AProtocol<CONNECTOR_TYPE extends IFlexibleConnector<IN, OUT>, IN, OUT> implements IProtocol
{

	protected static final Logger logger = LogManager.getLogger();


	/** This lock protects all references to connector, device, logging-service */
	private final PipelineNotifierStub notifier = new PipelineNotifierStub(this);
	private PipelineConfigContainer config;
	protected final ReentrantLock lock = new ReentrantLock();
	private volatile CONNECTOR_TYPE pipeline;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(Map<String, String> parameters) throws IOException
	{
		this.lock.lock();
		try
		{
			if (this.isConnected())
				this.shutdown();
			this.config = new PipelineConfigContainer(parameters, this.getClass());
			this.pipeline = this.createPipeline(Utils.instantiate(this.config.getEntry(PARAM_SOCKET)));
			this.notifier.setupPipelineNotifier(this.pipeline);
		}
		finally
		{
			this.lock.unlock();
		}
		this.pipeline.connect(this.config);
	}
	
	/**
	 * Abstract method that is used to initialize/create the network stack (pipeline).
	 * This must be implemented by the specific protocols.
	 * @param socket The socket. The end of the pipeline - as chosen by the user.
	 * @return A connector which marks the beginning of the pipeline.
	 */
	protected abstract CONNECTOR_TYPE createPipeline(ISocket socket);
	
	/**
	 * @return The pipeline for access in derived types.
	 */
	protected CONNECTOR_TYPE pipeline()
	{
		return this.pipeline;
	}

	@Override
	public Object send(Object... params) throws IOException, TimeoutException
	{
		if (!this.isConnected())
			throw new IOException("Could not send request. Pipe not connected.");

		try
		{
			return this._send(params);
		}
		catch (IOException | TimeoutException e)
		{
			this.pipeline().disconnect();
			throw e;
		}
	}

	/**
	 * Internal method which is used by the official {@link #send(Object...)} method. Has the same signature.
	 * This style was chosen to implement a disconnect if an error occurs.
	 * @see #send(Object...)
	 */
	protected abstract Object _send(Object... params) throws IOException, TimeoutException;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected()
	{
		CONNECTOR_TYPE connector = this.pipeline;
		return this.isInitialized() && connector != null && connector.isConnected();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInitialized()
	{
		this.lock.lock();
		try
		{
			return this.config != null;
		}
		finally
		{
			this.lock.unlock();
		}
	}
	
	@Override
	public Map<String, String> getConfig()
	{
		this.lock.lock();
		try
		{
			if (!this.isInitialized())
				return Collections.emptyMap();
			return this.config.getConfig();
		}
		finally
		{
			this.lock.unlock();
		}
	}
	
	protected IConfigContainer config()
	{
		this.lock.lock();
		try
		{
			if (!this.isInitialized())
				return EmptyConfigContainer.INSTANCE;
			return new PipelineConfigContainer(this.config);
		}
		finally
		{
			this.lock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void shutdown()
	{
		//do not use #getConnector() here - instead use tryLock
		boolean succeeded = this.lock.tryLock();
		try
		{
			CONNECTOR_TYPE connector = this.pipeline;
			if (connector != null)
			{
				this.notifier.setdownPipelineNotifier(connector);
				connector.shutdown();
			}
			this.config = null;
		}
		finally
		{
			this.pipeline = null;
			if (succeeded)
				this.lock.unlock();
		}
	}

	@Override
	public void registerEventHandler(IEventHandler handler)
	{
		this.notifier.registerEventHandler(handler);
	}

	@Override
	public void unregisterEventHandler(IEventHandler handler)
	{
		this.notifier.unregisterEventHandler(handler);
	}

}
