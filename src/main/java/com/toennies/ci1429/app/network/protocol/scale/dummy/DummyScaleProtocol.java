/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.dummy;

import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.scale.Commands.Command;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.protocol.AtProtocol;
import com.toennies.ci1429.app.network.protocol.scale.AScaleProtocol;
import com.toennies.ci1429.app.network.protocol.scale.AtSupportedCommands;
import com.toennies.ci1429.app.network.protocol.scale.AtSupportedCommands.AtCommand;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;
import com.toennies.ci1429.app.network.protocol.scale.IHardwareRequest;
import com.toennies.ci1429.app.network.socket.ISocket;

/**
 * A dummy scale implementation. Returns random values when asked to weight. Range can be specified by parameters. This protocol does not need a specific socket.
 * Is completely ignored.
 * @author renkenh
 */
@AtProtocol(value = "Dummy Scale Protocol", deviceType = DeviceType.SCALE)
@AtDefaultParameters(value=
{
	@Parameter(name=DummyScaleProtocol.PARAM_LOWER_BOUND, value="1", typeInformation="int:1..", toolTip="The lower bound of the weights in KG this scale should generate."),
	@Parameter(name=DummyScaleProtocol.PARAM_UPPER_BOUND, value="10", typeInformation="int:1..", toolTip="The upper bound of the weights in KG this scale should generate.")
})
@AtSupportedCommands(value=
{
	@AtCommand(value=Command.CLEAR_TARE),
	@AtCommand(value=Command.ITEM_ADDING),
	@AtCommand(value=Command.ITEM_NOT_ADDING),
	@AtCommand(value=Command.RESET),
	@AtCommand(value=Command.TARE),
	@AtCommand(value=Command.TARE_WITH_VALUE),
	@AtCommand(value=Command.WEIGH),
	@AtCommand(value=Command.WEIGH_DIRECT)
})
public class DummyScaleProtocol extends AScaleProtocol
{
	
	/** The lower bound of the generated values (in KG). */
	public static final String PARAM_LOWER_BOUND = "lowerbound";
	/** The upper bound of the generated values (in KG). */
	public static final String PARAM_UPPER_BOUND = "upperbound";

	@Override
	protected IHardwareRequest transform(Object... params)
	{
		Command cmd = (Command) params[0];
		if (cmd != Command.TARE_WITH_VALUE)
			return new DummyRequest(cmd);
		float value = Float.valueOf(String.valueOf(params[1]));
		return new DummyTareValueRequest(value);
	}

	@Override
	protected IFlexibleConnector<IHardwareRequest, HardwareResponse> createPipeline(ISocket socket)
	{
		return new DummyScalePipe();
	}

}
