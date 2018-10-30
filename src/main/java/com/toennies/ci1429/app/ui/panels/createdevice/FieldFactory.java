/*
 * FieldFactory.java
 * 
 * Created on Mar 30, 2017
 * 
 * Copyright (C) 2017 Toennies, All rights reserved.
 */

package com.toennies.ci1429.app.ui.panels.createdevice;


import java.util.List;

import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.network.parameter.ParamDescriptor;
import com.toennies.ci1429.app.network.parameter.Parameters;
import com.toennies.ci1429.app.network.parameter.typeinformation.IListTypeInformationValidator;
import com.toennies.ci1429.app.network.parameter.typeinformation.ITypeInformationValidator;
import com.toennies.ci1429.app.network.parameter.typeinformation.ITypeInformationValidator.InputType;
import com.toennies.ci1429.app.network.parameter.typeinformation.ProtocolNameValidator;
import com.toennies.ci1429.app.network.protocol.AProtocol;
import com.toennies.ci1429.app.network.protocol.AtProtocol;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.network.socket.AtSocket;
import com.toennies.ci1501.lib.components.GenericComboBox;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

/**
 * A factory for creating Vaadin Field objects by providing ParamDescriptor.
 *
 * @author HiepLV
 */
public class FieldFactory
{

	/**
	 * Creates a new label that follows a specific color code.
	 * The given caption is used a the labels value.
	 * @param caption The caption to show on the label.
	 * @return The label.
	 */
	public static final Label createFormHeading(String caption)
	{
		Label formHeading = new Label(caption);
		formHeading.setStyleName(ValoTheme.LABEL_H3);
		formHeading.addStyleName(ValoTheme.LABEL_COLORED);
		return formHeading;
	}
	
	/**
	 * @return A combobox that shows all available sockets. The sockets are found using the annotation {@link AtSocket}.
	 */
    public static AbstractComponent getSocketComboBox()
    {
    	ParamDescriptor descriptor = Parameters.getParameter(IProtocol.PARAM_SOCKET, AProtocol.class.getName());
    	return getFieldByParamDescriptor(descriptor);
    }

	/**
	 * @return A combobox that shows all available protocols for a specific device type. The protocols are found
	 * using the annotation {@link AtProtocol}.
	 */
    public static AbstractComponent getProtocolComboBox(DeviceType deviceType)
    {
    	String typeInfo = ProtocolNameValidator.VALIDATOR_NAME+ITypeInformationValidator.VALIDATOR_DELIMITER+(deviceType != null ? deviceType.name() : "");
    	ParamDescriptor descriptor = new ParamDescriptor("protocol", "", true, typeInfo, "Select the high-level protocol that should be used to connect to the device.");
    	return getFieldByParamDescriptor(descriptor);
    }

    /**
     * Gets Vaadin field by providing a ParamDescriptor.
     *
     * @param descriptor the descriptor
     * @return the field by param descriptor
     */
    public static AbstractComponent getFieldByParamDescriptor(ParamDescriptor descriptor) {
        ITypeInformationValidator validator = descriptor.getValidator();
        // Use text field in case no validator for this param
        if(validator == null){
            return createTextField(descriptor);
        }
        InputType inputType = validator.getInputType();
        switch (inputType) {
            case INT:
            case FLOAT:
            case TEXT:
                return createTextField(descriptor);
            case BOOLEAN:
                return createCheckBox(descriptor);
            case LIST:
                return createComboBox(descriptor);
            default:
                break;
        }
        return null;
    }

    /**
     * Creates a new Field object.
     *
     * @param descriptor the descriptor
     * @return the abstract field<?>
     */
    private static AbstractField<?> createComboBox(ParamDescriptor descriptor) {
        GenericComboBox<String> cbb = new GenericComboBox<>(descriptor.getName());
        IListTypeInformationValidator validator = (IListTypeInformationValidator) descriptor.getValidator();
        List<String> validValues = validator.getValidValues();
        cbb.addItems(validValues);
        cbb.setData(descriptor);
        cbb.setValue(descriptor.getValue());
        cbb.addValidator(new DescriptorValidator(descriptor));
        cbb.setRequired(descriptor.isRequired());
        cbb.setRequiredError("Missing required value");
        cbb.setImmediate(true);
        return cbb;
    }

    /**
     * Creates a new Field object.
     *
     * @param descriptor the descriptor
     * @return the abstract field<?>
     */
    private static AbstractComponent createCheckBox(ParamDescriptor descriptor)
    {
    	HorizontalLayout wrap = new HorizontalLayout();
    	wrap.setSpacing(true);
    	wrap.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
    	wrap.setCaption(descriptor.getName());
    	wrap.setData(descriptor);
 
        CheckBox checkBox = new CheckBox();
        checkBox.setValue(Boolean.valueOf(descriptor.getValue()));
        checkBox.setRequiredError("Missing required value");
        checkBox.setImmediate(true);
        wrap.addComponent(checkBox);
        return wrap;
    }

    /**
     * Creates a new Field object.
     *
     * @param descriptor the descriptor
     * @return the abstract field<?>
     */
    private static AbstractField<?> createTextField(ParamDescriptor descriptor) {
        TextField textField = new TextField(descriptor.getName());
        textField.setData(descriptor);
        textField.setValue(descriptor.getValue());
        textField.setRequired(descriptor.isRequired());
        textField.setRequiredError("Missing required value");
        textField.addValidator(new DescriptorValidator(descriptor));
        textField.setNullRepresentation("");
        textField.setImmediate(true);
        return textField;
    }

    private FieldFactory()
    {
        //no instance
    }

}
