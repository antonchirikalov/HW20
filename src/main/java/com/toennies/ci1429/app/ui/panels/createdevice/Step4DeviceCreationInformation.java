package com.toennies.ci1429.app.ui.panels.createdevice;

import com.toennies.ci1429.app.model.DeviceException;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Step 4 of the wizard. here the device is eventually created and a message about success or failure is shown
 * to the user.
 * @author renkenh
 */
public class Step4DeviceCreationInformation extends AbstractWizardStep
{

	private Label resultLabel;


	public Step4DeviceCreationInformation(CreateDeviceWizardController controller)
	{
		super(controller);
		this.initUI();
	}


	@Override
	public String getCaption() {
		return "Device creation information";
	}

	@Override
	protected void setupForm(final FormLayout formLayout)
	{
		this.resultLabel = new Label();
		formLayout.addComponent(this.resultLabel);
	}


    public Component getContent()
    {
    	this.resultLabel.setValue("");
    	this.resultLabel.removeStyleName(ValoTheme.LABEL_SUCCESS);
    	this.resultLabel.removeStyleName(ValoTheme.LABEL_FAILURE);
    	
    	String message = "";
    	String style = "";
    	if (this.createDevice())
    	{
    		message = "Device successfully ";
    		if (controller.wizardData().isUpdateWizard())
    			message += "updated.";
    		else
    			message += "created.";
    		style = ValoTheme.LABEL_SUCCESS;
    	}
    	else
    	{
    		message = "Error during device creation.";
    		style = ValoTheme.LABEL_FAILURE;
    	}
    	this.resultLabel.setValue(message);
    	this.resultLabel.addStyleName(style);
    	return super.getContent();
    }
    
    private boolean createDevice()
    {
		try
		{
			if(controller.wizardData().isUpdateWizard())
		        controller.updateDevice();
		    else
		    	controller.createDevice();
			return true;
		}
		catch (DeviceException e)
		{
			return false;
		}
    }

	@Override
	protected void updateWizardData()
	{
		// not required in this step
	}

	@Override
	protected void updateUI()
	{
		//
	}

	@Override
	protected boolean isValid()
	{
		return true;
	}

}
