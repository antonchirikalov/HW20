/**
 * 
 */
package com.toennies.ci1429.app.model.watcher;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.model.ADevice;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.IDeviceDescription;
import com.toennies.ci1429.app.model.watcher.IWatchEvent.EventType;
import com.toennies.ci1429.app.network.protocol.watcher.AWatcherProtocol;
import com.toennies.ci1429.app.repository.IWatchEventRepository;
import com.toennies.ci1429.app.repository.WatchEventEntity;
import com.toennies.ci1429.app.util.WatcherUtil;

/**
 * "Watchdog" implementation.
 * Receives events from the protocol and processes them where needed. Provides the event log.
 * Saves events to database where needed.
 * @author renkenh
 */
public class Watcher extends ADevice<AWatcherProtocol<?>> implements ISystem
{
	
	private static final Logger logger = LogManager.getLogger();

	private static final Duration DAYS_TO_HOLD = Duration.of(30, ChronoUnit.DAYS);
	private static final Duration HOURS_TO_CACHE = Duration.of(1, ChronoUnit.HOURS);
	
	/** Constant to indicate that all (known) events are wanted from the event log. */
	public static final int ALL_EVENTS = -1;

	/** Command that can be used by a client to activate a specified system. */
	public static final String CMD_ACTIVATE_SYSTEM = "activate";
	/** Command that can be used by a client to deactivate a specified system. */
	public static final String CMD_DEACTIVATE_SYSTEM = "deactivate";


	private final ReentrantLock lock = new ReentrantLock();
	//only hold the newest x events in watcher - everything else, save in db (write through cache)
	private final LinkedList<IWatchEvent> eventlog = new LinkedList<>();
	private String name;
	private Map<String, SubSystem> systemsById;
	private SubSystem global;

	private IWatchEventRepository repo;

	
	/**
	 * Constructor.
	 * @param description The description of this device.
	 * @param repo The repository to use for event log storage.
	 */
	public Watcher(IDeviceDescription description)
	{
		this.repo = RepoService.getRepo();
		System.out.println(this.repo);
		this.updateDevice(description);
	}
	
	
	@Override
	public void updateDevice(IDeviceDescription description)
	{
		super.updateDevice(description);
		
		this.lock.lock();
		try
		{
			this.name = this.getConfiguration().get(AWatcherProtocol.PARAM_NAME);
			this.global = new SubSystem(this);
			this.systemsById = new HashMap<>();
			this.systemsById.put(this.global.getId(), this.global);
			
			this.eventlog.clear();
			if (repo != null)
				this.eventlog.addAll(repo.findByDeviceIDAndTimestampGreaterThanEqual(this.getDeviceID(), Instant.now().minus(HOURS_TO_CACHE)));
			String[] subnames = WatcherUtil.getSubsystems(this.getProtocolClass());
			for (String subname : subnames)
				this.systemsById.put(subname, new SubSystem(subname, this));
		}
		finally
		{
			this.lock.unlock();
		}
	}
	
	
	@Override
	public @NotNull DeviceResponse process(Object... params)
	{
		if (params.length < 2)
			return new DeviceResponse(Status.BAD_REQUEST, "Parameters not as expected.");
		String cmd = String.valueOf(params[0]);
		String system = String.valueOf(params[1]);

		switch (cmd)
		{
			case CMD_ACTIVATE_SYSTEM:
				return deActivateSystem(system, false);
			case CMD_DEACTIVATE_SYSTEM:
				return deActivateSystem(system, true);
			default:
				return new DeviceResponse(Status.BAD_SERVER, "Not implemented.");
		}
	}
	
	private DeviceResponse deActivateSystem(String systemId, boolean deactivate)
	{
		SubSystem ss = null;
		this.lock.lock();
		try
		{
			ss = this.systemsById.get(systemId);
		}
		finally
		{
			this.lock.unlock();
		}
		
		if (ss == null)
			return new DeviceResponse(Status.BAD_NOT_FOUND, "System " + systemId + " not found.");
		
		if (!deactivate)
			ss.activate();
		else
			ss.shutdown();

		return DeviceResponse.OK;
	}
	
	@Override
	public String getId()
	{
		return this.name;
	}

	@Override
	public String getName()
	{
		return this.getId();
	}
	
	@Override
	public DeviceState getDeviceState()
	{
		DeviceState state = super.getDeviceState();
		if (state != DeviceState.NOT_INITIALIZED && !this.getCurrentFaults().isEmpty())
			return DeviceState.FAULTY;
		return state;
	}

	@Override
	public SystemState getSystemState()
	{
		DeviceState dState = this.getDeviceState();
		switch (dState)
		{
			case CONNECTED:
			case INITIALIZED:
				return SystemState.ACTIVE;
			case FAULTY:
				return SystemState.FAULTY;
			case NOT_INITIALIZED:
				return SystemState.SHUTDOWN;
		}
		return null;
	}

	@Override
	public List<Fault> getCurrentFaults()
	{
		this.lock.lock();
		try
		{
			return this.global.getCurrentFaults();
		}
		finally
		{
			this.lock.unlock();
		}
	}
	
	@Override
	public Instant lastChange()
	{
		return this.global.lastChange();
	}

	@Override
	public String lastMessage()
	{
		return this.global.lastMessage();
	}
	
	@Override
	public ISystem createSnapshot()
	{
		this.lock.lock();
		try
		{
			return this.global.createSnapshot();
		}
		finally
		{
			this.lock.unlock();
		}
	}

	/**
	 * @return Returns all faults within the current system hierarchy. The faults are sorted chronologically.
	 */
	public List<Fault> getAllCurrentFaults()
	{
		this.lock.lock();
		try
		{
			ArrayList<Fault> faults = new ArrayList<>();
			for (ISystem subsystem : this.systemsById.values())
				subsystem.getCurrentFaults().stream().map((f) -> new Fault(f, subsystem)).forEach(faults::add);

			Collections.sort(faults, Fault.CHRONOLOGICAL_DESC);
			return faults;
		}
		finally
		{
			this.lock.unlock();
		}
	}

	/**
	 * @return A list of snapshots of all known {@link ISystem}s - including the global system (this watcher).
	 */
	public Collection<ISystem> getSystemOverview()
	{
		this.lock.lock();
		try
		{
			List<ISystem> systems = new ArrayList<>(this.systemsById.size());
			this.systemsById.values().stream().map((s) -> s.createSnapshot()).forEach(systems::add);
			Collections.sort(systems, (s1, s2) -> s1.getId().compareTo(s2.getId()));
			return systems;
		}
		finally
		{
			this.lock.unlock();
		}
	}

	/**
	 * @return The whole event log.
	 */
	public List<IWatchEvent> getEventLog()
	{
		return this.getEventLog(ALL_EVENTS, null);
	}

	/**
	 * A part of the event log. The <code>count</code> number of the latest events in the event log.
	 * @param count The number of events wanted.
	 * @return An array with events sorted chronologically.
	 */
	public List<IWatchEvent> getEventLog(int count, Instant time)
	{
		this.lock.lock();
		try
		{
			List<? extends IWatchEvent> events;
			if (repo != null && (count < 1 || count > this.eventlog.size()))
			{
				if (time == null)
					events = this.repo.findByDeviceID(this.getDeviceID());
				else
					events = this.repo.findByDeviceIDAndTimestampGreaterThanEqual(this.getDeviceID(), time);
			}
			else
				events = this.eventlog;

			if (count > 0)
				events = events.subList(0, Math.min(count, events.size()));
			
			Collections.sort(events, IWatchEvent.CHRONOLOGICAL_DESC);
			
			if (time != null)
			{
				int i = 0;
				Iterator<? extends IWatchEvent> ie = events.iterator();
				while (ie.hasNext() && ie.next().getTimestamp().isAfter(time))
				{
					i++;
				}
				events = events.subList(0, i);
			}
			
			return Collections.unmodifiableList(events);
		}
		finally
		{
			this.lock.unlock();
		}
	}

	@Override
	public void handleEvent(String eventID, Object source, Object... params)
	{
		if (!AWatcherProtocol.EVENT_WATCH_EVENT.equals(eventID))
		{
			super.handleEvent(eventID, source, params);
			return;
		}

		IWatchEvent event = (IWatchEvent) params[0];
		SubSystem ss = this.systemsById.get(event.getSystemId());
		if (ss == null)
			return;
		ss.handleEvent(event);
	}

	/**
	 * Used by the {@link SubSystem} implementations to add events to the event log.
	 * @param event The event to add.
	 */
	void addToEventLog(IWatchEvent event)
	{
		logger.info("[{}][{},{},{}]", event.getSystemId(), event.getTimestamp(), event.getType(), event.getMessage());
		this.lock.lock();
		try
		{
			this.updateEventLog(event);
			this.updateDBLog(event);
			this.publishEvent(event);
		}
		finally
		{
			this.lock.unlock();
		}
		
	}

	private void updateEventLog(IWatchEvent event)
	{
		this.eventlog.addFirst(event);
		Collections.sort(this.eventlog, IWatchEvent.CHRONOLOGICAL_DESC);
		IWatchEvent oldest = this.eventlog.pollLast();
		Instant least = Instant.now().minus(HOURS_TO_CACHE);
		while (oldest != null && oldest.getTimestamp().isBefore(least))
		{
			this.eventlog.removeLast();
			oldest = this.eventlog.pollLast();
		}
	}


	private void updateDBLog(IWatchEvent event)
	{
		if (this.repo == null)
			return;
		
		this.repo.save(WatchEventEntity.createFrom(this.getDeviceID(), event));
		this.repo.deleteByDeviceIDAndTimestampLessThan(this.getDeviceID(), Instant.now().minus(DAYS_TO_HOLD));
	}


	private void publishEvent(IWatchEvent event)
	{
		if (event.getType() != EventType.INFO)
			this.publishEvent(IDevice.EVENT_STATE_CHANGED, this.getDeviceState());
	}

}
