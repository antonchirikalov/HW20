package com.toennies.ci1429.app.services.testcases;

import com.toennies.ci1429.app.model.DeviceException;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.ResponseFormat;
import com.toennies.ci1429.app.model.scale.Commands.Command;
import com.toennies.ci1429.app.model.scale.Scale;
import com.toennies.ci1429.app.model.scale.Scale.Unit;

public class ScaleTestCase extends TestCase<Scale> {

	private Command command;
	private ResponseFormat responseFormat;
	private Unit scaleResultUnit;

	public ScaleTestCase(Scale device, Command command, ResponseFormat responseFormat, Unit scaleResultUnit) {
		super(device);
		updateScaleParameter(command, responseFormat, scaleResultUnit);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DeviceResponse _doTest() throws DeviceException {
		return device.process(command, "3", responseFormat, scaleResultUnit);
	}

	public void updateScaleParameter(Command command, ResponseFormat responseFormat, Unit scaleResultUnit) {
		this.command = command;
		this.responseFormat = responseFormat;
		this.scaleResultUnit = scaleResultUnit;
	}

}
