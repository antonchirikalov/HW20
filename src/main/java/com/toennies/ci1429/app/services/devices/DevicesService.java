package com.toennies.ci1429.app.services.devices;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.spring.events.EventBus;

import com.toennies.ci1429.app.model.ADevice;
import com.toennies.ci1429.app.model.DeviceException;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.IDeviceDescription;
import com.toennies.ci1429.app.network.event.IEventHandler;
import com.toennies.ci1429.app.repository.DeviceDescriptionEntity;
import com.toennies.ci1429.app.repository.IDevicesRepository;


@Component
public class DevicesService implements IDevicesService, IEventHandler
{
	
	private static final Logger logger = LogManager.getLogger();

	private static final Comparator<IDevice> DEVICE_SORTER = (d1, d2) -> d1.getDeviceID() - d2.getDeviceID();


	@Autowired
	private IDevicesRepository repo;
	
	@Autowired
	private EventBus.ApplicationEventBus eventBus;

	private boolean onShutdown = false;
	private final ReentrantLock devicesLock = new ReentrantLock();
	private final SortedMap<Integer, IDevice> devices = new TreeMap<>();

	
	@PostConstruct
	private void init()
	{
		this.onShutdown = false;
		final Iterable<DeviceDescriptionEntity> allDevices = this.repo.findAll();
		for(DeviceDescriptionEntity dde : allDevices)
		{
			IDevice createdDevice = this.createDeviceByDescription(dde);
			if (createdDevice == null)
			{
				// Exception can't be thrown, because it's in @PostConstruct
				logger.error("Device with id {} can't be created.", dde.getDeviceID());
				continue;
			}

			this.devicesLock.lock();
			try
			{
				this.devices.put(dde.getDeviceID(), createdDevice);
			}
			finally
			{
				this.devicesLock.unlock();
			}
			this.eventBus.publish(EVENT_NEW_DEVICE, this, (IDevice) createdDevice);
			createdDevice.registerEventHandler(this);
			if (BooleanUtils.isTrue(dde.getIsActive()))
				try
				{
					createdDevice.activateDevice();
				}
				catch (DeviceException e)
				{
					logger.error("Could not activate device {} on startup.", dde.getDeviceID());
				}
		}
	}

	@Override
	public IDevice getDeviceById(int deviceID)
	{
		this.devicesLock.lock();
		try
		{
			return devices.get(Integer.valueOf(deviceID));
		}
		finally
		{
			this.devicesLock.unlock();
		}
	}

	@Override
	public IDevice createNewDevice(final IDeviceDescription desc) throws DeviceException
	{
		if (desc.getDeviceID() != IDeviceDescription.NO_ID && desc.getDeviceID() < 1)
			throw new IllegalArgumentException("If specified, device ID must be greater than zero (0).");
		if (desc.getDeviceID() != IDeviceDescription.NO_ID && this.getDeviceById(desc.getDeviceID()) != null)
			throw new IllegalArgumentException("Cannot create new device with an already known ID.");
		
		DeviceDescriptionEntity saveDesc = new DeviceDescriptionEntity(desc);

		IDevice device = null;
		devicesLock.lock();
		try
		{
			if (saveDesc.getDeviceID() == IDeviceDescription.NO_ID)
				saveDesc.setDeviceID(this.searchForNextId());	// must be called within locks - otherwise two devices may have the same number

			saveDesc = repo.save(saveDesc);
			device = this.createDeviceByDescription(saveDesc);
			if (device != null)
				devices.put(Integer.valueOf(saveDesc.getDeviceID()), device);
		}
		finally
		{
			devicesLock.unlock();
		}
		if (device != null)
		{
			this.eventBus.publish(EVENT_NEW_DEVICE, this, device);
			device.registerEventHandler(this);
		}
		return device;
	}

	@Override
	public IDevice updateDevice(IDeviceDescription desc)
	{
		IDevice device = this.getDeviceById(desc.getDeviceID());
		if (device == null)
			throw new IllegalArgumentException("Given Device ID "+desc.getDeviceID()+" is unknown.");
		if (device.getType() != desc.getType())
			throw new IllegalArgumentException("Given description "+desc.getType()+" does not match device type: " + device.getType());
		
		((ADevice<?>) device).updateDevice(desc);	//this will generate a params-changed event which will be handled in the event handler method below.
		return device;
	}

	@Override
	public IDevice deleteDeviceById(int deviceID)
	{
		IDevice deletedDevice = null;
		this.devicesLock.lock();
		try
		{
			Integer deviceIDAsInteger = Integer.valueOf(deviceID);
			deletedDevice = this.devices.remove(deviceIDAsInteger);
			if (deletedDevice == null)
				return null;
			repo.delete(deviceIDAsInteger);
		}
		finally
		{
			this.devicesLock.unlock();
		}
		deletedDevice.deactivateDevice();
		deletedDevice.unregisterEventHandler(this);
		this.eventBus.publish(EVENT_DEVICE_DELETED, this, (IDevice) deletedDevice);
		return deletedDevice;
	}

	@Override
	public SortedSet<IDevice> getAllDevices()
	{
		SortedSet<IDevice> sortedSetOfDevices = new TreeSet<>(DEVICE_SORTER);
		this.devicesLock.lock();
		try
		{
			sortedSetOfDevices.addAll(this.devices.values());
		}
		finally
		{
			this.devicesLock.unlock();
		}
		return sortedSetOfDevices;
	}

	@Override
	public void shutdownService()
	{
		this.onShutdown = true;
		boolean isLock = this.devicesLock.tryLock();
		try
		{
			getAllDevices().forEach(d -> d.deactivateDevice());
		}
		finally
		{
			this.devices.clear();
			if (isLock)
				this.devicesLock.unlock();
		}
	}

	@Override
	public boolean deviceExistsWithID(int deviceID)
	{
		this.devicesLock.lock();
		try
		{
			return this.devices.containsKey(Integer.valueOf(deviceID));
		}
		finally
		{
			devicesLock.unlock();
		}
	}

	private final int searchForNextId()
	{
		int id = 1;
		while (this.deviceExistsWithID(id))
		{
			id++;
		}
		return id;
	}
	
	/**
	 * Creates a new {@link ADevice} object that depends on given
	 * {@link DeviceDescription}. May return null
	 */
	private final ADevice<?> createDeviceByDescription(IDeviceDescription desc)
	{
		if (desc == null || desc.getType() == null)
			return null;

		logger.debug("Creating device for {}.", desc);
		ADevice<?> device = createDeviceByDeviceDescription(desc);

		if (device != null)
			device.updateDevice(desc);
		return device;
	}

	private ADevice<?> createDeviceByDeviceDescription(IDeviceDescription desc)
	{
		try
		{
			return desc.getType().implementation.getConstructor(IDeviceDescription.class).newInstance(desc);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Could not instantiate device", e);
		}
	}

	@Override
	public void handleEvent(String eventID, Object source, Object... params)
	{
		switch (eventID)
		{
			case IDevice.EVENT_STATE_CHANGED:
				if (this.onShutdown)
					break;
			case IDevice.EVENT_PARAMS_UPDATED:
				IDevice device = (IDevice) source;
				repo.save(new DeviceDescriptionEntity(device));
			default:
				break;
		}
	}

}
