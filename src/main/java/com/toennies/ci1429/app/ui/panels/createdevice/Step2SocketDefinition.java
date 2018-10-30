package com.toennies.ci1429.app.ui.panels.createdevice;

import java.util.Map;

import com.toennies.ci1501.lib.components.GenericComboBox;

/**
 * Step 2 of the wizard. Here the user can select a socket over which the hardware can be reached.
 * @author renkenh
 */
@SuppressWarnings("serial")
public class Step2SocketDefinition extends AbstractSelectionWizardStep
{

	/**
	 * Standard Constructor.
	 */
    public Step2SocketDefinition(CreateDeviceWizardController controller)
    {
        super("Socket", controller);
    }


	@Override
	protected GenericComboBox<String> createComboBox()
	{
		return (GenericComboBox<String>) FieldFactory.getSocketComboBox();
	}

    @Override
    protected void setWizardSelectionData(String selectionData, Map<String, String> parameterData)
    {
        this.controller.wizardData().setStepSocketData(selectionData, parameterData);
    }

	@Override
	protected String getWizardSelectionData()
	{
		return this.controller.wizardData().getSocketClassName();
	}

}
