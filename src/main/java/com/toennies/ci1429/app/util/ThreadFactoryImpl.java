package com.toennies.ci1429.app.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadFactory implementation that can be used with {@link ExecutorService}s.
 * Other than the default factory used in {@link Executors} this factory can create deamonized threads.
 * @author renkenh
 */
public class ThreadFactoryImpl implements ThreadFactory
{

	private final AtomicInteger threadNumber = new AtomicInteger(0);
	
	private final ThreadFactory defaultFactory;
	private final ThreadGroup threadGroup;
	private final boolean daemon;


	/**
	 * Constructor. The factory will not create the threads in a thread group.
	 * @param daemon Specifies whether this factory should create daemonized threads or not.
	 */
	public ThreadFactoryImpl(boolean daemon)
	{
		this(null, daemon);
	}
	
	/**
	 * Constructor.
	 * @param groupName The name of the {@link ThreadGroup} in which the threads should be created. May be <code>null</code> for no group.
	 * @param daemon Specifies whether this factory should create daemonized threads or not.
	 */
	public ThreadFactoryImpl(String groupName, boolean daemon)
	{
		this.threadGroup = groupName != null ? new ThreadGroup(groupName) : null;
		this.defaultFactory = groupName == null ? Executors.defaultThreadFactory() : null;
		this.daemon = daemon;
	}


	@Override
	public Thread newThread(Runnable r)
	{
		if (this.threadGroup == null)
		{
			Thread t = this.defaultFactory.newThread(r);
			t.setDaemon(this.daemon);
		}
		Thread t = new Thread(this.threadGroup, r, this.threadGroup.getName() + this.threadNumber.getAndIncrement(), 0);
		t.setDaemon(this.daemon);
		return t;
	}

}
