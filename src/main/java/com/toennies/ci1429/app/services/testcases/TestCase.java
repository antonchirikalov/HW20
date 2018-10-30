package com.toennies.ci1429.app.services.testcases;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.model.DeviceException;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.IDevice.DeviceState;

/**
 * Base class for simple test cases for devices. Methods are protected. So
 * override them if you want to.
 * 
 * See also RFC0712-29 and respective sub classes.
 * 
 */
public abstract class TestCase<D extends IDevice> {

	private final static Logger logger = LogManager.getLogger();

	/**
	 * For this device test case will be executed.
	 */
	protected final D device;

	public TestCase(D device) {
		this.device = device;
	}

	/**
	 * Public method that starts test case. Invokes inner methods
	 * {@link #doTest()}.
	 */
	public DeviceResponse testDevice() {
		logStartOfTest();
		if (!checkDeviceState()) {
			logger.warn("Device is not ready for test.");
			return null;
		}
		DeviceResponse testResult = doTest();
		logEndOfTest();
		return testResult;
	}

	/**
	 * Logs start of test case.
	 */
	private void logStartOfTest() {
		logger.info("Start test of ".concat(buildDeviceInformation()));
	}

	/**
	 * Is device ready for test case? If not, invoking test case doesn't make
	 * sense.
	 */
	protected boolean checkDeviceState() {
		return device.getDeviceState() == DeviceState.CONNECTED;
	}

	/**
	 * Invokes device specific test case method. This device specific test case
	 * method may throw an exception. These possible exceptions are handled
	 * here.
	 */
	private DeviceResponse doTest() {
		try {
			return _doTest();
		} catch (DeviceException e) {
			logger.error("Error during running test", e);
			return null;
		}
	}

	/**
	 * Performs respective test for {@link #device}. Exception handling is done
	 * by invoking method {@link #doTest()}.
	 */
	protected abstract DeviceResponse _doTest() throws DeviceException;

	/**
	 * Logs end of test case.
	 */
	private void logEndOfTest() {
		logger.info("End test of ".concat(buildDeviceInformation()));
	}

	private String buildDeviceInformation() {
		StringBuilder sb = new StringBuilder();
		sb.append("[deviceModel:");
		sb.append(device.getDeviceModel());
		sb.append(";deviceId:");
		sb.append(device.getDeviceID());
		sb.append("]");
		return sb.toString();
	}

}
