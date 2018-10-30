package com.toennies.ci1429.app.network.protocol.scale.hofelmeyer;

import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.scale.Commands;
import com.toennies.ci1429.app.network.connector.*;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.protocol.AtProtocol;
import com.toennies.ci1429.app.network.protocol.scale.*;
import com.toennies.ci1429.app.network.protocol.scale.AtSupportedCommands.AtCommand;
import com.toennies.ci1429.app.network.protocol.scale.hofelmeyer.requests.RequestFactory;
import com.toennies.ci1429.app.network.socket.ISocket;

@AtProtocol(value = "Hofelmeyer Scale Protocol", deviceType = DeviceType.SCALE)
@AtSupportedCommands(value = {@AtCommand(value = Commands.Command.CLEAR_TARE), @AtCommand(value = Commands.Command.TARE),
		@AtCommand(value = Commands.Command.TARE_WITH_VALUE), @AtCommand(value = Commands.Command.WEIGH),
		@AtCommand(value = Commands.Command.WEIGH_AUTOMATIC), @AtCommand(value = Commands.Command.ZERO)})
@AtDefaultParameters(value = {
		@Parameter(name = ADataTransformer.PARAM_FRAME_START, value = "<", toolTip = "Specifies the control characters defining the start of the header of a message. ASCII-control characters must be written in [], e.g. [SOH] or [CR]"),
		@Parameter(name = ADataTransformer.PARAM_FRAME_END, value = ">", toolTip = "Specifies the control characters defining the end of a scanned value. ASCII-control characters must be written in [], e.g. [SOH] or [CR]")})
public class HofelmeyerProtocol extends AScaleProtocol
{
	@Override
	protected IHardwareRequest transform(Object... params)
	{
		RequestFactory requestFactory = new RequestFactory();
		return requestFactory.getRequest(params);
	}

	@Override
	protected IFlexibleConnector<IHardwareRequest, HardwareResponse> createPipeline(ISocket socket)
	{
		LoggingConnector<byte[]> logging = new LoggingConnector<>(socket);
		HofelmeyerConfigurableDataTransformer transformer = new HofelmeyerConfigurableDataTransformer(logging);
		HofelmeyerConnector connector = new HofelmeyerConnector(transformer);
		RequestHandler requestHandler = new RequestHandler(connector);
		FlexibleExceptionConnector<IHardwareRequest, HardwareResponse> exception = new FlexibleExceptionConnector<>(requestHandler);
		FlexibleReConnector<IHardwareRequest, HardwareResponse> reconnector = new FlexibleReConnector<>(exception);
		return reconnector;
	}
}
