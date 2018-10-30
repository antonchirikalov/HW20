package com.toennies.ci1429.app.services.devicetemplates;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.toennies.ci1429.app.model.DeviceType;

@Entity
public class DeviceTemplateEntity implements ITemplateDeviceDescription
{

	@Id
	private Integer templateID;
	private String templateName;
	private int deviceID = -1;
	private DeviceType type;
	private String model;
	private String vendor;
	private String protocolClass;
	@Lob
	@Column(length = 10000)
	private HashMap<String, String> parameters = new HashMap<>();

	public DeviceTemplateEntity() {
	}
	
	public DeviceTemplateEntity(ITemplateDeviceDescription desc)
	{
		this.setDeviceID(desc.getDeviceID());
		this.setDeviceModel(desc.getDeviceModel());
		this.setParameters(desc.getParameters());
		this.setProtocolClass(desc.getProtocolClass());
		this.setTemplateID(desc.getTemplateID());
		this.setTemplateName(desc.getTemplateName());
		this.setType(desc.getType());
		this.setVendor(desc.getVendor());
	}

	public Integer getTemplateID() {
		return templateID;
	}

	public void setTemplateID(Integer templateID) {
		this.templateID = templateID;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public int getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(int deviceID) {
		this.deviceID = deviceID;
	}

	public DeviceType getType() {
		return type;
	}

	public void setType(DeviceType type) {
		this.type = type;
	}

	public String getDeviceModel() {
		return model;
	}

	public void setDeviceModel(String model) {
		this.model = model;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getProtocolClass() {
		return protocolClass;
	}

	public void setProtocolClass(String protocolClass) {
		this.protocolClass = protocolClass;
	}

	public HashMap<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = new HashMap<>(parameters != null ? parameters : Collections.emptyMap());
	}

	@Override
	public String toString() {
		return "DeviceTemplateEntity [templateID=" + templateID + ", templateName=" + templateName + ", deviceID="
				+ deviceID + ", type=" + type + ", model=" + model + ", vendor=" + vendor + ", protocolClass="
				+ protocolClass + ", parameters=" + parameters + "]";
	}

}
