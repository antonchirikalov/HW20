package com.toennies.ci1429.app.restcontroller;

import org.springframework.web.bind.annotation.RestController;

import com.toennies.ci1429.app.model.DeviceException;
import com.toennies.ci1429.app.model.IDevice;

/**
 * If an {@link DeviceException} bubbles up to {@link RestController}, this
 * exception is transformed to this class. This is done by
 * {@link DeviceExceptionAdvice}. Without transforming to POJO, ci1515lib would
 * encrypt given exception information.
 */
public class DeviceExceptionModel {

	private final IDevice device;
	private final Throwable originalException;
	private final String errorText; // human readable

	public DeviceExceptionModel(Throwable exception) {
		this.device = null;
		this.originalException = exception;
		this.errorText = null;
	}

	public DeviceExceptionModel(DeviceException exception) {
		this.device = exception.getDevice();
		this.originalException = exception.getOriginalException();
		this.errorText = exception.getErrorText();
	}

	public IDevice getDevice() {
		return device;
	}

	public String getExceptionMessage() {
		if (originalException == null) {
			return null;
		}
		return originalException.getMessage();
	}

	public String getErrorText() {
		return errorText;
	}

}
