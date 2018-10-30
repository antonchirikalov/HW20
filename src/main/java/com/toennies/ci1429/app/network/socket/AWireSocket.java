/**
 * 
 */
package com.toennies.ci1429.app.network.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import com.toennies.ci1429.app.network.connector.BlockingByteBuffer;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.protocol.IProtocol;


/**
 * Abstract implementation to connect to a socket that represents a wired connection, like RS232 or TCP.
 * This type represents a "technical" TCP connection. No assumption about the protocol is made.
 * Use a protocol implementation {@link IProtocol} as a higher level of logic how to communication with a device.
 * @author renkenh
 */
@Parameter(name=IFlexibleConnector.PARAM_TIMEOUT, value="1000", isRequired=true, typeInformation="int:0..", toolTip="Network timeout in milliseconds.")
@Parameter
public abstract class AWireSocket implements ISocket
{
	
	private static final int MAX_BUFFER_SIZE = 4096;

//	private static final Logger logger = LogManager.getLogger();
	

	private final class Receiver extends Thread
	{
		
		{
			this.setDaemon(true);
		}
		
		public final AtomicBoolean isRunning = new AtomicBoolean(true);

		private final byte[] readBuffer = new byte[512];
		private final InputStream stream;
		
		public Receiver(InputStream instream)
		{
			this.stream = instream;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run()
		{
			while (this.isRunning.get() && !this.isInterrupted())
			{
				try
				{
					int read = this.stream.read(this.readBuffer);
					if (read > 0)
					{
						byte[] print = new byte[read];
						System.arraycopy(this.readBuffer, 0, print, 0, read);
						AWireSocket.this.dataBuffer.pushData(this.readBuffer, 0, read);
					}
					else if (read == -1)
						this.isRunning.set(false);
				}
				catch (Exception ex)
				{
					//do nothing. This happens because SO_TIMEOUT is set.
				}
			}
		}
		
	}
	
	
	private final ReentrantLock lock = new ReentrantLock();
	private BlockingByteBuffer dataBuffer;
	private Receiver receiver;
	private int timeout;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		this.lock.lock();
		try
		{
			if (this.isConnected())
				this.disconnect();

			this.timeout = config.getIntEntry(PARAM_TIMEOUT);
			this.dataBuffer = new BlockingByteBuffer(MAX_BUFFER_SIZE, this.timeout);
			this._setupWireSocket(config);
			this.receiver = new Receiver(this.getInputStream());
			this.receiver.start();
		}
		finally
		{
			this.lock.unlock();
		}
	}

	/** Return an inputstream to read from. */
	protected abstract InputStream getInputStream() throws IOException;

	/** Return an outputstream to write to. */
	protected abstract OutputStream getOutputStream() throws IOException;

	/** Method to setup the underlying wire specific connection. */
	protected abstract void _setupWireSocket(IConfigContainer config) throws IOException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected()
	{
		this.lock.lock();
		try
		{
			return this._isConnected();
		}
		finally
		{
			this.lock.unlock();
		}
	}
	
	/**
	 * @return Whether the wire specific connection thinks it is connected or not.
	 */
	protected abstract boolean _isConnected();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void push(byte[] data) throws IOException
	{
		if (!this.isConnected())
			throw new IOException("Not connected.");
		
		this.lock.lock();
		try
		{
			
			OutputStream out = this.getOutputStream();
			out.write(data);
			out.flush();
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
	public byte[] poll() throws IOException
	{
		if (!this.isConnected())
			throw new IOException("Not connected.");

		return this.dataBuffer.pollData();		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] pop() throws IOException, TimeoutException
	{
		if (!this.isConnected())
			throw new IOException("Not connected.");
		
		return this.dataBuffer.popData();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void disconnect()
	{
		this.lock.lock();
		try
		{
			if (this.receiver != null)
			{
				this.receiver.isRunning.set(false);
				this.receiver.interrupt();
			}
			
			this._setdownWireSocket();
			if (this.dataBuffer != null)
				this.dataBuffer.clear();
			this.receiver = null;
		}
		finally
		{
			this.lock.unlock();
		}
	}
	
	/**
	 * Method to setdown the wire specific connection.
	 */
	protected abstract void _setdownWireSocket();

	@Override
	public void shutdown()
	{
		final boolean success = this.lock.tryLock();
		try
		{
			final Receiver r = this.receiver;
			if (r != null)
			{
				r.isRunning.set(false);
				r.interrupt();
			}
			
			this._setdownWireSocket();
			this.receiver = null;
		}
		finally
		{
			if (success)
				this.lock.unlock();
		}
	}

}
