package com.toennies.ci1429.app.restcontroller.hateos;

import java.util.Collection;
import java.util.Map;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toennies.ci1429.app.model.sps.Sps;
import com.toennies.ci1429.app.network.protocol.sps.SpsCommand;
import com.toennies.ci1429.app.restcontroller.SpsRestController;
import com.toennies.ci1429.app.util.Utils;

public class HateosSps extends HateosDevice<Sps>
{
	public HateosSps(Sps device)
	{
		super(device);
	}

	@Override
	protected void addDeviceSpecificLinks(Collection<Link> links)
	{
		for (SpsCommand sc : this.device.getActiveCommands())
			links.add(createLink(sc));
	}
	
	private Link createLink(SpsCommand cmd)
	{
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> pojoPayload = null;
		if (cmd.expectsPayload())
		{
			Object pojo = Utils.instantiate(cmd.payloadClass.getName());
			if (pojo != null)
				pojoPayload = mapper.convertValue(pojo, new TypeReference<Map<String, Object>>() {});
		}
		return ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(SpsRestController.class)
				.putSpsCommand(getDeviceID(), cmd.name, pojoPayload))
				.withRel(cmd.name.toLowerCase());
	}

}
