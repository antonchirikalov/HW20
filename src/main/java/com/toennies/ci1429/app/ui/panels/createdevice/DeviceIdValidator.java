/*
 * DeviceIdValidator.java
 * 
 * Created on Mar 31, 2017
 * 
 * Copyright (C) 2017 Toennies, All rights reserved.
 */

package com.toennies.ci1429.app.ui.panels.createdevice;


import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.Validator;

/**
 * The Class DeviceIdValidator to validate deviceId whether id already existed and is above 0.
 *
 * @author HiepLV
 */
public class DeviceIdValidator implements Validator {

    private static final long serialVersionUID = 1L;

    private CreateDeviceWizardController controller;

    /**
     * Instantiates a new device id validator.
     */
    public DeviceIdValidator(CreateDeviceWizardController controller) {
        this.controller = controller;
    }

    @Override
    public void validate(Object value) {
        String idStr = (String) value;
        if (controller.wizardData().isUpdateWizard()) {
            // update data, no need to check id
            return;
        }
        if (StringUtils.isEmpty(idStr)) {
            // empty value, auto increase id
            return;
        }
        if (StringUtils.isNumeric(idStr)) {
            int id = Integer.valueOf(idStr).intValue();
            if (id > 0 && !controller.isDeviceIdExist(id)) {
                // id is above 0 and does not exists -> valid
                return;
            }
        }
        throw new InvalidValueException("Invalid ID or ID already existed");
    }

}
