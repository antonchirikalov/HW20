package com.toennies.ci1429.app.util;

import java.util.ArrayList;
import java.util.List;

import com.toennies.ci1429.app.services.devicetemplates.ITemplateDeviceDescription;
import com.toennies.ci1429.app.services.devicetemplates.TemplateDeviceDescription;
import com.toennies.ci1429.app.ui.panels.createdevice.Step0TemplateDefinition;

public class DeviceTemplateUtil {

	/**
	 * Helper method is needed for displaying {@link TemplateDeviceDescription}
	 * in {@link Step0TemplateDefinition}.
	 * 
	 * Transforms the TemplateDeviceDescription List to a String List. Through
	 * this it's possible to display the
	 * {@link TemplateDeviceDescription#getTemplateName()} instead of
	 * {@link TemplateDeviceDescription#toString()}
	 */
	public static List<String> returnStringList(List<ITemplateDeviceDescription> allTemplates) {
		List<String> stringList = new ArrayList<>();
		allTemplates.stream().forEach(e -> stringList.add(e.getTemplateName()));
		return stringList;
	}
}
