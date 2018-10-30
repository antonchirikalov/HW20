package com.toennies.ci1429.app.ui.panels.createdevice;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

import com.toennies.ci1429.app.model.DeviceException;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.IDeviceDescription;
import com.toennies.ci1429.app.repository.DeviceDescriptionEntity;
import com.toennies.ci1429.app.services.devices.IDevicesService;
import com.toennies.ci1429.app.services.devicetemplates.DeviceTemplateService;
import com.toennies.ci1429.app.services.devicetemplates.ITemplateDeviceDescription;

public class CreateDeviceWizardController implements WizardProgressListener, WizardValueChangedListener
{
	private final CreateDeviceWizardData wizardData;

	// Stores listener that gets informed, if Wizard*Event events occur
	private final List<WizardProgressListener> externalWizardProgessListener;
	private final List<WizardValueChangedListener> externalWizardValueChangedListener;
	
	private final DeviceTemplateService templateService;
	private final IDevicesService devicesService;


	public CreateDeviceWizardController(DeviceTemplateService templateService, IDevicesService devicesService)
	{
		this.templateService = templateService;
		this.devicesService  = devicesService;
		wizardData = new CreateDeviceWizardData();
		externalWizardProgessListener = new ArrayList<>();
		externalWizardValueChangedListener = new ArrayList<>();
	}

	/**
	 * Method that creates new device. Collects data entered in GUI and creates
	 * a {@link IDeviceDescription} object.
	 *
	 * After all, the created device gets added to a panel.
	 */
	public IDevice createDevice() throws DeviceException
	{
        int deviceId = wizardData.getDeviceID() == null ? IDeviceDescription.NO_ID : wizardData.getDeviceID();
		IDeviceDescription deviceDescription = new DeviceDescriptionEntity(deviceId, wizardData.getDeviceType(),
				wizardData.getModel(), wizardData.getManufacturer(), wizardData.getProtocolClassName(),
				wizardData.getParameters());

		// updateDevice method can be used for saving a new device or updating a
		// present one.
		IDevice createdDevice = this.devicesService.createNewDevice(deviceDescription);
		// device may be null
		return createdDevice;
	}
	

    public IDevice updateDevice() throws DeviceException {
        IDeviceDescription deviceDescription = new DeviceDescriptionEntity(wizardData.getDeviceID(), wizardData.getDeviceType(), wizardData.getModel(),
                wizardData.getManufacturer(), wizardData.getProtocolClassName(), wizardData.getParameters());
        return this.devicesService.updateDevice(deviceDescription);
    }
	
    /**
     * Checks if is device id exist.
     */
    public boolean isDeviceIdExist(int id) {
        return devicesService.deviceExistsWithID(id);
    }
    
	public List<ITemplateDeviceDescription> getTemplates()
	{
		return this.templateService.findAll();
	}
	
	public CreateDeviceWizardData wizardData()
	{
		return this.wizardData;
	}

	public void addWizardProgressListener(WizardProgressListener listener) {
		externalWizardProgessListener.add(listener);
	}

	public void addWizardValueChangedListener(WizardValueChangedListener listener) {
		externalWizardValueChangedListener.add(listener);
	}
	
	@Override
	public void activeStepChanged(WizardStepActivationEvent event) {
		externalWizardProgessListener.stream().forEach(e -> e.activeStepChanged(event));
	}

	@Override
	public void stepSetChanged(WizardStepSetChangedEvent event) {
		externalWizardProgessListener.stream().forEach(e -> e.stepSetChanged(event));
	}

	@Override
	public void wizardCompleted(WizardCompletedEvent event) {
		externalWizardProgessListener.stream().forEach(e -> e.wizardCompleted(event));
	}

	@Override
	public void wizardCancelled(WizardCancelledEvent event) {
		externalWizardProgessListener.stream().forEach(e -> e.wizardCancelled(event));
	}

	@Override
	public void wizardValueChanged(boolean changedValue) {
		externalWizardValueChangedListener.stream().forEach(e -> e.wizardValueChanged(changedValue));
	}

}
