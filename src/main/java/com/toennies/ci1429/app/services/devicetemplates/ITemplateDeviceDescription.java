package com.toennies.ci1429.app.services.devicetemplates;

import org.springframework.hateoas.Identifiable;

import com.toennies.ci1429.app.model.IDeviceDescription;


public interface ITemplateDeviceDescription extends IDeviceDescription, Identifiable<Integer>
{

	public default Integer getId()
	{
		return this.getTemplateID();
	}

	public Integer getTemplateID();

	public String getTemplateName();

}