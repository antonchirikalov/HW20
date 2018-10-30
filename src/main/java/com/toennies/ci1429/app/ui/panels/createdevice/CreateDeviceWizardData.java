package com.toennies.ci1429.app.ui.panels.createdevice;

import java.util.HashMap;
import java.util.Map;

import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.services.devicetemplates.ITemplateDeviceDescription;

public class CreateDeviceWizardData {

	// Formular data step 0 - device template
	private ITemplateDeviceDescription template;

	// Formular data step 1
	private Integer deviceID; // may be null
	private DeviceType deviceType;
	private String manufacturer;
	private String model;
	private boolean isUpdateWizard;

	private Map<String, String> parameters;

	// Formular data step protocol
	private String protocolClassName;

	// Formular data step connector
	private String socketClassName;

	public CreateDeviceWizardData() {
		parameters = new HashMap<>();
	}

	public void setAllStepsData(Integer deviceID, DeviceType deviceType, String manufacturer, String model,
			Map<String, String> parameters, String protocolClassName, String socketClassName) {
		this.deviceID = deviceID;
		this.deviceType = deviceType;
		this.manufacturer = manufacturer;
		this.model = model;
		this.parameters = parameters;
		this.protocolClassName = protocolClassName;
		this.socketClassName = socketClassName;
	}

	public void resetData() {
		this.template = null;
		setStep1Data(null, null, null, null);
		parameters.clear();
		setStepSocketData(null, null);
		setStepProtocolData(null, null);
	}

	public void setStep0Data(ITemplateDeviceDescription template) {
		resetData();
		this.template = template;
		// a template is available, set fill wizard data with information stored in
		// template
		if (template != null) {
			String templateConnectorInformation = (template.getParameters() != null)
					? template.getParameters().get("connector") : null;
			setAllStepsData(template.getDeviceID(), template.getType(), template.getVendor(), template.getDeviceModel(),
					template.getParameters(), template.getProtocolClass(), templateConnectorInformation);
		}
	}

	public void setStep1Data(Integer deviceID, DeviceType deviceType, String manufacturer, String model) {
		this.deviceID = deviceID;
		this.deviceType = deviceType;
		this.manufacturer = manufacturer;
		this.model = model;

	}
	
	public void setStep1Data(Map<String, String> runtimeValues){
	    extendParameters(runtimeValues);
	}

	public void setStepProtocolData(String protocolClassName, Map<String, String> protocolDescription) {
		this.protocolClassName = protocolClassName;
		extendParameters(protocolDescription);
	}

	public void setStepSocketData(String socketClassName, Map<String, String> socketDescription) {
		this.socketClassName = socketClassName;

		extendParameters(socketDescription);
	}

	private void extendParameters(Map<String, String> data2Add) {
		if (data2Add != null) {
			parameters.putAll(data2Add);
		}
	}

	public ITemplateDeviceDescription getTemplate() {
		return template;
	}

	public Integer getDeviceID() {
		return deviceID;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public String getModel() {
		return model;
	}

	public String getProtocolClassName() {
		return protocolClassName;
	}

	public String getSocketClassName() {
		return socketClassName;
	}

	public Map<String, String> getParameters() {
		// socket information needs to be stored in param map, too.
		parameters.put(IProtocol.PARAM_SOCKET, this.socketClassName);
		return parameters;
	}

	public boolean isUpdateWizard() {
		return isUpdateWizard;
	}

	public void setUpdateWizard(boolean isUpdateWizard) {
		this.isUpdateWizard = isUpdateWizard;
	}

}
