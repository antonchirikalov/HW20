package com.toennies.ci1429.app.ui.panels.createdevice;

import java.util.Map;

import com.toennies.ci1429.app.network.parameter.Parameters;
import com.toennies.ci1501.lib.components.GenericComboBox;
import com.vaadin.ui.FormLayout;

/**
 * Further abstraction of the wizard step. This steps shows a simple drop down field and 
 * manages subsequent fields based on the selections being made.
 * @author renkenh
 */
@SuppressWarnings("serial")
public abstract class AbstractSelectionWizardStep extends AbstractWizardStep
{

	private final String caption;
	protected FormLayout formLayout;
    private GenericComboBox<String> comboBox;
    private ManagedFieldCollection managedFieldCollection;
    

    /**
     * Constructor.
     * @param caption The caption used to show a description for the managed fields.
     * @param controller The controller for this wizard.
     */
    public AbstractSelectionWizardStep(String caption, CreateDeviceWizardController controller)
    {
        super(controller);
        this.caption = caption;
		this.initUI();
    }


    @Override
    public String getCaption()
    {
        return this.caption+" Configuration";
    }

    @Override
    protected final void setupForm(final FormLayout formLayout)
    {
    	this.formLayout = formLayout;
        this.comboBox = this.createComboBox();
    	//set value when this is update - set it before adding the value change listener - do not trigger updateUI
    	if (this.controller.wizardData().isUpdateWizard())
    		this.comboBox.setValue(this.getWizardSelectionData());
        this.comboBox.addValueChangeListener(event -> { this.updateUI(); this.onInputChange(); });
        formLayout.addComponent(comboBox);
        this._updateUI(true);
    }
    
    /**
     * Called to create a combobox which is used for the overall selection.
     * @return The combobox for the initial selection.
     */
    protected abstract GenericComboBox<String> createComboBox();

    @Override
    protected void updateUI()
    {
    	this._updateUI(false);
    }
    
    /**
     * Method to update the actual UI in context whether its initial (constructor or not).
     * This is needed since there is a difference when updating a device (where something must be preselected).
     */
    protected void _updateUI(boolean initial)
    {
    	String selected = comboBox.getGenericValue();
    	String previous = this.getWizardSelectionData();
    	if (!initial && (selected == previous || selected != null && selected.equals(previous)))
    	{
    		if (this.managedFieldCollection != null)
    			this.managedFieldCollection.initForm(this.controller.wizardData().getParameters());
    		return;
    	}
    	
    	if (this.managedFieldCollection != null)
    		this.managedFieldCollection.shutdownForm();
    	
    	if (selected == null)
    		return;
    	
    	this.managedFieldCollection = new ManagedFieldCollection(Parameters.getParameters(selected).values(), this.caption+" Parameters", this.formLayout, this);
    	this.managedFieldCollection.initForm(this.controller.wizardData().getParameters());
    }
    
    /**
     * Returns the last selection saved to the wizard controller for this step/combobox.
     * This method is used to restore the state of the current step.
     * @return The selection saved earlier to the controller.
     */
    protected abstract String getWizardSelectionData();

	@Override
	protected final void updateWizardData()
	{
		Map<String, String> parameterData = null;
		if (this.managedFieldCollection != null)
			parameterData = managedFieldCollection.getDataMap();
	    this.setWizardSelectionData(comboBox.getGenericValue(), parameterData);
	}

	/**
	 * Method to save the current data to the controller.
	 * @param selectionData The current selection from the combobox.
	 * @param parameterData The parameter values of the managed fields. May be <code>null</code>.
	 */
    protected abstract void setWizardSelectionData(String selectionData, Map<String, String> parameterData);
    
    @Override
    protected boolean isValid()
    {
        return comboBox.isValid() && managedFieldCollection != null && managedFieldCollection.isValid();
    }

}
