package com.toennies.ci1429.app.restcontroller.hateos;

import java.util.Collection;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import com.toennies.ci1429.app.model.ResponseFormat;
import com.toennies.ci1429.app.model.scanner.Scanner;
import com.toennies.ci1429.app.restcontroller.ScannerRestController;

public class HateosScanner extends HateosDevice<Scanner>
{

	public HateosScanner(Scanner device)
	{
		super(device);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addDeviceSpecificLinks(Collection<Link> links) {
		links.add(buildScannerLink());
	}

	private Link buildScannerLink() {
		return ControllerLinkBuilder
				.linkTo(ControllerLinkBuilder.methodOn(ScannerRestController.class).getScan(this.getId().intValue(),
						ResponseFormat.HUMAN.name(), ScannerRestController.DEFAULT_BATCH_VALUE))
				.withRel("scan");
	}

}
