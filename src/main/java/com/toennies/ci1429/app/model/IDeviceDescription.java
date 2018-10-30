package com.toennies.ci1429.app.model;

import java.util.Map;

import com.toennies.ci1429.app.network.protocol.IProtocol;


/**
 * Description of a device. Used to create a device. Used to save the description to the database.
 * @author renkenh
 */
public interface IDeviceDescription
{

	/** CONST indicating that the description does not specify a certain ID. */
	public static final int NO_ID = -1;

	
	/**
	 * @return The id that should be (or is) used for the device that is described by this entity. 
	 */
	public int getDeviceID();

	/**
	 * @return The device class
	 */
	public DeviceType getType();

	/**
	 * @return The vendor.
	 */
	public String getVendor();

	/**
	 * @return The original model description from the vendor. 
	 */
	public String getDeviceModel();

	/**
	 * @return The id of the class that represents the network stack. Must be derived from {@link IProtocol}.
	 */
	public String getProtocolClass();

	
	/**
	 * All parameters set for this device description. This does not contain default parameters used by the different device
	 * and protocol implementations to fulfill their function. It just contains all parameters specified for this device from
	 * extern.
	 * @return A map containing all specified parameters.
	 */
	public Map<String, String> getParameters();

	/**
	 * Provides an null-save access to {@link #getParameters()} attribute. May return
	 * null, if parameter map is null or key not found.
	 */
	public default String returnParameterValueByKey(String key)
	{
		if (key == null)
			return null;
		return this.getParameters().get(key);
	}

}