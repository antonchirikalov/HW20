/*
 * WizardValueChangedListener.java
 *
 * Created on Mar 24, 2017
 *
 * Copyright (C) 2015 Toennies, All rights reserved.
 */
package com.toennies.ci1429.app.ui.panels.createdevice;

/**
 * The listener interface for receiving wizardValueChanged events.
 * The class that is interested in processing a wizardValueChanged
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addWizardValueChangedListener</code> method. When
 * the wizardValueChanged event occurs, that object's appropriate
 * method is invoked.
 *
 * @see WizardValueChangedEvent
 */
@FunctionalInterface
public interface WizardValueChangedListener {
	
	/**
	 * Wizard value changed.
	 */
	public void wizardValueChanged(boolean changedValue);
}