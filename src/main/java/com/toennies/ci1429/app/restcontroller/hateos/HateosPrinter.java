package com.toennies.ci1429.app.restcontroller.hateos;

import java.util.Collection;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import com.toennies.ci1429.app.model.printer.Printer;
import com.toennies.ci1429.app.restcontroller.PrinterRestController;

public class HateosPrinter extends HateosDevice<Printer>
{

	public HateosPrinter(Printer device)
	{
		super(device);
	}


	/**
	 * Flag that shows (in the REST-API) whether this printer in general supports label preview functionality or not. 
	 * @return Whether this printer in general support label previews.
	 */
	public boolean getPreviewSupport()
	{
		return this.device.supportsPreview();
	}


	@Override
	protected void addDeviceSpecificLinks(Collection<Link> links) {
		links.add(buildPrintLink());
		links.add(buildUploadLink());
		if (this.device.supportsPreview())
			links.add(buildPreviewLink());
	}

	private Link buildPrintLink() {
		return ControllerLinkBuilder.linkTo(
				ControllerLinkBuilder.methodOn(PrinterRestController.class).putPrintBatch(this.getId().intValue(), null, PrinterRestController.DEFAULT_BATCH_VALUE))
				.withRel("print");
	}

	private Link buildPreviewLink() {
		return ControllerLinkBuilder.linkTo(
				ControllerLinkBuilder.methodOn(PrinterRestController.class).putPreview(this.getId().intValue(), null))
				.withRel("preview");
	}

	private Link buildUploadLink() {
		return ControllerLinkBuilder
				.linkTo(ControllerLinkBuilder.methodOn(PrinterRestController.class).putUpload(this.getId().intValue(), null))
				.withRel("upload");
	}

}
