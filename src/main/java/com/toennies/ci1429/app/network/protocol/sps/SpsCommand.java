package com.toennies.ci1429.app.network.protocol.sps;

/**
 * This class collects all data, that is needed to define a command that can be
 * send to a sps. Every command represents a concrete action which can be
 * performed by sps.
 * 
 * A sps command is defined by a combination of telegram-header and payload
 * data. So this class provides getter methods in order to build a concrete
 * telegram object.
 *
 */
public class SpsCommand
{
	
	public final String name;
	public final Class<?> payloadClass;

	
	public SpsCommand(String name)
	{
		this(name, null);
	}
	
	public SpsCommand(String name, Class<?> payloadClass)
	{
		this.name = name;
		this.payloadClass = payloadClass;
	}

	
	/**
	 * Returns whether this command expects payload or not.
	 * @return Whether this command expects payload or not.
	 */
	public boolean expectsPayload()
	{
		return this.payloadClass != null && this.payloadClass == Void.class;
	}
}
