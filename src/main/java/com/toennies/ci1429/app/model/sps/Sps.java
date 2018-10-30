package com.toennies.ci1429.app.model.sps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.toennies.ci1429.app.model.ADevice;
import com.toennies.ci1429.app.model.IDeviceDescription;
import com.toennies.ci1429.app.network.protocol.sps.ASpsProtocol;
import com.toennies.ci1429.app.network.protocol.sps.SpsCommand;

/**
 * Concrete implementation of a sps.
 */
public class Sps extends ADevice<ASpsProtocol>
{

	public Sps(IDeviceDescription description)
	{
		this.updateDevice(description);
	}

	
	public List<SpsCommand> getActiveCommands()
	{
		if (protocol() == null)
			return Collections.emptyList();
		return protocol().getActiveCommands();
	}
	
	/**
	 * @return a {@link Map} of supported {@link SpsCommand}. Also have a look
	 *         at {@link ASpsProtocol#getSupportedCommands()}.
	 */
	public List<SpsCommand> getSupportedCommands()
	{
		if (protocol() == null)
			return Collections.emptyList();
		return protocol().getSupportedCommands();
	}
}
