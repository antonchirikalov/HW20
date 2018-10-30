/**
 * 
 */
package com.toennies.ci1429.app.util;



import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.toennies.ci1574.lib.helper.Generics;


/**
 * 
 * @author renkenh
 */
public class Compute
{
	
	public static final String TIMEOUT_MESSAGE = Compute.class.getName()+".TimeoutException";


	private final ReentrantLock lock = new ReentrantLock();
	private final Condition waitForResult = this.lock.newCondition();
	private boolean resulted = false;
	private boolean canceled = false;
	private Object result = null;
	
	private int isWaiting = 0;

	
	public void put(Object result)
	{
		this.lock.lock();
		try
		{
			this.result = result;
			this.resulted = true;
			this.waitForResult.signalAll();
		}
		finally
		{
			this.lock.unlock();
		}
	}
	
	public void error(Exception e)
	{
		this.put(e);
	}
	
	public void cancel()
	{
		this.lock.lock();
		try
		{
			this.canceled = true;
			this.waitForResult.signalAll();
		}
		finally
		{
			this.lock.unlock();
		}
	}

	public <O extends Object> O poll() throws Exception
	{
		return this.get(-1);
	}
	
	public <O extends Object> O get() throws Exception
	{
		return this.get(0);
	}
	
	public <O extends Object> O get(int waitMillis) throws Exception
	{
		boolean waiting = false;
		this.lock.lock();
		try
		{
			if (waitMillis < 0 && !this.resulted || this.canceled)
				return null;
				
			waiting = true;
			this.isWaiting++;
			while (!this.resulted && !this.canceled)
			{
				if (waitMillis > 0 && !this.waitForResult.await(waitMillis, TimeUnit.MILLISECONDS))
					throw new TimeoutException(TIMEOUT_MESSAGE);
				else if (waitMillis == 0)
					this.waitForResult.await();
			}
			if (this.result instanceof Exception)
				throw (Exception) this.result;
			if (this.canceled)
				return null;
			return Generics.convertUnchecked(this.result);
		}
		finally
		{
			if (waiting)
				this.isWaiting--;
			this.lock.unlock();
		}
	}

	public boolean hasWaiters()
	{
		this.lock.lock();
		try
		{
			return !this.canceled && this.isWaiting > 0;
		}
		finally
		{
			this.lock.unlock();
		}
	}
}
