/**
 * 
 */
package com.toennies.ci1429.app.model.watcher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.toennies.ci1429.app.model.watcher.IWatchEvent.EventType;
import com.toennies.ci1429.app.network.protocol.watcher.WatchSystemEvent;

/**
 * Implementation of the {@link ISystem} interface for subsystems of a {@link Watcher} instance.
 * @author renkenh
 */
public class SubSystem implements ISystem
{

	//message strings
	private static final String SYSTEM_ACTIVATED = "System activated";
	private static final String SYSTEM_SHUTDOWN  = "System shutdown";
	private static final String FAULT_RESOLVED   = "Fault {} resolved";
	
	
	private final String name;
	private final Watcher parent;
	private final ReentrantLock lock = new ReentrantLock();
	private final Map<String, Fault> faultsByName = new HashMap<>();
	private SystemState currentState = SystemState.SHUTDOWN;
	private volatile Instant lastChange;
	private volatile String lastMessage;


	/**
	 * Constructor for a global system, i.e. the root of a system hierarchy.
	 * @param parent The parent.
	 */
	public SubSystem(Watcher parent)
	{
		this(null, parent);
	}

	/**
	 * Constructor for a real subsystem. The id is a concatenation of the parent id and the name of this system.
	 * @param name The name of the subsystem
	 * @param parent The parent.
	 */
	public SubSystem(String name, Watcher parent)
	{
		this.name = name;
		this.parent = parent;
	}


	@Override
	public String getId()
	{
		if (this.name == null)
			return this.parent.getName();
		return this.parent.getName() + "." + this.name;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public SystemState getSystemState()
	{
		this.lock.lock();
		try
		{
			if (this.faultsByName.size() > 0)
				return SystemState.FAULTY;
			return this.currentState;
		}
		finally
		{
			this.lock.unlock();
		}
	}

	@Override
	public List<Fault> getCurrentFaults()
	{
		this.lock.lock();
		try
		{
			ArrayList<Fault> faults = new ArrayList<>(this.faultsByName.values());
			Collections.sort(faults, Fault.CHRONOLOGICAL_DESC);
			return faults;
		}
		finally
		{
			this.lock.unlock();
		}
	}
	
	@Override
	public ISystem createSnapshot()
	{
		this.lock.lock();
		try
		{
			return new SystemSnapshot(this);
		}
		finally
		{
			this.lock.unlock();
		}
	}

	/**
	 * (Re-)Actives this subsystem. If this action changes the state of the subsystem, an event is added to the
	 * event log of the parent watcher instance.
	 */
	public void activate()
	{
		ISystemEvent event = null;
		this.lock.lock();
		try
		{
			if (this._activate())
				event = new WatchSystemEvent(this.name, EventType.UP, this.lastChange, this.lastMessage);
		}
		finally
		{
			this.lock.unlock();
		}
		if (event != null)
			this.parent.addToEventLog(event);
	}
	
	private boolean _activate()
	{
		this.lock.lock();
		try
		{
			if (this.currentState != SystemState.SHUTDOWN)
				return false;

			this.currentState = SystemState.ACTIVE;
			this.lastChange = Instant.now();
			this.lastMessage = SYSTEM_ACTIVATED;
			return true;
		}
		finally
		{
			this.lock.unlock();
		}
	}

	/**
	 * Shuts down this subsystem. If this action changes the state of the subsystem, an event is added to the
	 * event log of the parent watcher instance.
	 */
	public void shutdown()
	{
		ISystemEvent event = null;
		this.lock.lock();
		try
		{
			if (this._shutdown())
				event = new WatchSystemEvent(this.name, EventType.DOWN, this.lastChange, this.lastMessage);
		}
		finally
		{
			this.lock.unlock();
		}
		if (event != null)
			this.parent.addToEventLog(event);
	}
	
	private boolean _shutdown()
	{
		this.lock.lock();
		try
		{
			if (this.currentState == SystemState.SHUTDOWN)
				return false;
			
			this.currentState = SystemState.SHUTDOWN;
			this.faultsByName.clear();
			this.lastChange = Instant.now();
			this.lastMessage = SYSTEM_SHUTDOWN;
			return true;
		}
		finally
		{
			this.lock.unlock();
		}
	}
	
	/**
	 * Method to handle incoming watch events for this system. if the event does change the state of this system,
	 * the event is added to the event log of the watcher parent.
	 * @param event The event to process.
	 */
	void handleEvent(IWatchEvent event)
	{
		boolean doLog = false;
		if (event instanceof IFaultEvent)
		{
			IFaultEvent eFault = (IFaultEvent) event;
			switch (eFault.getType())
			{
				case DOWN:
					doLog = this.removeFault(eFault);
					break;
				case INFO:
					doLog = this.setInfo(eFault);
					break;
				case UP:
					doLog = this.addFault(eFault);
					break;
			}
		}
		else if (event instanceof ISystemEvent)
		{
			switch (event.getType())
			{
				case DOWN:
					doLog = this._shutdown();
					break;
				case INFO:
					doLog = this.setInfo(event);
					break;
				case UP:
					doLog = this._activate();
					break;
			}
		}
		if (doLog)
			this.parent.addToEventLog(event);
	}

	private boolean addFault(IFaultEvent event)
	{
		this.lock.lock();
		try
		{
			if (this.faultsByName.containsKey(event.getId()))
				return false;
			
			Fault fault = new Fault(event.getId(), event.getSeverity(), event.getMessage(), event.getTimestamp());
			this.faultsByName.put(fault.getId(), fault);
			this.lastChange = fault.getTimestamp();
			this.lastMessage = fault.getMessage();
			return true;
		}
		finally
		{
			this.lock.unlock();
		}
	}

	private boolean setInfo(IWatchEvent event)
	{
		this.lock.lock();
		try
		{
			this.lastChange = event.getTimestamp();
			this.lastMessage = event.getMessage();
		}
		finally
		{
			this.lock.unlock();
		}
		return true;
	}

	private boolean removeFault(IFaultEvent event)
	{
		this.lock.lock();
		try
		{
			if (!this.faultsByName.containsKey(event.getId()))
				return false;
			
			this.faultsByName.remove(event.getId());
			this.lastChange = event.getTimestamp();
			this.lastMessage = FAULT_RESOLVED.replace("{}", event.getId());
			return true;
		}
		finally
		{
			this.lock.unlock();
		}
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
