package com.toennies.ci1429.app.ui.panels.testdevice;

import java.util.Arrays;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.ResponseFormat;
import com.toennies.ci1429.app.model.scale.Commands.Command;
import com.toennies.ci1429.app.model.scale.Scale;
import com.toennies.ci1429.app.services.testcases.TestService;
import com.toennies.ci1429.app.util.ScaleUtil;
import com.toennies.ci1501.lib.components.EnumComboBox;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class ScaleTestPanel extends TestDevicePanel<Scale>
{

	private ComboBox commandComboBox;
	private EnumComboBox<ResponseFormat> responseComboBox;
	private EnumComboBox<com.toennies.ci1429.app.model.scale.Scale.Unit> unitComboBox;

	private Button startScale;
	private final TestService testService;


	public ScaleTestPanel(Scale scale, TestService testService)
	{
		super(scale);
		this.testService = testService;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setupForm(Component testResultArea)
	{
		commandComboBox = new ComboBox("Command", Arrays.asList(ScaleUtil.getCommands(this.device.getProtocolClass())));
		responseComboBox = new EnumComboBox<>("Response", ResponseFormat.class, true);
		unitComboBox = new EnumComboBox<>("Unit", com.toennies.ci1429.app.model.scale.Scale.Unit.class, true);

		startScale = new Button("Start scaling", e -> doScaling());

		addComponent(commandComboBox);
		addComponent(responseComboBox);
		addComponent(unitComboBox);
		addComponent(startScale);
		addComponent(testResultArea);

		setExpandRatio(commandComboBox, 0);
		setExpandRatio(responseComboBox, 0);
		setExpandRatio(unitComboBox, 0);
		setExpandRatio(startScale, 0);
		setExpandRatio(testResultArea, 1);
	}

	private void doScaling() {
		DeviceResponse testResult = this.testService.doScaleTest(device, (Command) commandComboBox.getValue(),
				responseComboBox.getGenericValue(),
				unitComboBox.getGenericValue());
		super.displayTestResult(testResult);
	}

}
