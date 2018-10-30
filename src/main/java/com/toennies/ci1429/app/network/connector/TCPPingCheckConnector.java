/**
 * 
 */
package com.toennies.ci1429.app.network.connector;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.network.event.EventNotifierStub;
import com.toennies.ci1429.app.network.event.IEventHandler;
import com.toennies.ci1429.app.network.event.IEventNotifier;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.socket.TCPSocket;

/**
 * @author renkenh
 *
 */
public class TCPPingCheckConnector<T> extends AWrappedExecConnector<T> implements IEventNotifier
{

	/** The port on which to connect. */
	public static final String PARAM_PING = "Check Ping";

	private static final int TIMEOUT_HEALTHCHECK = 3000;

	private boolean checkPing = false;
	private volatile boolean isReachable = false;
	private InetAddress address;

	private class HealthCheck implements Runnable
	{
		@Override
		public void run()
		{
			// needed to have a (more or less) atomic operation
			try
			{
				TCPPingCheckConnector.this.isReachable = TCPPingCheckConnector.this.address
						.isReachable(TIMEOUT_HEALTHCHECK);
			}
			catch (IOException e)
			{
				logger.error("Could not reach address: {}.", e.getMessage());
				TCPPingCheckConnector.this.isReachable = false;
				TCPPingCheckConnector.this.notifier.publishEvent(IDevice.EVENT_ERROR_OCCURRED,
						"Could not connect to device. Retrying.");
			}
			TCPPingCheckConnector.this.notifier.publishEvent(IDevice.EVENT_STATE_CHANGED);
		}

	}

	private final EventNotifierStub notifier = new EventNotifierStub(this);

	private volatile IConfigContainer config;

	/**
	 * @param connector
	 */
	public TCPPingCheckConnector(IConnector<T> connector)
	{
		super(connector);
	}

	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		this.checkPing = config.getBooleanEntry(PARAM_PING);
		try
		{
			this.address = InetAddress.getByName(config.getEntry(TCPSocket.PARAM_HOST));
		}
		catch (UnknownHostException ex)
		{
			logger.info("Given host {} is unknown to network subsystem.", config.getEntry(TCPSocket.PARAM_HOST), ex);
			this.address = null;
		}
		super.connect(config);
		this.config = config;
	}

	@Override
	protected void schedule(ScheduledExecutorService service)
	{
		service.scheduleWithFixedDelay(new HealthCheck(), 1, 5, TimeUnit.SECONDS);
	}

	@Override
	public boolean isConnected()
	{
		if (!this.checkPing || this.address == null)
			return super.isConnected();

		return this.config != null && this.isReachable;
	}

	@Override
	protected void shutdownExecute()
	{
		this.config = null;
		super.shutdownExecute();
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
