package com.toennies.ci1429.app.ui.panels.createdevice;

import org.vaadin.teemu.wizards.WizardStep;

import com.toennies.ci1429.app.network.parameter.ParamDescriptor;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Abstract implementation of the WizardStep interface. Provides convenient methods
 * for validation, description display and update handling.
 * @author renkenh
 */
public abstract class AbstractWizardStep implements WizardStep, FocusListener, ValueChangeListener
{

	/** The wizard controller containing currently entered data. */
	protected final CreateDeviceWizardController controller;
	private final TextArea paraDescriptionArea = new TextArea();
	{
//		this.paraDescriptionArea.setCaption("Parameter Description");
		this.paraDescriptionArea.setSizeFull();
		this.paraDescriptionArea.setImmediate(true);
		this.paraDescriptionArea.setEnabled(false);
	}
    private final HorizontalLayout stepRoot = new HorizontalLayout();
    {
    	this.stepRoot.setSizeFull();
    	this.stepRoot.setSpacing(true);
    	this.stepRoot.setMargin(true);
//    	this.stepRoot.addStyleName("param-descriptor");
    }


    /**
     * Create a new wizard step and initializes the UI. After calling this constructor,
     * the developer has to call in the derived types {@link #initUI()} himself!
     * @param controller The controller used by the fields to save data between steps.
     */
	public AbstractWizardStep(CreateDeviceWizardController controller)
	{
		this.controller = controller;
	}

	
	/**
	 * Initializes the UI. Must be called by derived types after constructor calling. 
	 */
	protected final void initUI()
	{
		final FormLayout form = new FormLayout();
		form.setMargin(true);
		form.setSpacing(true);
		form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);

		form.addComponent(FieldFactory.createFormHeading("Parameters"));
		this.setupForm(form);

		VerticalLayout paramDescrLayout = new VerticalLayout();
		paramDescrLayout.addComponents(FieldFactory.createFormHeading("Description"), this.paraDescriptionArea);
		paramDescrLayout.setSizeFull();
		paramDescrLayout.setExpandRatio(this.paraDescriptionArea, 1);
		Panel wrapper = new Panel();
		wrapper.addStyleName(ValoTheme.PANEL_BORDERLESS);
		wrapper.setContent(form);
		wrapper.setSizeFull();
		this.stepRoot.addComponents(wrapper, paramDescrLayout);
		this.stepRoot.setExpandRatio(wrapper, 3);
		this.stepRoot.setExpandRatio(paramDescrLayout, 1);
	}
	
	/**
	 * Used by derived implementations to initialize the given layout with fields.
	 * @param form The form to initialize. Its the root form which should contain all needed fields.
	 */
	protected abstract void setupForm(FormLayout form);

	/**
	 * Has to write all entered values into the controller. Gets invoked on step change (advancing or going back).
	 */
	protected abstract void updateWizardData();

	
	/**
	 * Should update the UI with the current data provided by the controller.
	 * Is called every time before a step is shown to the user. 
	 */
	protected abstract void updateUI();

	
	@Override
	public boolean onBack()
	{
		this.updateWizardData();
		return true;
	}

	@Override
    public Component getContent()
    {
    	this.updateUI();
    	this.onInputChange();
    	return this.stepRoot;
    }

	/**
	 * Returns whether the currently available information is valid (against the requirements provided by the overall data model).
	 * This is done for the current step only. If this method returns true, the user may advance to the next wizard step.
	 */
    protected abstract boolean isValid();

	@Override
	public boolean onAdvance() {
		updateWizardData();
		return this.isValid();
	}

	/**
	 * Do some actions on wizard input data was changed.
	 */
	protected final void onInputChange()
	{
		boolean isValid = isValid();
		this.controller.wizardValueChanged(isValid);
	}
	
    /**
     * When Combobox is updated, ParameterGrid needs to be updated, too. Update is accomplished through adding new parametergrid to ui.
     */
    @Override
    public final void valueChange(ValueChangeEvent event)
    {
        onInputChange();
    }

	@Override
	public final void focus(FocusEvent event)
	{
		AbstractComponent source = (AbstractComponent) event.getSource();
		ParamDescriptor descriptor = (ParamDescriptor) source.getData();
		paraDescriptionArea.setValue(descriptor != null ? descriptor.getToolTip() : "");
	}
	
	private static final long serialVersionUID = 1L;
}
