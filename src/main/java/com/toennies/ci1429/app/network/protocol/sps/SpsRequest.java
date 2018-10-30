/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.sps;

/**
 * Simple SPS request with pojo payload.
 * @author renkenh
 */
public class SpsRequest extends ASpsRequest<Object>
{

	/** The command to be executed. */
	public final SpsCommand command;


	/**
	 * Constructor.
	 */
	public SpsRequest(SpsCommand command)
	{
		this.command = command;
	}

	/**
	 * Constructor.
	 */
	public SpsRequest(SpsCommand command, Object payload)
	{
		super(payload);
		this.command = command;
	}


	@Override
	public String getCommandID()
	{
		return this.command.name;
	}
	
}
