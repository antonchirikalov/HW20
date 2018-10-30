package com.toennies.ci1429.app.network.protocol.sps;

/**
 * Simple wrapper class that stores SPS request data. Handling of data is done
 * in {@link SpsResponseTransformer}.
 * While a {@link SpsCommand} defines an action that can theoretically be executed, the request references an actual execution request
 * with payload to a specific SPS.
 */
public abstract class ASpsRequest<PAYLOAD>
{

	private final PAYLOAD payload;
	

	/**
	 * Constructor with empty payload.
	 */
	public ASpsRequest()
	{
		this(null);
	}
	
	/**
	 * Constructor with payload.
	 * @param payload The payload.
	 */
	public ASpsRequest(PAYLOAD payload)
	{
		this.payload = payload;
	}

	
	/**
	 * Returns the command id associated with this request.
	 * @return The command id.
	 */
	public abstract String getCommandID();


	/**
	 * The payload of this request.
	 * @return The request.
	 */
	public PAYLOAD getPayload()
	{
		return this.payload;
	}
}
