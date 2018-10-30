/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.mettler;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Map;

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
 * Protocol for Mettler IND690 scales.
 * Creates the network stack and specifies some mettler specific parameters.
 * @author renkenh
 */
@AtProtocol(value = "Mettler IND690-Base Scale Protocol", deviceType = DeviceType.SCALE)
@AtDefaultParameters(value =
{
	@Parameter(name=ADataTransformer.PARAM_FRAME_END, value="[CR][LF]", toolTip="Specifies the control characters defining the end of a scanned value. ASCII-control characters must be written in [], e.g. [SOH] or [CR]"),
	@Parameter(name=MettlerINDProtocol.PARAM_AB_BRUTTO, value="A011", toolTip="Specifies the identifier of the brutto block in the response. Must match the scale settings."),
	@Parameter(name=MettlerINDProtocol.PARAM_AB_NETTO, value="A012", toolTip="Specifies the identifier of the netto block in the response. Must match the scale settings."),
	@Parameter(name=MettlerINDProtocol.PARAM_AB_TARA, value="A013", toolTip="Specifies the identifier of the tara block in the response. Must match the scale settings."),
	@Parameter(name=MettlerINDProtocol.PARAM_AB_WEIGHT, value="A098", toolTip="Specifies the identifier for a whole weight block (which includes brutto, netto, time, counter, ...). Must match the scale settings.")
})
@AtSupportedCommands(value=
{
	@AtCommand(value=Command.WEIGH),
	@AtCommand(value=Command.WEIGH_DIRECT),
	@AtCommand(value=Command.ZERO),
	@AtCommand(value=Command.CLEAR_TARE),
	@AtCommand(value=Command.TARE),
	@AtCommand(value=Command.TARE_WITH_VALUE)
})
public class MettlerINDProtocol extends AScaleProtocol
{

	/** The number used to identify the brutto block in a weight record. */
	public static final String PARAM_AB_BRUTTO = "#AB Brutto";
	/** The number used to identify the netto block in a weight record. */
	public static final String PARAM_AB_NETTO = "#AB Netto";
	/** The number used to identify the tara block in a weight record. */
	public static final String PARAM_AB_TARA = "#AB Tara";
	/** The number used to identify the info block in a weight record. */
	public static final String PARAM_AB_WEIGHT = "#AB Weighinfo";


	private static final SimpleRequest ZERO_REQUEST = new SimpleRequest("Z");
	private static final SimpleRequest CLEAR_TARE_REQUEST = new SimpleRequest("T ");
	private static final SimpleRequest TARE_REQUEST = new SimpleRequest("T");

	private static final DecimalFormat TARE_VALUE_FORMATTER = new DecimalFormat("0000000.00");
	static
	{
		DecimalFormatSymbols sym = new DecimalFormatSymbols();
		sym.setDecimalSeparator('.');
		TARE_VALUE_FORMATTER.setDecimalFormatSymbols(sym);
	}


	private WeighRequest weighRequest;
	private WeighRequest weighDirectRequest;
	

	@Override
	public void setup(Map<String, String> parameters) throws IOException
	{
		super.setup(parameters);
		this.weighRequest = new WeighRequest("SX", this.config());
		this.weighDirectRequest = new WeighRequest("SXI", this.config());
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
			case WEIGH:
				return this.weighRequest;
			case WEIGH_DIRECT:
				return this.weighDirectRequest;
			case ZERO:
				return ZERO_REQUEST;
			case CLEAR_TARE:
				return CLEAR_TARE_REQUEST;
			case TARE:
				return TARE_REQUEST;
			case TARE_WITH_VALUE:
				float value = Float.valueOf(String.valueOf(params[1]));
				StringBuilder sb = new StringBuilder(12);
				sb.append("T ");
				sb.append(TARE_VALUE_FORMATTER.format(value));
				sb.append(" kg");
//				return new TaraValueRequest(sb.toString());	//FIXME is this correct?
				return new SimpleRequest(sb.toString());
			default:
				return null;
		}
	}

	@Override
	protected IFlexibleConnector<IHardwareRequest, HardwareResponse> createPipeline(ISocket socket)
	{
		LoggingConnector<byte[]> rawLogger = new LoggingConnector<>(socket);
		DataTransformer envelope = new DataTransformer(new MettlerMSGTransformer(), rawLogger);
		HandshakePerformer handshake = new HandshakePerformer(envelope);
		MettlerINDConnector connector = new MettlerINDConnector(handshake);
		RequestHandler requestHandler = new RequestHandler(connector);
		FlexibleExceptionConnector<IHardwareRequest, HardwareResponse> exception = new FlexibleExceptionConnector<>(requestHandler);
		FlexibleReConnector<IHardwareRequest, HardwareResponse> reconnector = new FlexibleReConnector<>(exception);
		return reconnector;
	}

}
