package com.toennies.ci1429.app.services.devicetemplates;

import java.util.List;

public interface DeviceTemplateService
{

	ITemplateDeviceDescription findById(Integer id);

	List<ITemplateDeviceDescription> findAll();

	boolean syncTemplates();

	ITemplateDeviceDescription findByTemplateName(String templateName);

}
