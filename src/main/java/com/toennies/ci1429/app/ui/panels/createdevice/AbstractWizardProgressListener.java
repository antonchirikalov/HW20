package com.toennies.ci1429.app.ui.panels.createdevice;

import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

/**
 * Instantiate a object of this class, if you are interested in
 * {@link WizardProgressListener} events.
 * 
 * Methods are already implemented. So you only need to override methods in
 * subclasses that are explicitly needed.
 */
public abstract class AbstractWizardProgressListener implements WizardProgressListener {

	@Override
	public void activeStepChanged(WizardStepActivationEvent event) {
		// Override this method, if you want to get notified about this event
	}

	@Override
	public void stepSetChanged(WizardStepSetChangedEvent event) {
		// Override this method, if you want to get notified about this event
	}

	@Override
	public void wizardCompleted(WizardCompletedEvent event) {
		// Override this method, if you want to get notified about this event
	}

	@Override
	public void wizardCancelled(WizardCancelledEvent event) {
		// Override this method, if you want to get notified about this event
	}
}
