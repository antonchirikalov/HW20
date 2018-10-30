package com.toennies.ci1429.app.ui.components;

import java.util.List;

import com.toennies.ci1429.app.services.devicetemplates.TemplateDeviceDescription;
import com.toennies.ci1501.lib.components.GenericComboBox;
import com.toennies.ci1501.lib.utils.ContainerUtils;

/**
 * ComboBox that displays {@link TemplateDeviceDescription} objects
 */
@SuppressWarnings("serial")
public class TemplateDeviceComboBox extends GenericComboBox<TemplateDeviceDescription> {

	public TemplateDeviceComboBox(List<TemplateDeviceDescription> allTemplates) {
		super("Template", ContainerUtils.returnBeanItemContainer(TemplateDeviceDescription.class, allTemplates));
	}

}
