package com.toennies.ci1429.app.services.devices;

import java.util.Comparator;
import java.util.SortedSet;

import com.toennies.ci1429.app.model.DeviceException;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.IDeviceDescription;

/**
 * Defines methods for devices connected with hw20 instance. Needs a generic
 * type because of sub classes. See {@link DevicesService}
 */
public interface IDevicesService
{
	
	/**
	 * Event that indicates that a new device has been added to this service.
	 * Payload is the {@link IDevice}.
	 */
	public static final String EVENT_NEW_DEVICE = "EVENT_NEW_DEVICE";

	/**
	 * Event that indicates that a known device has been deleted from this service.
	 * Payload is the {@link IDevice}.
	 */
	public static final String EVENT_DEVICE_DELETED = "EVENT_DEVICE_DELETED";

	
	/**
	 * Returns all devices in a {@link SortedSet}. Sort is done with default
	 * {@link Comparator}.
	 */
	public SortedSet<IDevice> getAllDevices();

	/**
	 * Returns device with given deviceID. May return null.
	 */
	public IDevice getDeviceById(int deviceID);

	/**
	 * Tries to save a new device. May return null, if an error occurs.
	 */
	public IDevice createNewDevice(IDeviceDescription desc) throws DeviceException;

	/**
	 * Tries to update an already present device. May return null, if an error
	 * occurs.
	 */
	public IDevice updateDevice(IDeviceDescription desc) throws DeviceException;

	/**
	 * Tries to delete a device. Deletion is based on given
	 * {@link IDeviceDescription}. Returns the deleted device. May return null,
	 * if no device is found.
	 */
	public default IDevice deleteDevice(IDeviceDescription desc)
	{
		return this.deleteDeviceById(desc.getDeviceID());
	}

	/**
	 * Tries to delete a device. Deletion is based on given deviceID. Returns
	 * the deleted device. May return null, if no device is found.
	 */
	public IDevice deleteDeviceById(int deviceID);

	/**
	 * {@link IDevice#deactivateDevice()} is invoked for every device.
	 */
	public void shutdownService();

	/**
	 * @return true if given deviceID is already used by another device.
	 */
	public boolean deviceExistsWithID(int deviceID);

}
