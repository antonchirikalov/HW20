/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.dummy;

import com.toennies.ci1429.app.model.scale.Commands.Command;

/**
 * Request to tare with value. Contains the value to tare with.
 * @author renkenh
 */
class DummyTareValueRequest extends DummyRequest
{

	/** The value to tare with. */
	public final double value;


	/**
	 * Constructor.
	 * @param value The value to tare with.
	 */
	public DummyTareValueRequest(double value)
	{
		super(Command.TARE_WITH_VALUE);
		this.value = value;
	}

}
