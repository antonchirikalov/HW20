package com.toennies.ci1429.app.ui.panels.createdevice;

import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.WizardStep;

/**
 * Without this helperclass, there is no direct possibility to change the active
 * step. Through changeStep method it's possible to do so.
 *
 */
@SuppressWarnings("serial")
public class ChangeableStepWizard extends Wizard implements WizardValueChangedListener
{

	public ChangeableStepWizard() {
		super();
		this.footer.setMargin(true);
	}

	/**
	 * Call the originally {@link Wizard#activateStep} method.
	 */
	public void changeStep(WizardStep step) {
		activateStep(step);
	}

	@Override
	public void next() {
		super.next();
		updateButtonStates();
	}

	@Override
	public void back() {
		super.back();
		updateButtonStates();
	}

	/**
	 * This method will set state for buttons: Cancel, Back, Next, Finish. <br>
	 * Back button doesn't display on first wizard step. <br>
	 * Finish button only display on last wizard step. <br>
	 * Cancel button doesn't display on last wizard step. <br>
	 * Last step only display Finish button. <br>
	 * If user click Back button, button next will be always enabled even if
	 * current data is invalid.
	 */
	protected void updateButtonStates()
	{
		boolean isLastStep = isLastStep(currentStep);
		boolean isFirstStep = isFirstStep(currentStep);
		// Check whether current step data is valid or not
		boolean isValid = currentStep.onAdvance();
		this.getCancelButton().setVisible(!isLastStep);
		this.getCancelButton().setEnabled(!isLastStep);
		
		this.getBackButton().setVisible(!isLastStep);
		this.getBackButton().setEnabled(!isFirstStep && !isLastStep);

		this.getNextButton().setVisible(!isLastStep);
		this.getNextButton().setEnabled(!isLastStep && isValid);

		this.getFinishButton().setEnabled(isLastStep);
		this.getFinishButton().setVisible(isLastStep);
	}

	@Override
	public void wizardValueChanged(boolean changedValue)
	{
		this.updateButtonStates();
	}

}
