/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.sps;

import org.apache.logging.log4j.LogManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;

/**
 * Utilities for SPS framework.
 * @author renkenh
 */
class Utils
{


	/**
	 * Generates a BAD_REQUEST response when the given payload does not match the expected classes.
	 * @param cmd The command that should be executed.
	 * @return The device response.
	 */
	public static final DeviceResponse generateWrongPayloadResponse(SpsCommand cmd)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Expected Payload of Type ");
		sb.append(cmd.payloadClass.getSimpleName());
		
		Object pojo = com.toennies.ci1429.app.util.Utils.instantiate(cmd.payloadClass.getName());
		if (pojo != null)
		{
			try
			{
				sb.append('\n');
				ObjectMapper mapper = new ObjectMapper();
				String example = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pojo);
				sb.append("Empty Example:\n");
				sb.append(example);
			}
			catch (JsonProcessingException jex)
			{
				LogManager.getLogger().debug("Could not generate example json for class {}.", cmd.payloadClass, jex);
			}
		}
		return new DeviceResponse(Status.BAD_REQUEST, sb.toString());
	}
	
	
	private Utils()
	{
		//no instance
	}

}
