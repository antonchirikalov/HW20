/**
 * 
 */
package com.toennies.ci1429.app.model.printer;

import com.toennies.ci1429.app.model.ADevice;
import com.toennies.ci1429.app.model.IDeviceDescription;
import com.toennies.ci1429.app.network.protocol.printer.APrinterProtocol;


/**
 * The device class "Printer".
 * @author renkenh
 */
public class Printer extends ADevice<APrinterProtocol>
{
	
	public Printer(IDeviceDescription description)
	{
		this.updateDevice(description);
	}

	
	/**
	 * Returns whether this printer supports previews or not.
	 * @return whether this printer supports previews or not.
	 */
	public boolean supportsPreview()
	{
		return this.protocol().supportsPreview();
	}
}
