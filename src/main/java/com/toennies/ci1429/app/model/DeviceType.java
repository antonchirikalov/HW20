package com.toennies.ci1429.app.model;

import com.toennies.ci1429.app.model.printer.Printer;
import com.toennies.ci1429.app.model.scale.Scale;
import com.toennies.ci1429.app.model.scanner.Scanner;
import com.toennies.ci1429.app.model.sps.Sps;
import com.toennies.ci1429.app.model.watcher.Watcher;

/**
 * The device classes.
 * @author renkenh
 */
public enum DeviceType
{
	
	SCALE("Scale", Scale.class),
	SCANNER("Scanner", Scanner.class),
	PRINTER("Printer", Printer.class),
	SPS("SPS", Sps.class),
	WATCHER("Monitor", Watcher.class);


	public final String caption;
	public final Class<? extends ADevice<?>> implementation;


	private DeviceType(String caption, Class<? extends ADevice<?>> implementation)
	{
		this.caption = caption;
		this.implementation = implementation;
	}

	@Override
	public String toString() {
		return caption;
	}

}
