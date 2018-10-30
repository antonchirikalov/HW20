
package com.toennies.ci1429.app.ui.panels.createdevice;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.parameter.ParamDescriptor;
import com.toennies.ci1429.app.network.parameter.Parameters;
import com.toennies.ci1501.lib.utils.ContainerUtils;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@AtDefaultParameters(value = {
        @Parameter(name = "Device ID",isRequired = false, value = "", typeInformation = "text", toolTip="Optional, specify the id under which the new device can be found later. Value must be an integer and greater than zero."),
        @Parameter(name = "Device type", isRequired = true, value = "", typeInformation = "enum:com.toennies.ci1429.app.model.DeviceType", toolTip="Select a device type."),
        @Parameter(name = "Manufacturer", isRequired = true, value = "", typeInformation = "text", toolTip="Specifiy a speaking vendor name. This value can be used to identify the device later on."),
        @Parameter(name = "Model", isRequired = true, value = "", typeInformation = "text", toolTip="Specifiy a speaking model name. This value can be used to identify the device later on.")
})
public class Step1DeviceType extends AbstractWizardStep {

    // may be null
    private TextField deviceID;
    private ComboBox deviceType;
    private TextField manufacturer;
    private TextField model;
    
    private PropertysetItem propertysetItem;
    private FieldGroup binder;


    public Step1DeviceType(CreateDeviceWizardController controller) {
        super(controller);
		this.initUI();
    }

    @Override
    public String getCaption() {
        return "Select device type";
    }

    @Override
    protected void setupForm(FormLayout formLayout)
    {
        // Layout every component is added to
        formLayout.setMargin(true);
        formLayout.setSpacing(true);
        formLayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        // Data for device ComboBox
        BeanItemContainer<DeviceType> itemContainer =
                ContainerUtils.returnBeanItemContainer(DeviceType.class, new ArrayList<DeviceType>(Arrays.asList(DeviceType.values())));
        formLayout.setMargin(true);
        Map<String, ParamDescriptor> parameterDescriptors = Parameters.getParameters(Step1DeviceType.class);
        
        deviceID = (TextField) FieldFactory.getFieldByParamDescriptor(parameterDescriptors.get("Device ID"));
        deviceID.addValidator(new DeviceIdValidator(this.controller));
        deviceID.setNullRepresentation("");
        deviceID.setInputPrompt("May be empty");
        deviceID.setSizeFull();
        deviceID.addValueChangeListener(this);
        deviceID.addFocusListener(this);
        
        deviceType = (ComboBox) FieldFactory.getFieldByParamDescriptor(parameterDescriptors.get("Device type"));
        deviceType.setContainerDataSource(itemContainer);
        deviceType.setRequired(true);
        deviceType.setRequiredError("Missing required value");
        deviceType.setSizeFull();
        deviceType.addValueChangeListener(this);
        deviceType.addFocusListener(this);
        
        manufacturer = (TextField) FieldFactory.getFieldByParamDescriptor(parameterDescriptors.get("Manufacturer"));
        manufacturer.setRequired(true);
        manufacturer.setRequiredError("Missing required value");
        manufacturer.setSizeFull();
        manufacturer.addValueChangeListener(this);
        manufacturer.addFocusListener(this);
        
        model = (TextField) FieldFactory.getFieldByParamDescriptor(parameterDescriptors.get("Model"));
        // creating item properties
        propertysetItem = new PropertysetItem();
        propertysetItem.addItemProperty("deviceID", new ObjectProperty<String>(null, String.class));
        propertysetItem.addItemProperty("deviceType", new ObjectProperty<Enum>(null, Enum.class));
        propertysetItem.addItemProperty("manufacturer", new ObjectProperty<String>("", String.class));
        propertysetItem.addItemProperty("model", new ObjectProperty<String>("", String.class));

        // binding fields to items.
        binder = new FieldGroup(propertysetItem);
        binder.bind(deviceID, "deviceID");
        binder.bind(deviceType, "deviceType");
        binder.bind(manufacturer, "manufacturer");
        binder.bind(model, "model");
        model.setRequired(true);
        model.setRequiredError("Missing required value");
        model.setSizeFull();
        model.addValueChangeListener(this);
        model.addFocusListener(this);

        formLayout.addComponent(deviceID);
        formLayout.addComponent(deviceType);
        formLayout.addComponent(manufacturer);
        formLayout.addComponent(model);
        updateUI();
        
        if (this.controller.wizardData().isUpdateWizard()) {
            deviceID.setEnabled(false);
            deviceType.setEnabled(false);
        }

//        this.addInputChangeListener(deviceID, deviceType, manufacturer, model);
    }


    @Override
    protected void updateUI() {
        CreateDeviceWizardData wizardData = this.controller.wizardData();
        deviceID.setValue(wizardData.getDeviceID() == null ? null : wizardData.getDeviceID().toString());
        deviceType.setValue(wizardData.getDeviceType());
        manufacturer.setValue(wizardData.getManufacturer() == null ? "" : wizardData.getManufacturer());
        model.setValue(wizardData.getModel() == null ? "" : wizardData.getModel());
    }

    @Override
    protected void updateWizardData() {
        this.controller.wizardData().setStep1Data(getDeviceID(), getDeviceType(), manufacturer.getValue(), model.getValue());
    }

    @Override
    protected boolean isValid() {
        return binder.isValid();
    }

    private DeviceType getDeviceType() {
        if (deviceType.getValue() != null && deviceType.getValue() instanceof DeviceType) {
            return (DeviceType) deviceType.getValue();
        }
        return null;
    }

    private Integer getDeviceID() {
        if (StringUtils.isNumeric(deviceID.getValue())) {
            return Integer.valueOf(deviceID.getValue());
        }
        return null;
    }

}
