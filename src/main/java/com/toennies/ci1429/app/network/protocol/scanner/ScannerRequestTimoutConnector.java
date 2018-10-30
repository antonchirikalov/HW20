/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scanner;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.network.connector.AFlexibleWrappedExecTransformer;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.util.Compute;

/**
 * Connector for device class {@link DeviceType#SCANNER}. Does special handling
 * regarding multiple requests (which are hold until a result is returned).
 * 
 * @author renkenh
 */
public class ScannerRequestTimoutConnector extends AFlexibleWrappedExecTransformer<Void, IExtendedMessage, IMessage, IExtendedMessage>
{

	private class Repop implements Runnable
	{
		@Override
		public void run()
		{
			if (!ScannerRequestTimoutConnector.this.isConnected())
			{
				ScannerRequestTimoutConnector.this.executor.schedule(this, 5, TimeUnit.SECONDS);
				return;
			}
			try
			{
				
				IExtendedMessage rawScan = ScannerRequestTimoutConnector.super.pop();
				ScannerRequestTimoutConnector.this.handleScanResult(rawScan);
			}
			catch (TimeoutException ex)
			{
				// ignore this. timeout happens if no one scans for a certain
				// amount of time.
				// instead use high level request-timeout in ScannerConnector to
				// timeout request
			}
			catch (IOException ex)
			{
				logger.error("Could not connect to device.", ex);
				ScannerRequestTimoutConnector.this.handleScanResult(ex);
			}
			if (!ScannerRequestTimoutConnector.this.executor.isTerminated())
				ScannerRequestTimoutConnector.this.executor.execute(new Repop());
		}
	}

	private final ReentrantLock computeLock = new ReentrantLock();
	private Compute compute = new Compute();
	private ScheduledExecutorService executor;
	private int timeout;

	/**
	 * Constructor.
	 */
	public ScannerRequestTimoutConnector(IFlexibleConnector<IMessage, IExtendedMessage> connector)
	{
		super(connector);
	}

	@Override
	protected void schedule(ScheduledExecutorService service)
	{
		this.executor = service;
		this.executor.execute(new Repop());
	}

	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		super.connect(new NoSEPConfigContainer(config));
		this.timeout = config.getIntEntry(IProtocol.PARAM_REQUEST_TIMEOUT);
	}

	@Override
	public IExtendedMessage pop() throws IOException, TimeoutException
	{
		Compute reference = null;
		this.computeLock.lock();
		try
		{
			reference = this.compute;
		}
		finally
		{
			this.computeLock.unlock();
		}
		try
		{
			return reference.get(this.timeout);
		}
		catch (TimeoutException | IOException e)
		{
			throw e;
		}
		catch (InterruptedException e)
		{
			return null;
		}
		catch (Exception e)
		{
			throw new IOException(e);
		}
	}

	@Override
	public IExtendedMessage poll() throws IOException
	{
		Compute reference = null;
		this.computeLock.lock();
		try
		{
			reference = this.compute;
		}
		finally
		{
			this.computeLock.unlock();
		}
		try
		{
			return reference.poll();
		}
		catch (IOException e)
		{
			throw e;
		}
		catch (InterruptedException e)
		{
			return null;
		}
		catch (Exception e)
		{
			throw new IOException(e);
		}
	}

	@Override
	protected IExtendedMessage transformToOut(IExtendedMessage entity) throws IOException
	{
		return entity;
	}

	@Override
	protected IMessage transformToConIn(Void entity) throws IOException
	{
		return null;
	}

	private final void handleScanResult(Object scanData)
	{
		this.computeLock.lock();
		try
		{
			this.compute.put(scanData);
			this.compute = new Compute();
		}
		finally
		{
			this.computeLock.unlock();
		}
	}

	@Override
	public void disconnect() throws IOException
	{
		this.computeLock.lock();
		try
		{
			this.compute.cancel();
			this.compute = null;
		}
		finally
		{
			this.computeLock.unlock();
		}
		super.disconnect();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void shutdown()
	{
		this.computeLock.lock();
		try
		{
			this.compute.cancel();
			this.compute = null;
		}
		finally
		{
			this.computeLock.unlock();
		}
		super.shutdown();
	}

}
