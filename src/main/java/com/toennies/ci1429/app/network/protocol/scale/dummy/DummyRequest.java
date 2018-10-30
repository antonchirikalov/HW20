/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.dummy;

import com.toennies.ci1429.app.model.scale.Commands.Command;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;
import com.toennies.ci1429.app.network.protocol.scale.IHardwareRequest;

/**
 * Simple request, containing the scale command only.
 * @author renkenh
 */
class DummyRequest implements IHardwareRequest
{

	/** The command to process by the dummy scale protocol. */
	public final Command cmd;


	/**
	 * Constructor.
	 * @param cmd The command to process.
	 */
	public DummyRequest(Command cmd)
	{
		this.cmd = cmd;
	}


	@Override
	public IMessage getRequestMessage()
	{
		return null;
	}

	@Override
	public HardwareResponse handleResponse(IMessage response)
	{
		return null;
	}

}
