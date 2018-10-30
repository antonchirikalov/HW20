/*
 * DeviceDescriptionFromLayout.java
 * 
 * Created on Mar 30, 2017
 * 
 * Copyright (C) 2017 Toennies, All rights reserved.
 */
package com.toennies.ci1429.app.ui.panels.createdevice;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.toennies.ci1429.app.network.parameter.ParamDescriptor;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;

/**
 * This class is used to manage fields based on a selection. The fields are added to (and removed from) a given
 * FormLayout with components from collection of parameter descriptor.
 * Should be used with {@link AbstractSelectionWizardStep} only!
 * @author LuanVT1
 * @author renkenh
 */
public class ManagedFieldCollection
{

    private final PropertysetItem propertysetItem = new PropertysetItem();
    private final Collection<ParamDescriptor> paramDescriptors;
    private final FormLayout layout;
    private final AbstractSelectionWizardStep wizardStep;

   
    /**
     * Instantiates a new device description panel.
     *
     * @param paramDescriptors the param descriptors
     * @param userEnteredData the user entered data
     * @param caption the caption
     */
    public ManagedFieldCollection(Collection<ParamDescriptor> paramDescriptors, String caption, FormLayout layout, AbstractSelectionWizardStep wizardStep)
    {
//    	this.setSizeFull();
//        this.setMargin(true);
//        this.setSpacing(true);
//        this.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
//        this.addStyleName("form-scrollable");
        
    	this.layout = layout;
    	this.wizardStep = wizardStep;
        this.paramDescriptors = paramDescriptors;
        setupStructure(caption);
        this.addValueChangeListener(wizardStep);
        this.addFocusListener(wizardStep);
    }
    
    /**
     * build component by descriptor.
     *
     * @param userEnteredData the user entered data
     */
    private void setupStructure(String caption)
    {
    	this.layout.addComponent(FieldFactory.createFormHeading(caption));
        for (ParamDescriptor descriptor : this.paramDescriptors)
        {
            AbstractComponent fieldByParamDescriptor = FieldFactory.getFieldByParamDescriptor(descriptor);
            fieldByParamDescriptor.setSizeFull();
            String descriptorName = descriptor.getName();
            this.layout.addComponent(fieldByParamDescriptor);
            ObjectProperty<String> property = new ObjectProperty<>(null, String.class);
            propertysetItem.addItemProperty(descriptorName, property);
            extractField(fieldByParamDescriptor).setPropertyDataSource(property);
        }
    }
    
    /**
     * Tries to find recursively the first available field. Needed since the checkbox is wrapped by
     * the {@link FieldFactory} within a horizontal layout.
     */
    private static final AbstractField<?> extractField(Component component)
    {
    	if (component instanceof AbstractField)
    		return (AbstractField<?>) component;
    	
    	if (component instanceof ComponentContainer)
    	{
    		ComponentContainer container = (ComponentContainer) component;
    		for (Component comp : container)
    		{
    			AbstractField<?> field = extractField(comp);
    			if (field != null)
    				return field;
    		}
    	}
    	return null;
    }

    /**
     * Update all fields with current data. Used by the steps to update the UI.
     * @param userEnteredData The parameters from the controller. May be <code>null</code>.
     */
    public void initForm(Map<String, String> userEnteredData)
    {
        for (ParamDescriptor descriptor : paramDescriptors)
        {
            String descriptorName = descriptor.getName();
            String fieldValue = userEnteredData != null ? userEnteredData.get(descriptorName) : null;
            if (StringUtils.isBlank(fieldValue))
            	fieldValue = descriptor.getValue();
            this.propertysetItem.getItemProperty(descriptorName).setValue(fieldValue);
        }
    }
    
    /**
     * Removes all fields created by this form. Removes listeners that where added on setup/init.
     * Two fields are left untouched (the first ones). These are a label with the caption "Parameters" and
     * the combobox. 
     */
    public void shutdownForm()
    {
    	this.removeValueChangeListener(this.wizardStep);
    	this.removeFocusListener(this.wizardStep);
    	while (this.layout.getComponentCount() > 2)
    	{
    		Component component = this.layout.getComponent(this.layout.getComponentCount()-1);
    		this.layout.removeComponent(component);
    	}
    }

	/**
	 * Gets the data runtime from all fields from this panel.
	 */
	public Map<String, String> getDataMap()
	{
		Map<String, String> runtimeValues = new HashMap<>();
		for (ParamDescriptor descriptor : paramDescriptors)
		{
			Object value = this.propertysetItem.getItemProperty(descriptor.getName()).getValue();
			if (value != null && !StringUtils.isBlank(String.valueOf(value)))
			{
				runtimeValues.put(descriptor.getName(), String.valueOf(value));
			}
		}
		return runtimeValues;
	}

	public boolean isValid()
	{
		for (Field<?> field : this.getFields())
		{
			if (!field.isValid())
				return false;
		}
		return true;
	}

	/**
	 * Adds the value change and focus listener.
	 */
	private void addValueChangeListener(ValueChangeListener listener)
	{
		for (Field<?> field : this.getFields())
		{
			field.addValueChangeListener(listener);
		}
	}

	private void removeValueChangeListener(ValueChangeListener listener)
	{
		for (Field<?> field : this.getFields())
		{
			field.removeValueChangeListener(listener);
		}
	}

	/**
	 * Add focus listener.
	 */
	private void addFocusListener(FocusListener focusListener)
	{
		for (Field<?> field : this.getFields())
		{
			if (field instanceof FocusNotifier)
			{
				FocusNotifier notifier = (FocusNotifier) field;
				notifier.addFocusListener(focusListener);
			}
		}
	}

	private void removeFocusListener(FocusListener focusListener)
	{
		for (Field<?> field : this.getFields())
		{
			if (field instanceof FocusNotifier)
			{
				FocusNotifier notifier = (FocusNotifier) field;
				notifier.removeFocusListener(focusListener);
			}
		}
	}
    
	private List<Field<?>> getFields()
	{
		List<Field<?>> fields = new ArrayList<>();
		for (int i = 2; i < this.layout.getComponentCount(); i++)
		{
			Component c = this.layout.getComponent(i);
			if (c instanceof Field)
				fields.add((Field<?>) c);
		}
		return fields;
	}
}
