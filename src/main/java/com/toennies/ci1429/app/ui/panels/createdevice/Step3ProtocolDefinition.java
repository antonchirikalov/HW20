
package com.toennies.ci1429.app.ui.panels.createdevice;


import java.util.Map;

import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.network.parameter.ParamDescriptor;
import com.toennies.ci1429.app.network.parameter.typeinformation.ProtocolNameValidator;
import com.toennies.ci1501.lib.components.GenericComboBox;

/**
 * Step 3 of the wizard. Here the user can select a protocol based on the device type selected in step 1.
 * @author renkenh
 */
@SuppressWarnings("serial")
public class Step3ProtocolDefinition extends AbstractSelectionWizardStep
{

	private GenericComboBox<String> protocolBox;


	/**
	 * Standard constructor.
	 */
    public Step3ProtocolDefinition(CreateDeviceWizardController controller)
    {
        super("Protocol", controller);
    }
    
   
	@Override
	protected GenericComboBox<String> createComboBox()
	{
		this.protocolBox = (GenericComboBox<String>) FieldFactory.getProtocolComboBox(null);
		return this.protocolBox;
	}

    @Override
    protected void _updateUI(boolean initial)
    {
    	this.updateComboBox();
    	super._updateUI(initial);
    }
    
    private void updateComboBox()
    {
    	DeviceType type = this.controller.wizardData().getDeviceType();
    	ProtocolNameValidator validator = ((ProtocolNameValidator)((ParamDescriptor) this.protocolBox.getData()).getValidator());
    	if (validator.updateValidator(type))
    	{
    		this.protocolBox.removeAllItems();
    		this.protocolBox.addItems(validator.getValidValues());
    		this.protocolBox.setValue(this.getWizardSelectionData());
    	}
    }

	@Override
	protected String getWizardSelectionData()
	{
		return this.controller.wizardData().getProtocolClassName();
	}

	@Override
	protected void setWizardSelectionData(String selectionData, Map<String, String> parameterData)
	{
		this.controller.wizardData().setStepProtocolData(selectionData, parameterData);		
	}

}
