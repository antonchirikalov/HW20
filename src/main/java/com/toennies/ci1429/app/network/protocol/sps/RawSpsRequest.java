/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.sps;

import java.util.Map;

/**
 * Raw SPS request. Takes as payload a map with parameters instead of a pojo.
 * @author renkenh
 */
public class RawSpsRequest extends ASpsRequest<Map<String, Object>>
{

	private final String commandID;
	
	
	/**
	 * Constructor.
	 */
	public RawSpsRequest(String commandID)
	{
		this.commandID = commandID;
	}

	/**
	 * Constructor with payload.
	 */
	public RawSpsRequest(String commandID, Map<String, Object> payload)
	{
		super(payload);
		this.commandID = commandID;
	}


	@Override
	public String getCommandID()
	{
		return this.commandID;
	}

}
