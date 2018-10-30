/*
 * FieldValidator.java
 * 
 * Created on Mar 30, 2017
 * 
 * Copyright (C) 2017 Toennies, All rights reserved.
 */

package com.toennies.ci1429.app.ui.panels.createdevice;


import com.toennies.ci1429.app.network.parameter.ParamDescriptor;
import com.toennies.ci1429.app.network.parameter.typeinformation.ValidationResult;
import com.vaadin.data.Validator;

/**
 * The Class DescriptorValidator. Validator based on ParamDescriptor.
 *
 * @author LuanVT1
 */
public class DescriptorValidator implements Validator {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The descriptor. */
    private ParamDescriptor descriptor;

    /**
     * Instantiates a new descriptor validator.
     *
     * @param descriptor the descriptor
     */
    public DescriptorValidator(ParamDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public void validate(Object realValue) throws InvalidValueException {
        String valueStr = realValue == null ? null : String.valueOf(realValue);
        ValidationResult validate = descriptor.validate(valueStr);
        if (validate.isError()) {
            throw new InvalidValueException(validate.getErrorText());
        }
    }
}
