package com.toennies.ci1429.app.restcontroller.hateos;

import java.util.Collection;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import com.toennies.ci1429.app.model.watcher.Watcher;
import com.toennies.ci1429.app.restcontroller.WatcherRestController;

public class HateosWatcher extends HateosDevice<Watcher>
{

	public HateosWatcher(Watcher device)
	{
		super(device);
	}
	
	
	@Override
	protected void addDeviceSpecificLinks(Collection<Link> links) {
		links.add(buildActivateLink());
		links.add(buildEventlogLink());
		links.add(buildFullOverviewLink());
		links.add(buildDeactivateLink());
	}


	private Link buildActivateLink() {
		return ControllerLinkBuilder.linkTo(
				ControllerLinkBuilder.methodOn(WatcherRestController.class).putActivate(this.getId().intValue(), this.device.getName()))
				.withRel("activate");
	}

	private Link buildEventlogLink() {
		return ControllerLinkBuilder.linkTo(
				ControllerLinkBuilder.methodOn(WatcherRestController.class).getEventlog(this.getId(), Watcher.ALL_EVENTS, null))
				.withRel("eventlog");
	}

	private Link buildFullOverviewLink() {
		return ControllerLinkBuilder.linkTo(
				ControllerLinkBuilder.methodOn(WatcherRestController.class).getFullOverview(this.getId().intValue()))
				.withRel("fulloverview");
	}

	private Link buildDeactivateLink() {
		return ControllerLinkBuilder.linkTo(
				ControllerLinkBuilder.methodOn(WatcherRestController.class).putDeactivate(this.getId().intValue(), this.device.getName()))
				.withRel("deactivate");
	}

}
