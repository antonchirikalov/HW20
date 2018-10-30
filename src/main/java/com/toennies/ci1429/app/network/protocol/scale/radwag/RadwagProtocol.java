/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.radwag;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.scale.Commands.Command;
import com.toennies.ci1429.app.network.connector.ADataTransformer;
import com.toennies.ci1429.app.network.connector.ConfigurableDataTransformer;
import com.toennies.ci1429.app.network.connector.FlexibleExceptionConnector;
import com.toennies.ci1429.app.network.connector.FlexibleReConnector;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.connector.LoggingConnector;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.protocol.AtProtocol;
import com.toennies.ci1429.app.network.protocol.scale.AScaleProtocol;
import com.toennies.ci1429.app.network.protocol.scale.AtSupportedCommands;
import com.toennies.ci1429.app.network.protocol.scale.AtSupportedCommands.AtCommand;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse.Status;
import com.toennies.ci1429.app.network.protocol.scale.IHardwareRequest;
import com.toennies.ci1429.app.network.protocol.scale.RequestHandler;
import com.toennies.ci1429.app.network.socket.ISocket;

/**
 * Radwag scale protocol. Initializes the network stack. Defines needed
 * parameters.
 * 
 * @author renkenh
 */
@AtProtocol(value = "Radwag Scale Protocol", deviceType = DeviceType.SCALE)
@AtDefaultParameters(value = {
		@Parameter(name = ADataTransformer.PARAM_FRAME_END, value = "[CR][LF]", toolTip = "Specifies the control characters defining the end of a scanned value. ASCII-control characters must be written in [], e.g. [SOH] or [CR]"),
		@Parameter(name = ADataTransformer.PARAM_FRAME_SEP, value = "|", toolTip = "Specifies the control characters separating header and body. ASCII-control characters must be written in [], e.g. [SOH] or [CR]"),
		@Parameter(name = RadwagProtocol.PARAM_RETRIES, value = "2", toolTip = "Specifies the number of retries when radwag unexpectedly responds with a command does not exist error"),
		@Parameter(name = "databits", value = "8"), @Parameter(name = "stopbits", value = "2"),
		@Parameter(name = "parity", value = "NONE") })
@AtSupportedCommands(value = { @AtCommand(value = Command.CLEAR_TARE), @AtCommand(value = Command.TARE),
		@AtCommand(value = Command.TARE_WITH_VALUE), @AtCommand(value = Command.WEIGH),
		@AtCommand(value = Command.WEIGH_DIRECT), @AtCommand(value = Command.ZERO) })
public class RadwagProtocol extends AScaleProtocol
{

	public static final String PARAM_RETRIES = "retries";


	private static final SimpleRequest TARE_REQUEST = new SimpleRequest("T");
	private static final SimpleRequest ZERO_REQUEST = new SimpleRequest("Z");
	// Radwag scale does not support clearing of tare. That's why a tare with
	// value request is sent with value 0 kg
	private static final SimpleRequest CLEAR_TARE_REQUEST = new SimpleRequest("UT 0 kg");
	private static final WeighDataRequest WEIGH_DIRECT_REQUEST = new WeighDataRequest("SS");
	private static final WeighDataRequest WEIGH_REQUEST = new WeighDataRequest("S");


	@Override
	protected Object _send(Object... params) throws IOException, TimeoutException
	{
		int count = 0;
		HardwareResponse resp = null;
		// Radwag scale sometimes returns an error on a valid command. That is
		// why the Protocol should retry the request.
		int retries = this.config().getIntEntry(PARAM_RETRIES);
		do
		{
			this.pipeline().push(this.transform(params));
			resp = this.pipeline().pop();
			count++;
		}
		while (Status.ERROR == resp.getStatus() && count <= retries);
		return resp;
	}

	@Override
	protected IHardwareRequest transform(Object... params)
	{
		Command cmd = (Command) params[0];
		logger.debug("Got Command {}", cmd);
		switch (cmd)
		{
			case TARE:
				return TARE_REQUEST;
			case CLEAR_TARE:
				return CLEAR_TARE_REQUEST;
			case TARE_WITH_VALUE:
				float value = Float.valueOf(String.valueOf(params[1]));
				StringBuilder sb = new StringBuilder();
				sb.append("UT ");
				sb.append(value);
				sb.append(" kg");
				return new SimpleRequest(sb.toString());
			case ZERO:
				return ZERO_REQUEST;
			case WEIGH_DIRECT:
				return WEIGH_DIRECT_REQUEST;
			case WEIGH:
				return WEIGH_REQUEST;
			default:
				return null;
		}
	}

	@Override
	protected IFlexibleConnector<IHardwareRequest, HardwareResponse> createPipeline(ISocket socket)
	{
		LoggingConnector<byte[]> logging = new LoggingConnector<>(socket);
		ADataTransformer envelope = new ConfigurableDataTransformer(logging);
		RadwagConnector connector = new RadwagConnector(envelope);
		RequestHandler requestHandler = new RequestHandler(connector);
		FlexibleExceptionConnector<IHardwareRequest, HardwareResponse> exception = new FlexibleExceptionConnector<>(requestHandler);
		FlexibleReConnector<IHardwareRequest, HardwareResponse> reconnector = new FlexibleReConnector<>(exception);
		return reconnector;
	}

}
