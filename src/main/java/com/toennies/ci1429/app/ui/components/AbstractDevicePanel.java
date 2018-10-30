package com.toennies.ci1429.app.ui.components;

import com.toennies.ci1429.app.model.ADevice;
import com.toennies.ci1429.app.model.IDevice;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;

/**
 * Abstract Panel that displays {@link ADevice} objects. See subclasses
 */
@SuppressWarnings("serial")
public abstract class AbstractDevicePanel extends FormLayout {

	protected final IDevice aDevice;

	/**
	 * Takes an {@link ADevice} and prepares everything to display it.
	 */
	public AbstractDevicePanel(IDevice aDevice) {
		this.aDevice = aDevice;

		if (this.aDevice != null) {
			// only display the device, if it's valid
			addComponents();
		} else {
			showDeviceNotValidText();
		}
	}

	/**
	 * This method adds respective GUI components to display the device.
	 * Implemenation depends on Subclass.
	 */
	protected abstract void addComponents();

	/**
	 * Adds components for signal a invalid device. Override if you want to!
	 */
	protected void showDeviceNotValidText() {
		addComponent(new Label("Device is not valid!"));
	}

}
