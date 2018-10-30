package com.toennies.ci1429.app.ui.panels.testdevice;

import com.toennies.ci1429.app.model.ResponseFormat;
import com.toennies.ci1429.app.model.scanner.Scanner;
import com.toennies.ci1429.app.services.testcases.TestService;
import com.toennies.ci1501.lib.components.EnumComboBox;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class ScannerTestPanel extends TestDevicePanel<Scanner>
{
	private EnumComboBox<ResponseFormat> responseComboBox;

	private Button startScanning;
	private final TestService testService;
	
	public ScannerTestPanel(Scanner device, TestService service)
	{
		super(device);
		this.testService = service;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setupForm(Component testResultArea)
	{
		responseComboBox = new EnumComboBox<>("Response", ResponseFormat.class, true);

		startScanning = new Button("Start scanning");
		startScanning.addClickListener(
			e -> displayTestResult(this.testService.doScannerTest(device, responseComboBox.getGenericValue())));

		addComponent(responseComboBox);
		addComponent(startScanning);
		addComponent(testResultArea);

		setExpandRatio(responseComboBox, 0);
		setExpandRatio(startScanning, 0);
		setExpandRatio(testResultArea, 1);
	}

}
