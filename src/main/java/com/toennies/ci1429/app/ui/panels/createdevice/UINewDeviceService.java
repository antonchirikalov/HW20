/**
 * 
 */
package com.toennies.ci1429.app.ui.panels.createdevice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.services.devices.IDevicesService;
import com.toennies.ci1429.app.services.devicetemplates.DeviceTemplateService;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.UI;

/**
 * @author renkenh
 *
 */
@Component
@UIScope
public class UINewDeviceService
{

	@Autowired
	private DeviceTemplateService templateService;
	
	@Autowired
	private IDevicesService devicesService;

	/**
	 * 
	 */
	public void startWizard(UI ui)
	{
		CreateDeviceWizardController controller = new CreateDeviceWizardController(templateService, devicesService);
		ModalCreateDevicePanel window = new ModalCreateDevicePanel(controller);
		ui.addWindow(window);
	}
	
    /**
     * Start wizard with predefined device data.
     *
     * @param ui the ui where wizard shows
     * @param device the device info
     */
    public void startUpdateWizard(UI ui, IDevice device) {
        CreateDeviceWizardController controller = new CreateDeviceWizardController(templateService, devicesService);
        Map<String, String> parameters = device.getConfiguration();
        Map<String, String> modifiableParameters = new HashMap<>(device.getParameters());
        String connectorClassName = parameters.get(IProtocol.PARAM_SOCKET);
        controller.wizardData().setAllStepsData(device.getDeviceID(), device.getType(), device.getVendor(), device.getDeviceModel(), modifiableParameters,
                device.getProtocolClass(), connectorClassName);
        // let the wizard know that we update data
        controller.wizardData().setUpdateWizard(true);
        ModalCreateDevicePanel window = new ModalCreateDevicePanel(controller);
        ui.addWindow(window);
    }

}
