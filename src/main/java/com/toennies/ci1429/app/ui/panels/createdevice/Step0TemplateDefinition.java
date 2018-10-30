package com.toennies.ci1429.app.ui.panels.createdevice;

import com.toennies.ci1429.app.services.devicetemplates.ITemplateDeviceDescription;
import com.toennies.ci1429.app.services.devicetemplates.TemplateDeviceDescription;
import com.toennies.ci1429.app.util.DeviceTemplateUtil;
import com.toennies.ci1501.lib.components.GenericComboBox;
import com.vaadin.ui.FormLayout;

public class Step0TemplateDefinition extends AbstractWizardStep
{

	private FormLayout formLayout;
	private GenericComboBox<String> templateComboBox;
	
	public Step0TemplateDefinition(CreateDeviceWizardController controller)
	{
		super(controller);
		this.initUI();
	}

	@Override
	public String getCaption() {
		return "Select a template";
	}

	@Override
    protected void setupForm(FormLayout form)
    {
		// Layout every component is added to
		this.formLayout = form;
		this.templateComboBox = returnNewTemplateComboBox();
		templateComboBox.addValueChangeListener(this);
		this.formLayout.addComponent(templateComboBox);
		this.formLayout.setMargin(true);
	}

	@Override
	protected void updateWizardData() {
		this.controller.wizardData().setStep0Data(getTemplateByName(templateComboBox.getGenericValue()));
	}

	private GenericComboBox<String> returnNewTemplateComboBox() {
		GenericComboBox<String> cb = new GenericComboBox<>("Templates",
				DeviceTemplateUtil.returnStringList(this.controller.getTemplates()));
		cb.setInputPrompt("May be empty.");
		return cb;
	}

	@Override
	protected void updateUI() {
		if (templateComboBox != null) {
			formLayout.removeComponent(templateComboBox);
		}
		templateComboBox = returnNewTemplateComboBox();
		formLayout.addComponent(templateComboBox);
	}

	@Override
	protected boolean isValid() {
		// always valid, because selected template may be empty
		return true;
	}

	/**
	 * {@link #templateComboBox} displays template names (=String). This method
	 * does the transformation backwards from template name to
	 * {@link TemplateDeviceDescription} object
	 */
	private ITemplateDeviceDescription getTemplateByName(String templateName) {
		for (ITemplateDeviceDescription tdd : this.controller.getTemplates()) {
			if (tdd.getTemplateName().equalsIgnoreCase(templateName)) {
				return tdd;
			}
		}
		return null;
	}

}
