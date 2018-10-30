package com.toennies.ci1429.app.ui.panels.testdevice;

import com.toennies.ci1429.app.model.printer.Printer;
import com.toennies.ci1429.app.services.testcases.TestService;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class PrinterTestPanel extends TestDevicePanel<Printer>
{

	private TextField templateName;
	private Button startPrinter;
	private TextArea rawText;
	private Button startRawPrinter;
	private final TestService testService;


	public PrinterTestPanel(Printer device, TestService testService)
	{
		super(device);
		this.testService = testService;
	}

	@Override
	protected void setupForm(Component testResultArea)
	{
		templateName = new TextField("Template name", "data.ZPL");

		startPrinter = new Button("Start print test", e -> doTestPrinting());

		rawText = new TextArea("Raw text");
		rawText.setInputPrompt("Enter raw text here ...");
		rawText.setWidth("100%");

		startRawPrinter = new Button("Print raw data", e -> doRawPrinting());

		addComponent(templateName);
		addComponent(startPrinter);
		addComponent(rawText);
		addComponent(startRawPrinter);
		addComponent(testResultArea);

		setExpandRatio(templateName, 0);
		setExpandRatio(startPrinter, 0);
		setExpandRatio(rawText, 0);
		setExpandRatio(startRawPrinter, 0);
		setExpandRatio(testResultArea, 1);
	}


	private void doTestPrinting() {
		// There is no need to display result.
		this.testService.doPrinterTest(device, templateName.getValue(), false);
	}

	private void doRawPrinting() {
		this.testService.doPrinterTest(device, rawText.getValue(), true);
	}

}
