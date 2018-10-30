/**
 * 
 */
package com.toennies.ci1429.app.hw10.client;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.network.connector.AFlexibleWrapperTransformer;
import com.toennies.ci1429.app.network.connector.IConnector;
import com.toennies.ci1429.app.util.ASCII;

/**
 * @author renkenh
 *
 */
public class ResponseTransformer extends AFlexibleWrapperTransformer<DeviceResponse, String, String, String>
{

	/**
	 * @param connector
	 */
	public ResponseTransformer(IConnector<String> connector)
	{
		super(connector);
	}


	@Override
	protected String transformToOut(String entity)
	{
		return entity;
	}

	@Override
	protected String transformToConIn(DeviceResponse entity)
	{
		String response = String.valueOf(ASCII.NAK.c);
		if (entity == DeviceResponse.OK)
			response = String.valueOf(ASCII.ACK.c);
		else if (entity.getStatus() == Status.OK_DATA && entity.getPayload() != null)
			response = entity.getPayload();
		return response;
	}

}
