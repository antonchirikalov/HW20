/**
 * 
 */
package com.toennies.ci1429.app.network.connector;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Special byte buffer that allows threaded access. Allows blocking and non-blocking access to data (if available).
 * Has a specific size that does not grow (waits until space is available again).
 * @author renkenh
 */
public class BlockingByteBuffer
{
	/** Default timeout in Milliseconds. */
	public static final int DEFAULT_TIMEOUT = 1000;


	private final ReentrantLock dataLock = new ReentrantLock();
	private final Condition isDataBufferFull = this.dataLock.newCondition();
	private final Condition isDataBufferEmpty = this.dataLock.newCondition();
	private final byte[] dataBuffer;
	private final int timeout;
	private volatile boolean interrupted = false;
	private int dataBufferSize = 0;

	
	/**
	 * Constructor with default timeout.
	 */
	public BlockingByteBuffer(int size)
	{
		this(size, DEFAULT_TIMEOUT);
	}

	/**
	 * Constructor.
	 * @param size The max size of the buffer.
	 * @param timeout The timeout for blocking operations (in millisec).
	 */
	public BlockingByteBuffer(int size, int timeout)
	{
		this.dataBuffer = new byte[size];
		this.timeout = timeout;
	}

	
	/**
	 * Add new data to the buffer. If buffer is full, the method blocks until space is available again.
	 * @param arr The complete array will be added.
	 * @throws InterruptedException Is thrown if the process/thread is interrupted from external.
	 */
	public void pushData(byte[] arr) throws InterruptedException
	{
		this.pushData(arr, 0, arr.length);
	}
	
	/**
	 * Add new data to the buffer. If buffer is full, the method blocks until space is available again.
	 * @param arr The array will be added.
	 * @param offset The data beginning from the given offset is added.
	 * @param length This number of bytes is added to the buffer.
	 * @throws InterruptedException Is thrown if the process/thread is interrupted from external.
	 */
	public void pushData(byte[] arr, int offset, int length) throws InterruptedException
	{
		this.dataLock.lock();
		try
		{
			while (length > 0)
			{
				while (length > 0 && this.dataBufferSize >= this.dataBuffer.length)
				{
					if (this.interrupted)
						return;
					this.isDataBufferFull.await(this.timeout, TimeUnit.MILLISECONDS);
				}
	
				final int toCopy = Math.min(length, this.dataBuffer.length - this.dataBufferSize);
				System.arraycopy(arr, offset, this.dataBuffer, this.dataBufferSize, toCopy);
				length -= toCopy;
				offset += toCopy;
				this.dataBufferSize += toCopy;
				this.isDataBufferEmpty.signal();
			}
		}
		finally
		{
			this.interrupted = false;
			this.dataLock.unlock();
		}
	}
	
	/**
	 * Non-blocking operation. If no data is available, <code>null</code> is returned.
	 * @return Data if available, otherwise <code>null</code>.
	 */
	public byte[] pollData()
	{
		this.dataLock.lock();
		try
		{
			if (this.dataBufferSize == 0)
				return null;
			
			byte[] data = new byte[this.dataBufferSize];
			System.arraycopy(this.dataBuffer, 0, data, 0, this.dataBufferSize);
			this.dataBufferSize = 0;
			this.isDataBufferFull.signalAll();
			return data;
		}
		finally
		{
			this.dataLock.unlock();
		}
	}

	/**
	 * Blocking operation to retrieve data. Uses the initial specified timeout.
	 * Calls {@link #popData(int)}.
	 * @return Available data. Never <code>null</code>.
	 * @throws TimeoutException If the default amount of time elapses noticeable before data becomes available.
	 */
	public byte[] popData() throws TimeoutException
	{
		return this.popData(this.timeout);
	}
	
	/**
	 * Blocking operation to retrieve data. Uses the initial specified timeout.
	 * @param timeout The amount of time to wait for data before throwing a {@link TimeoutException}.
	 * @return Available data. Never <code>null</code>.
	 * @throws TimeoutException If the default amount of time elapses noticeable before data becomes available.
	 */
	public byte[] popData(int timeout) throws TimeoutException
	{
		this.dataLock.lock();
		try
		{
			if (this.dataBufferSize == 0)
				this.isDataBufferEmpty.await(timeout, TimeUnit.MILLISECONDS);
			
			if (this.interrupted || this.dataBufferSize == 0)
				throw new TimeoutException("No data in time.");
			
			byte[] data = new byte[this.dataBufferSize];
			System.arraycopy(this.dataBuffer, 0, data, 0, this.dataBufferSize);
			this.dataBufferSize = 0;
			this.isDataBufferFull.signalAll();
			return data;
		}
		catch (InterruptedException e)
		{
			throw new TimeoutException("Interrupted.");
		}
		finally
		{
			this.dataLock.unlock();
		}
	}

	/**
	 * Clears the buffer. After this operation, the buffer does no longer contain any data.
	 */
	public void clear()
	{
		this.dataLock.lock();
		try
		{
			this.interrupt();
			this.dataBufferSize = 0;
			this.isDataBufferFull.signal();
		}
		finally
		{
			this.dataLock.unlock();
		}
	}

	/**
	 * Interrupts all processes in blocking operation. Causing them to throw an {@link InterruptedException} and to return.
	 */
	public void interrupt()
	{
		this.dataLock.lock();
		try
		{
			this.interrupted = true;
			this.isDataBufferFull.signalAll();
			this.isDataBufferEmpty.signalAll();
		}
		finally
		{
			this.dataLock.unlock();
		}
	}

}
