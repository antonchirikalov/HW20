/**
 * 
 */
package com.toennies.ci1429.app.model.watcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.toennies.ci1429.app.repository.IWatchEventRepository;

/**
 * Service-Pattern for the watcher event repository. This is a hub for watcher devices, which they needed
 * to access the repository.
 * @author renkenh
 */
@Configuration
class RepoService
{

	private static IWatchEventRepository WATCHEVENT_REPOSITORY;

	/**
	 * Used by Spring to auto inject the repository. 
	 * @param repo The repository.
	 */
	@Autowired
	public void setWatchEventRepository(IWatchEventRepository repo)
	{
		WATCHEVENT_REPOSITORY = repo;
	}


	/**
	 * Method to access the event repository from non-beans.
	 * @return The repository. May be <code>null</code> if spring did not initialize this bean correctly.
	 */
	public static final IWatchEventRepository getRepo()
	{
		return WATCHEVENT_REPOSITORY;
	}

}
