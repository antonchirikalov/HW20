
package com.toennies.ci1429.app.ui.panels.createdevice;

import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;

import com.toennies.ci1501.lib.components.ActionConfirmationDialog;
import com.toennies.ci1501.lib.components.DeleteButton;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Modal Panel for displaying the device creation functionality.
 */
@SuppressWarnings("serial")
public class ModalCreateDevicePanel extends Window
{

	private final VerticalLayout root = new VerticalLayout();
	private final ChangeableStepWizard wizard = new ChangeableStepWizard();
	private final CreateDeviceWizardController controller;
	
	private static final float CREATE_DEVICE_PANEL_WIDTH = 950;
	private static final float CREATE_DEVICE_PANEL_HEIGHT = 690;

	public ModalCreateDevicePanel(CreateDeviceWizardController controller)
	{
		this.controller = controller;
		this.setupWizard();
		this.root.addComponent(this.wizard);
		this.root.setComponentAlignment(this.wizard, Alignment.TOP_CENTER);
		this.root.setResponsive(true);
		this.root.setMargin(true);
		this.root.setSizeFull();

		setContent(this.root);
		setWidth(CREATE_DEVICE_PANEL_WIDTH, Unit.PIXELS);
		setHeight(CREATE_DEVICE_PANEL_HEIGHT, Unit.PIXELS);
		setModal(true);
		setResizable(false);
		setClosable(false);
		center();

		CreateDeviceWizardProgressListener listener = new CreateDeviceWizardProgressListener();
		controller.addWizardProgressListener(listener);
		controller.addWizardValueChangedListener(ModalCreateDevicePanel.this.wizard.getNextButton()::setEnabled);
	}

	private void setupWizard()
	{
		this.wizard.addStep(new Step1DeviceType(this.controller));
		this.wizard.addStep(new Step2SocketDefinition(this.controller));
		this.wizard.addStep(new Step3ProtocolDefinition(this.controller));
		this.wizard.addStep(new Step4DeviceCreationInformation(this.controller));
		this.wizard.addListener(this.controller);
		this.controller.addWizardValueChangedListener(this.wizard);

		this.wizard.setSizeFull();
		this.wizard.updateButtonStates();
		this.wizard.getFinishButton().setStyleName(ValoTheme.BUTTON_PRIMARY);
		this.wizard.getCancelButton().setStyleName(ValoTheme.BUTTON_DANGER);
	}

	/**
	 * Class CreateDeviceWizardProgressListener will listen and handle for
	 * events on click wizard buttons
	 * 
	 * @author Le Minh Tri
	 *
	 */
	private class CreateDeviceWizardProgressListener extends AbstractWizardProgressListener {
		@Override
		public void activeStepChanged(WizardStepActivationEvent event) {
			super.activeStepChanged(event);
			center();
		}

		@Override
		public void wizardCompleted(WizardCompletedEvent event) {
			// CI1429APP-44 Finish im Wizard should close window
			close();
			super.wizardCompleted(event);
		}

		@Override
		public void wizardCancelled(WizardCancelledEvent event)
		{
			String cancelText = "Do you want to cancel creating new device?";
			if (controller.wizardData().isUpdateWizard())
				cancelText = "Do you want to cancel editing the device?";
			// Close im Wizard should close window
			UI.getCurrent().addWindow(new ActionConfirmationDialog(DeleteButton.DEFAULT_OK_TEXT, DeleteButton.DEFAULT_CANCEL_TEXT, cancelText, () -> close()));
			super.wizardCancelled(event);
		}
	}
	
}
