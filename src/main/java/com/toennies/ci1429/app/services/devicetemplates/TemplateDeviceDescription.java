package com.toennies.ci1429.app.services.devicetemplates;

import java.util.Map;

import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.IDeviceDescription;
import com.toennies.ci1429.app.repository.DeviceDescriptionEntity;

/**
 * Pojo that stores device template data. Through templates device creation is
 * simplified. Extends from {@link DeviceDescription} and adds two additional
 * attributes.
 */
public class TemplateDeviceDescription implements IDeviceDescription, ITemplateDeviceDescription
{

	private Integer templateID;
	private String templateName;
	private DeviceDescriptionEntity description;


	public TemplateDeviceDescription()
	{
		//empty constructor
	}

	public TemplateDeviceDescription(Integer templateID, String templateName, int deviceID, DeviceType type,
			String model, String vendor, String protocolClass, Map<String, String> parameters)
	{
		this.description = new DeviceDescriptionEntity(type, model, vendor, protocolClass, parameters);
		this.templateID = templateID;
		this.templateName = templateName;
	}


	@Override
	public Integer getTemplateID() {
		return templateID;
	}

	public void setTemplateID(Integer templateID) {
		this.templateID = templateID;
	}


	@Override
	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	@Override
	public int getDeviceID()
	{
		return NO_ID;
	}

	@Override
	public DeviceType getType()
	{
		return this.description.getType();
	}

	@Override
	public String getVendor()
	{
		return this.description.getVendor();
	}

	@Override
	public String getDeviceModel()
	{
		return this.description.getDeviceModel();
	}

	@Override
	public String getProtocolClass()
	{
		return this.description.getProtocolClass();
	}

	@Override
	public Map<String, String> getParameters()
	{
		return this.description.getParameters();
	}
	
	@Override
	public String toString() {
		return "TemplateDeviceDescription [templateID=" + templateID + ", templateName=" + templateName + ", deviceID="
				+ this.getDeviceID() + ", type=" + this.getType() + ", model=" + this.getDeviceModel() + ", vendor=" + this.getVendor() + ", protocolClass="
				+ this.getProtocolClass() + ", parameters=" + this.getParameters() + "]";
	}

}
