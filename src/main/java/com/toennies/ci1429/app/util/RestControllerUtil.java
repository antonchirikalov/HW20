/**
 * 
 */
package com.toennies.ci1429.app.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.ResponseType;

/**
 * @author renkenh
 *
 */
public class RestControllerUtil
{
	
//	/**
//	 * Maps the given response (from a batch process) to a single response entity.
//	 * Handles the special case when the list contains a single response that indicates an error while
//	 * batch processing.
//	 * @param responses The responses to map.
//	 * @return A response entity that contains the result.
//	 */
//	public static final ResponseEntity<?> map(List<DeviceResponse> responses)
//	{
//		if (responses.size() == 1 && responses.get(0).getStatus() != Status.OK_DATA)
//			return RestControllerUtil.map(responses.get(0));
//
//		List<Object> payload = new ArrayList<>(responses.size());
//		for (DeviceResponse cr : responses)
//			payload.add(mapPayload(cr));
//
//		return new ResponseEntity<>(payload, HttpStatus.OK);
//	}

	/**
	 * Maps a single response to an appropriate response entity.
	 * @param response The response to map.
	 * @return A response entity.
	 */
	public static final ResponseEntity<?> map(DeviceResponse response)
	{
		if (response.getStatus().type == ResponseType.DATA)
			return null;
		return new ResponseEntity<>(mapPayload(response), mapStatus(response));
	}

	public static final HttpStatus mapStatus(DeviceResponse response)
	{
		switch (response.getStatus())
		{
			case BAD_DEVICE_RESPONSE:
			case CANCELED_REQUEST:
				return HttpStatus.BAD_GATEWAY;
			case BAD_NETWORK:
			case BAD_NOT_CONNECTED:
			case BAD_NOT_INITIALIZED:
				return HttpStatus.SERVICE_UNAVAILABLE;
			case BAD_REQUEST:
				return HttpStatus.BAD_REQUEST;
			case BAD_SERVER:
				return HttpStatus.INTERNAL_SERVER_ERROR;
			case BAD_NOT_FOUND:
				return HttpStatus.NOT_FOUND;
			case OK:
				return HttpStatus.NO_CONTENT;
			case OK_DATA:
				return HttpStatus.OK;
			default:
				return HttpStatus.NOT_IMPLEMENTED;
		}
	}
	
	private static final Object mapPayload(DeviceResponse response)
	{
		switch (response.getStatus().type)
		{
			case DATA:
				throw new RuntimeException("Should never happen. Must be mapped in RESTController.");
			case ERROR_DATA:
				return String.valueOf(response.<Object>getPayload());
			default:
				return null;
		}
	}

	private RestControllerUtil()
	{
		//no instance
	}

}
