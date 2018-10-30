/**
 * 
 */
package com.toennies.ci1429.app.ui.panels.testdevice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.toennies.ci1429.app.model.ADevice;
import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.printer.Printer;
import com.toennies.ci1429.app.model.scale.Scale;
import com.toennies.ci1429.app.model.scanner.Scanner;
import com.toennies.ci1429.app.services.testcases.TestService;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Panel;

/**
 * @author renkenh
 *
 */
@UIScope
@Component
public class UITestService
{

	@Autowired
	private TestService testService;

    /**
     * Creates the test panel for specific device.
     *
     * @param device the device
     * @return the panel
     */
    public Panel createTestPanel(IDevice device) {
        com.vaadin.ui.Component testDevicePanel = createProperTestPanel(device);
        testDevicePanel.setSizeFull();
        Panel returnPanel = new Panel();
        returnPanel.setSizeFull();
        returnPanel.setContent(testDevicePanel);
        return returnPanel;
    }
	
	
	/**
	 * Depending on which {@link DeviceType} is passed to this method,
	 * respective {@link TestDevicePanel} is returned. Because every
	 * {@link DeviceType} has it's own test case panel.
	 * 
	 * So this method returns respective {@link TestDevicePanel} for given
	 * {@link ADevice}.
	 */
	com.vaadin.ui.Component createProperTestPanel(IDevice device)
	{
		switch (device.getType())
		{
			case SCALE:
				return new ScaleTestPanel((Scale) device, this.testService);
			case PRINTER:
				return new PrinterTestPanel((Printer) device, this.testService);
			case SCANNER:
				return new ScannerTestPanel((Scanner) device, this.testService);
			default:
				return new Panel("No Testing Panel available.");
		}
	}

}
