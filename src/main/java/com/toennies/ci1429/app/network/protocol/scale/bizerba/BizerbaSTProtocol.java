/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.bizerba;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.scale.Commands.Command;
import com.toennies.ci1429.app.network.connector.ADataTransformer;
import com.toennies.ci1429.app.network.connector.DataTransformer;
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
import com.toennies.ci1429.app.network.protocol.scale.IHardwareRequest;
import com.toennies.ci1429.app.network.protocol.scale.RequestHandler;
import com.toennies.ci1429.app.network.socket.ISocket;

/**
 * Bizerba ST scale protocol.
 * Initializes the network stack.
 * Defines needed parameters.
 * @author renkenh
 */
@AtProtocol(value = "Bizerba ST Scale Protocol", deviceType = DeviceType.SCALE)
@AtDefaultParameters(value=
{
	@Parameter(name=BizerbaSTProtocol.PARAM_SCALE_NUMBER, value="0", typeInformation="int:0..", toolTip="The number of the scale within the unit."),
	@Parameter(name=BizerbaSTProtocol.PARAM_UNIT_NUMBER, value="1", typeInformation="int:0..", toolTip="The number of the unit in which the scale is located."),
	@Parameter(name=BizerbaSTProtocol.PARAM_SEND_ENQ, value="true", typeInformation="boolean", toolTip="Whether [ENQ] should be send before sending any data."),
	@Parameter(name=ADataTransformer.PARAM_FRAME_START, value="[SOH]", toolTip="Specifies the control characters defining the start of the header of a message. ASCII-control characters must be written in [], e.g. [SOH] or [CR]"),
	@Parameter(name=ADataTransformer.PARAM_FRAME_END, value="[ETB]", toolTip="Specifies the control characters defining the end of a scanned value. ASCII-control characters must be written in [], e.g. [SOH] or [CR]"),
	@Parameter(name=ADataTransformer.PARAM_FRAME_SEP, value="[ETX]", toolTip="Specifies the control characters separating header and body. ASCII-control characters must be written in [], e.g. [SOH] or [CR]"),
//	@Parameter(name=FlexibleTimedHealthCheckTransformer.PARAM_HEALTHCHECK, value="5000", typeInformation="int:0..", toolTip="The period of the health check. In milliseconds.")
//	@Parameter(name="databits", value="7"),
//	@Parameter(name="stopbits", value="1"),
//	@Parameter(name="parity", value="EVEN")
})
@AtSupportedCommands(value=
{
	@AtCommand(value=Command.CLEAR_TARE),
	@AtCommand(value=Command.ITEM_ADDING),
	@AtCommand(value=Command.ITEM_NOT_ADDING),
	@AtCommand(value=Command.RESET),
	@AtCommand(value=Command.RESTART),
	@AtCommand(value=Command.TARE),
	@AtCommand(value=Command.TARE_WITH_VALUE),
	@AtCommand(value=Command.WEIGH),
	@AtCommand(value=Command.WEIGH_AUTOMATIC),
	@AtCommand(value=Command.WEIGH_DIRECT),
	@AtCommand(value=Command.ZERO)
})
public class BizerbaSTProtocol extends AScaleProtocol
{

	/** Whether the scale hardware expects that an ENQ is send or not. */
	public static final String PARAM_SEND_ENQ = "sendENQ";
	/** The number of the scale. */
	public static final String PARAM_SCALE_NUMBER = "scalenr";
	/** The unit (or facility) number. */
	public static final String PARAM_UNIT_NUMBER  = "unitnr";

	
	private static final DecimalFormat TARE_VALUE_FORMATTER = new DecimalFormat("00000.00");
	static
	{
		DecimalFormatSymbols sym = new DecimalFormatSymbols();
		sym.setDecimalSeparator(',');
		TARE_VALUE_FORMATTER.setDecimalFormatSymbols(sym);
	}

	private static final SimpleRequest  CLEAR_TARE_REQUEST = new SimpleRequest("q#");
	private static final SimpleRequest  RESET_REQUEST = new SimpleRequest("qK");
	private static final SimpleRequest  RESTART_REQUEST = new SimpleRequest("q ");
	private static final SimpleRequest  TARE_REQUEST = new SimpleRequest("q\"");
	private static final SimpleRequest  ZERO_REQUEST = new SimpleRequest("q!");
	private static final WeightDataRequest ITEM_ADDING_REQUEST = new WeightDataRequest("qY");
	private static final WeightDataRequest ITEM_NOT_ADDING_REQUEST = new WeightDataRequest("qZ");
	private static final WeightDataRequest WEIGH_AUTOMATIC_REQUEST = new WeightDataRequest((byte[][]) null);
	private static final WeightDataRequest WEIGH_DIRECT_REQUEST = new WeightDataRequest("q%");
	private static final WeightDataRequest WEIGH_REQUEST = new WeightDataRequest("q$");



	@Override
	protected IFlexibleConnector<IHardwareRequest, HardwareResponse> createPipeline(ISocket socket)
	{
		LoggingConnector<byte[]> logging = new LoggingConnector<>(socket);
		BizerbaMSGTransformer transformer = new BizerbaMSGTransformer();
		DataTransformer envelope = new DataTransformer(transformer, logging);
		BizerbaSTConnector connector = new BizerbaSTConnector(envelope);
		HandshakePerformer handshake = new HandshakePerformer(transformer, connector);
		RequestHandler requestHandler = new RequestHandler(handshake);
		FlexibleExceptionConnector<IHardwareRequest, HardwareResponse> exception = new FlexibleExceptionConnector<>(requestHandler);
		FlexibleReConnector<IHardwareRequest, HardwareResponse> reconnector = new FlexibleReConnector<>(exception);
		return reconnector;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IHardwareRequest transform(Object... params)
	{
		Command cmd = (Command) params[0];
		logger.debug("Got Command {}", cmd);
		switch (cmd)
		{
			case CLEAR_TARE:
				return CLEAR_TARE_REQUEST;
			case ITEM_ADDING:
				return ITEM_ADDING_REQUEST;
			case ITEM_NOT_ADDING:
				return ITEM_NOT_ADDING_REQUEST;
			case RESET:
				return RESET_REQUEST;
			case RESTART:
				return RESTART_REQUEST;
			case TARE:
				return TARE_REQUEST;
			case TARE_WITH_VALUE:
				float value = Float.valueOf(String.valueOf(params[1]));
				StringBuilder sb = new StringBuilder(12);
				sb.append("qS");
				sb.append(TARE_VALUE_FORMATTER.format(value));
				sb.append("kg");
				return new SimpleRequest(sb.toString());
			case WEIGH_AUTOMATIC:
				return WEIGH_AUTOMATIC_REQUEST;
			case WEIGH_DIRECT:
				return WEIGH_DIRECT_REQUEST;
			case WEIGH:
				return WEIGH_REQUEST;
			case ZERO:
				return ZERO_REQUEST;
			default:
				return null;
		}
	}

}
