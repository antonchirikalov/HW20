/**
 * 
 */
package com.toennies.ci1429.app.model;

import java.util.Collection;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Type that describes a response from a device generically. It is used to generalize the processing API between
 * (DeviceSpecific)-RestControllers and the Device-Implementations (like {@link Scanner}). 
 * @author renkenh
 */
public final class DeviceResponse
{
	
	/** Simple response that indicates that the device canceled the request. */
	public static final DeviceResponse CANCELED_REQUEST = new DeviceResponse(Status.CANCELED_REQUEST);
	/** Simple response that indicates that the device is not initialized. The request cannot be processed. */
	public static final DeviceResponse BAD_NOT_INITIALIZED = new DeviceResponse(Status.BAD_NOT_INITIALIZED);
	/** Simple response that indicates that the device is not connected. The request cannot be processed. */
	public static final DeviceResponse BAD_NOT_CONNECTED = new DeviceResponse(Status.BAD_NOT_CONNECTED);
	/** Simple response that indicates that the device has processed the request but does not return any information. */
	public static final DeviceResponse OK = new DeviceResponse(Status.OK);


	/** Internal status types for validation. */
	public enum ResponseType
	{
		DATA,
		NO_DATA,
		ERROR_DATA
	}


	/**
	 * The states that are returned by a device. They handle client errors, server errors and device errors.
	 * See description for details.
	 * @author renkenh
	 */
	public enum Status
	{
		/** Indicates that the request was canceled by the device. Does not hold any payload. */
		CANCELED_REQUEST(ResponseType.NO_DATA),
		/**
		 * The device returned a response that the server was not able to understand. Contains an error description
		 * as payload.
		 */
		BAD_DEVICE_RESPONSE(ResponseType.ERROR_DATA),
		/** While the request was executed the connection to the device was unreliable and an error occurred. */
		BAD_NETWORK(ResponseType.ERROR_DATA),
		/** The client send a request that was not understood by the server. The response contains a string describing the problem. */ 
		BAD_REQUEST(ResponseType.ERROR_DATA),
		/** The server could not find the resource referenced in the request. */
		BAD_NOT_FOUND(ResponseType.ERROR_DATA),
		/** Server encountered an error that was unexpected. The response contains a string describing the problem. */
		BAD_SERVER(ResponseType.ERROR_DATA),
		/** Indicates that the device is not initialized. */
		BAD_NOT_INITIALIZED(ResponseType.NO_DATA),
		/** Indicates that the device is not connected to the device. */
		BAD_NOT_CONNECTED(ResponseType.NO_DATA),
		/** Generic Error type - Payload should be a String with additional information. */
		BAD_GENERIC_ERROR(ResponseType.ERROR_DATA),
		/** Indicates that the request was processed successfully. The response contains data retrieved from the device. */
		OK_DATA(ResponseType.DATA),
		/** Indicates that the request was processed successfully. The response contains no additional data. */
		OK(ResponseType.NO_DATA);
		
		public final ResponseType type;
		
		private Status(ResponseType type)
		{
			this.type = type;
		}
		
		public ResponseType getType()
		{
			return this.type;
		}
	}
	
	private final Status status;
	private final Object payload;


	/**
	 * Internal constructor to create responses without payload. 
	 */
	private DeviceResponse(Status status)
	{
		if (status.type != ResponseType.NO_DATA)
			throw new IllegalArgumentException("Data is missing. Wrong constructor used.");
		this.status = status;
		this.payload = null;
	}

	/**
	 * Constructor to create a response that contains a string as payload.
	 * Only those states may be used that allow strings as payload. See their
	 * description for detail.
	 * @param status The status of the response.
	 * @param data The string to set as payload.
	 */
	public DeviceResponse(Status status, String data)
	{
		if (status.type == ResponseType.NO_DATA)
			throw new IllegalArgumentException("Given status does not allow any data");
		this.status = status;
		this.payload = data;
	}

	/**
	 * Constructor. Creates a response with the given map as payload.
	 * status is set to {@link Status#OK_DATA}.
	 * @param data The payload. May not be null.
	 */
	public DeviceResponse(Object data)
	{
		this.status = Status.OK_DATA;
		this.payload = data;
	}


	/**
	 * The status of the response. Never null.
	 * @return The status of the response.
	 */
	public Status getStatus()
	{
		return this.status;
	}
	
	/**
	 * The payload - if it is a map. If not an {@link IllegalStateException} is thrown.
	 * @return The payload.
	 * @throws IllegalStateException Is thrown if the payload is not of type {@link Map}.
	 */
	@SuppressWarnings("unchecked")
	public <TYPE> TYPE getPayload()
	{
		return (TYPE) this.payload;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(this.status);
		if (this.status.type != ResponseType.NO_DATA)
		{
			sb.append('|');
			if (this.getPayload() instanceof String)
				sb.append(this.<String>getPayload());
			else if (this.getPayload() instanceof Collection)
				sb.append(this.<Collection<?>>getPayload().stream().map(Object::toString).collect(Collectors.joining(",")));
			else
				sb.append("type:" + this.getPayload().getClass().getSimpleName());
		}
		return sb.toString();
	}
}
