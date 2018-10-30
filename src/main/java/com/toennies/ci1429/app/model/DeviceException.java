package com.toennies.ci1429.app.model;


/**
 * This exception is thrown by inner classes of hw20. For example in
 * {@link IDevice} instances. Also have a look at methods invoking constructor
 * {@link #DeviceException(IDevice, Exception, String)}.
 */
@SuppressWarnings("serial")
public class DeviceException extends Exception {

	private final IDevice device;
	private final Exception originalException;
	private final String errorText; // human readable

	public DeviceException(IDevice device, String errorText) {
		this(device, null, errorText);
	}

	public DeviceException(IDevice device, Exception originalException, String errorText) {
		super(errorText);
		this.device = device;
		this.originalException = originalException;
		this.errorText = errorText;
	}

	public IDevice getDevice() {
		return device;
	}

	public Exception getOriginalException() {
		return originalException;
	}

	public String getException() {
		if (originalException == null) {
			return null;
		}
		return originalException.getClass().getName();
	}

	public String getErrorText() {
		return errorText;
	}

}
