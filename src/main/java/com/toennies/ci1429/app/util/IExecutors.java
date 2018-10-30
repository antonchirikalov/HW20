/**
 * 
 */
package com.toennies.ci1429.app.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * Simple util that holds constants and methods for {@link ExecutorService}s.
 * @author renkenh
 */
public interface IExecutors
{

	/** The threadgroup name of the network {@link ThreadGroup}. */
	public static final String NETWORK_THREADGROUP = "network";
	
	/**
	 * The factory used for all executors in the network package.
	 * Creates daemonized threads in a thread group with the name {@link #NETWORK_THREADGROUP}.
	 */
	public static final ThreadFactory NETWORK_FACTORY = new ThreadFactoryImpl(NETWORK_THREADGROUP, true);

}
