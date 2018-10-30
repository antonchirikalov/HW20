package com.toennies.ci1429.app.repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.IDevice.DeviceState;
import com.toennies.ci1429.app.model.IDeviceDescription;


/**
 * @author renkenh
 *
 */
@Entity
public final class DeviceDescriptionEntity implements IDeviceDescription
{

	@Id
	private int deviceID = NO_ID;
	private DeviceType type;

	private Boolean isActive = false;

	private String model;
	private String vendor;

	//Protocol specific Parameters - PLC, TCP, RS232
	private String protocolClass;
	@Lob
	@Column(length=10000)
	private final HashMap<String, String> parameters = new HashMap<>();
	


	public DeviceDescriptionEntity()
	{
		//default constructor for jpa pojo instantiation
	}

	public DeviceDescriptionEntity(IDevice desc)
	{
		this((IDeviceDescription) desc);
		this.setIsActive(Boolean.valueOf(desc.getDeviceState() != DeviceState.NOT_INITIALIZED));
	}

	/**
	 * Copy Constructor.
	 * @param desc The description to copy.
	 */
	public DeviceDescriptionEntity(IDeviceDescription desc)
	{
		this(desc.getDeviceID(), desc.getType(), desc.getDeviceModel(), desc.getVendor(), desc.getProtocolClass(), desc.getParameters());
	}
	
	/**
	 * Constructor without deviceID
	 */
	public DeviceDescriptionEntity(DeviceType type, String model, String vendor, String protocolClass, Map<String, String> parameters)
	{
		this(NO_ID, type, model, vendor, protocolClass, parameters);
	}

	public DeviceDescriptionEntity(int deviceID, DeviceType type, String model, String vendor, String protocolClass, Map<String, String> parameters)
	{
		this.setDeviceID(deviceID);
		this.setType(type);
		this.setDeviceModel(model);
		this.setVendor(vendor);
		this.setProtocolClass(protocolClass);
		this.setParameters(parameters);
	}

	
	public Boolean getIsActive()
	{
		return this.isActive;
	}
	
	public void setIsActive(Boolean isActive)
	{
		this.isActive = isActive;
	}

	public void setDeviceID(int deviceID)
	{
		this.deviceID = deviceID;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getDeviceID()
	{
		return deviceID;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DeviceType getType()
	{
		return type;
	}
	
	public void setType(DeviceType type)
	{
		this.type = type;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getVendor()
	{
		return vendor;
	}
	
	public void setVendor(String vendor)
	{
		this.vendor = vendor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getProtocolClass()
	{
		return protocolClass;
	}

	public void setProtocolClass(String protocolClass)
	{
		this.protocolClass = protocolClass;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDeviceModel()
	{
		return model;
	}
	
	public void setDeviceModel(String deviceType)
	{
		this.model = deviceType;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getParameters()
	{
		return Collections.unmodifiableMap(this.parameters);
	}
	
	public void setParameters(Map<String, String> parameters)
	{
		this.parameters.clear();
		if (parameters != null)
			this.parameters.putAll(parameters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

}