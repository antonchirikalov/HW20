package com.toennies.ci1429.app.restcontroller.hateos;

import java.util.Collection;
import java.util.Map;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import com.toennies.ci1429.app.model.ResponseFormat;
import com.toennies.ci1429.app.model.scale.Commands.Command;
import com.toennies.ci1429.app.model.scale.Scale;
import com.toennies.ci1429.app.model.scale.Scale.Unit;
import com.toennies.ci1429.app.network.parameter.ParamDescriptor;
import com.toennies.ci1429.app.network.parameter.Parameters;
import com.toennies.ci1429.app.network.protocol.scale.AScaleProtocol;
import com.toennies.ci1429.app.restcontroller.ScaleRestController;
import com.toennies.ci1429.app.util.ScaleUtil;

public class HateosScale extends HateosDevice<Scale>
{

	public HateosScale(Scale device)
	{
		super(device);
	}


	@Override
	protected void addDeviceSpecificLinks(Collection<Link> links)
	{
		final Map<String, ParamDescriptor> defaults = Parameters.getParameters(this.device.getProtocolClass());
		final int precision = Integer.parseInt(defaults.get(AScaleProtocol.PARAM_PRECISION).getValue());
		final ResponseFormat responseFormat = ResponseFormat.valueOf(defaults.get(AScaleProtocol.PARAM_RESPONSE).getValue());
		final Unit unit = Unit.valueOf(defaults.get(AScaleProtocol.PARAM_UNIT).getValue());
		for (Command cmd : ScaleUtil.getCommands(this.device.getProtocolClass()))
		{
			Double value = null;
			if (cmd == Command.TARE_WITH_VALUE)
				value = Double.valueOf(0.0d);
			
			Link link = ControllerLinkBuilder.linkTo(
							ControllerLinkBuilder.methodOn(ScaleRestController.class)
												 .getSend(getDeviceID(), cmd, precision, responseFormat, unit, value))
												 .withRel(cmd.name().toLowerCase());
			links.add(link);
		}
	}

}
