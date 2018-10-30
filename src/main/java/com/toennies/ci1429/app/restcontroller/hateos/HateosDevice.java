package com.toennies.ci1429.app.restcontroller.hateos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.toennies.ci1429.app.model.ADevice;
import com.toennies.ci1429.app.model.DeviceException;
import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.IDevice.DeviceState;
import com.toennies.ci1429.app.model.IDeviceDescription;
import com.toennies.ci1429.app.restcontroller.DevicesRestController;

/**
 * This class wraps a given {@link ADevice} objects. Through this wrapper hateos
 * links gets added to {@link ADevice} objects. Hateos links are only needed in
 * {@link DevicesRestController}.
 */
public class HateosDevice<DEVICE extends com.toennies.ci1429.app.model.IDevice> implements IDeviceDescription, Identifiable<Integer>
{

	private static final Logger logger = LogManager.getLogger();

	/**
	 * The wrapped device.
	 */
	protected final DEVICE device;

	/**
	 * Constructor.
	 * @param device The wrapped device.
	 */
	public HateosDevice(DEVICE device) {
		if (device == null) {
			throw new IllegalArgumentException("Given device can't be null.");
		}
		this.device = device;
	}

	/**
	 * Hateos Links method. The links are created on the base of the device state.
	 * @return The heateos links. 
	 */
	public Collection<Link> getLinks() {
		Collection<Link> links = new ArrayList<Link>();
		this.addDefaultLinks(links);
		if (this.device.getDeviceState() == DeviceState.CONNECTED)
			this.addDeviceSpecificLinks(links);
		return links;
	}

	/**
	 * Adds hateos links that are common for every device.
	 */
	protected void addDefaultLinks(Collection<Link> links) {
		links.add(buildSelfLink());
		if (device.getDeviceState() == DeviceState.NOT_INITIALIZED) {
			links.add(buildInitLink());
		} else {
			links.add(buildShutdownLink());
		}
	}

	/**
	 * Some devices provide more hateos links. Override this method, if there
	 * are device specific links to add.
	 */
	protected void addDeviceSpecificLinks(Collection<Link> links) {
		// Nothing to do here
	}

	
	private Link buildInitLink() {
		try {
			return ControllerLinkBuilder.linkTo(
					ControllerLinkBuilder.methodOn(DevicesRestController.class).getInitDevice(device.getDeviceID()))
					.withRel("init");
		} catch (DeviceException e) {
			// It's rubbish, but we have to catch the exception.
			// ControllerLinkBuilder fakes an method invocation. So it's not
			// possible.
			logger.error("Error while building initlink", e);
			return null;
		}
	}

	private Link buildShutdownLink() {
		return ControllerLinkBuilder.linkTo(
				ControllerLinkBuilder.methodOn(DevicesRestController.class).getShutdownDevice(this.getId().intValue()))
				.withRel("shutdown");
	}

	private Link buildSelfLink() {
		return ControllerLinkBuilder.linkTo(DevicesRestController.class).slash(this).withSelfRel();
	}

	@JsonIgnore
	@Override
	public Integer getId() {
		return Integer.valueOf(this.getDeviceID());
	}

	public DeviceState getState() {
		return device.getDeviceState();
	}

	@Override
	public DeviceType getType() {
		return device.getType();
	}

	@Override
	public Map<String, String> getParameters() {
		return device.getParameters();
	}

	public Map<String, String> getConfiguration() {
		return device.getConfiguration();
	}

	@Override
	public int getDeviceID()
	{
		return this.device.getDeviceID();
	}

	@Override
	public String getVendor()
	{
		return this.device.getVendor();
	}

	@Override
	public String getDeviceModel()
	{
		return this.device.getDeviceModel();
	}

	@Override
	public String getProtocolClass()
	{
		return this.device.getProtocolClass();
	}

}
