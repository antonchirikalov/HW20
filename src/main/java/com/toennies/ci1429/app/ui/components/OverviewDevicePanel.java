package com.toennies.ci1429.app.ui.components;

import com.toennies.ci1429.app.model.ADevice;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1501.lib.components.ReadOnlyTextField;

/**
 * Panel that displays an overview of given {@link ADevice} object.
 */
@SuppressWarnings("serial")
public class OverviewDevicePanel extends AbstractDevicePanel {

	public OverviewDevicePanel(IDevice aDevice) {
		super(aDevice);
	}

	@Override
	protected void addComponents() {

		ReadOnlyTextField deviceId = new ReadOnlyTextField("Device ID",
				Integer.valueOf(aDevice.getDeviceID()).toString());
		ReadOnlyTextField modelName = new ReadOnlyTextField("Modelname", aDevice.getDeviceModel());
		ReadOnlyTextField vendor = new ReadOnlyTextField("Vendor", aDevice.getVendor());

		addComponent(deviceId);
		addComponent(modelName);
		addComponent(vendor);
	}

}
