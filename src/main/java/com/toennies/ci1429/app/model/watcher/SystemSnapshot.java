/**
 * 
 */
package com.toennies.ci1429.app.model.watcher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The immutable implementation of the {@link ISystem} interface. Used in the
 * {@link #createSnapshot()} method.
 * @author renkenh
 */
public class SystemSnapshot implements ISystem
{

	private final String id;
	private final String name;
	private final SystemState state;
	private final List<Fault> currentFaults = new ArrayList<>();
	private final Instant lastChange;
	private final String lastMessage;
	

	/**
	 * Copy Constructor.
	 */
	SystemSnapshot(ISystem system)
	{
		this.id   = system.getId();
		this.name = system.getName();
		this.state = system.getSystemState();
		this.currentFaults.addAll(system.getCurrentFaults());
		this.lastChange = system.lastChange();
		this.lastMessage = system.lastMessage();
	}


	@Override
	public String getId()
	{
		return this.id;
	}
	
	@Override
	public String getName()
	{
		return this.name;
	}
	
	@Override
	public SystemState getSystemState()
	{
		return this.state;
	}

	@Override
	public List<Fault> getCurrentFaults()
	{
		return Collections.unmodifiableList(this.currentFaults);
	}

	@Override
	public ISystem createSnapshot()
	{
		return this;
	}

	@Override
	public Instant lastChange()
	{
		return this.lastChange;
	}

	@Override
	public String lastMessage()
	{
		return this.lastMessage;
	}

}
