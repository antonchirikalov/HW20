package com.toennies.ci1429.app.ui.panels.testdevice;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.services.logging.LogEvent;
import com.toennies.ci1429.app.services.logging.LogEvent.EventType;
import com.toennies.ci1429.app.ui.components.EventConverter;
import com.toennies.ci1429.app.util.LogbookUtil;
import com.toennies.ci1501.lib.components.ReadOnlyTextField;
import com.toennies.ci1574.lib.helper.Generics;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * Every device can easily be tested via gui. So every respective device type
 * needs test case gui. This class represents the base class for this test case
 * guis. See sublcasses.
 */
@SuppressWarnings("serial")
public abstract class TestDevicePanel<D extends IDevice> extends VerticalLayout
{

	private final ObjectProperty<LogEvent[]> property = new ObjectProperty<>(null, Generics.convertUnchecked(LogEvent[].class));
	/**
	 * {@link #addComponent(com.vaadin.ui.Component)} needed to be invoked for
	 * this one.
	 */
	private final Label testResultLabel = new Label();
	private final Panel testResultPanel = new Panel("Result", this.testResultLabel);
	{
		testResultLabel.setContentMode(ContentMode.HTML);
		testResultLabel.setImmediate(true);
		testResultLabel.setPropertyDataSource(this.property);
		testResultLabel.setConverter(new EventConverter());
		CssLayout cssLayout = new CssLayout(this.testResultLabel)
		{
		    @Override
		    protected String getCss(Component c) {
		        return "overflow: unset";
		    }
		};
		testResultPanel.setContent(cssLayout);
		testResultPanel.setSizeFull();
	}


	/**
	 * For this device test case will be executed.
	 */
	protected final D device;

	public TestDevicePanel(D device)
	{
		this.device = device;

		ReadOnlyTextField deviceId = new ReadOnlyTextField("Device ID", String.valueOf(device.getDeviceID()));
		this.addComponent(deviceId);
		this.setExpandRatio(deviceId, 0);

		this.setupForm(this.testResultPanel);
		this.setSizeFull();
		this.setMargin(true);
		this.setSpacing(true);
	}

	/**
	 * Subclasses have to implement this method. In implementation of method,
	 * all components for test dialog should be added. After this,
	 * {@link #exitDialogButton} is added to dialog.
	 */
	protected abstract void setupForm(Component testResultArea);

	/**
	 * Invoke this method in order to display test result to user. Does nothing
	 * else than adding testResult#toString to {@link #testResultTextArea}
	 */
	protected void displayTestResult(DeviceResponse response)
	{
		LogEvent event = new LogEvent("", "", EventType.INFO, LogbookUtil.convertToString(response));
		this.property.setValue(new LogEvent[] { event });
	}

}
