/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.toennies.ci1429.app.network.connector.AWrapperTransformer;
import com.toennies.ci1429.app.network.message.IMessage;

/**
 * Simple interface to exchange data with device hardware. Currently only used by scales.
 * Defines output and input. More or less like a {@link AWrapperTransformer} but for a single
 * request only.
 * @author renkenh
 */
public interface IHardwareRequest
{
	
	/** Charset used to interpret the byte stream from the hardware device. */
	public static final Charset CHARSET = StandardCharsets.US_ASCII;


	/**
	 * Returns the message to send to execute the request represented by this object on the device.
	 * @return A message containing the request.
	 */
	public IMessage getRequestMessage();

	/**
	 * Used to parse the response from the hardware device.
	 * @param response The response in (more or less) raw format.
	 * @return The parsed response.
	 */
	public HardwareResponse handleResponse(IMessage response);

}
