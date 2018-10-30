/*
 * WrapperValidators.java
 * 
 * Created on Mar 27, 2017
 * 
 * Copyright (C) 2017 Toennies, All rights reserved.
 */

package com.toennies.ci1429.app.ui.panels.createdevice;


import java.util.Map;

import com.toennies.ci1429.app.network.parameter.Parameters;
import com.toennies.ci1429.app.network.parameter.typeinformation.ValidationResult;
import com.toennies.ci1429.app.network.parameter.typeinformation.ValidationResults;
import com.toennies.ci1429.app.network.parameter.typeinformation.Validators;
import com.toennies.ci1429.app.ui.components.ExtendedMapGrid;
import com.vaadin.data.Item;
import com.vaadin.data.Validator;

/**
 * The Class is used to validate  runtime value from Grid of parameters base on default Validators. 
 *
 * @author LuanVT1
 */
public class WrapperValidators implements Validator {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The protocol classname. */
    private String protocolClassname;

    /** The parameter grid. */
    private ExtendedMapGrid parameterGrid;

    private String errorText = null;

    /**
     * Instantiates a new wrapper validators.
     *
     * @param parameterGrid the parameter grid
     * @param protocolClassname the protocol classname
     */
    public WrapperValidators(ExtendedMapGrid parameterGrid, String protocolClassname) {
        this.protocolClassname = protocolClassname;
        this.parameterGrid = parameterGrid;

    }

    @Override
    public void validate(Object value) throws InvalidValueException {
        // do validate for socket parameters.
        errorText = null;
        Object editedItemId = parameterGrid.getEditedItemId();
        // get selected item row
        Item item = parameterGrid.getContainerDataSource().getItem(editedItemId);
        // get value of item property.
        String nameItem = item.getItemProperty("Name").getValue().toString();
        // build a map to set runtime value to corresponding edited item.
        Map<String, String> runtimeValue = parameterGrid.getDataMap();
        if (!runtimeValue.containsKey(nameItem))
            return;
        for (java.util.Map.Entry<String, String> entry : runtimeValue.entrySet()) {
            if(entry.getKey().equals(nameItem)){
                entry.setValue((String)value);
                break;
            }
        }
        Parameters.getParameters(protocolClassname);
        ValidationResults validateResults = Validators.validate(runtimeValue, protocolClassname);

        if (validateResults.hasErrors()) {
            for (ValidationResult error : validateResults.getErrors().values()) {
                if (null != error.getErrorText()) {
                    errorText = error.getErrorText();
                }
            }
        }

        if (null != errorText) {
            throw new InvalidValueException(String.valueOf(errorText));
        }
    }

    /**
     * @return the errorText
     */
    public String getErrorText() {
        return this.errorText;
    }

    /**
     * @param errorText the errorText to set
     */
    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

}
