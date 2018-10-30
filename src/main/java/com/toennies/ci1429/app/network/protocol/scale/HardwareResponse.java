/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale;

import com.toennies.ci1429.app.model.scale.WeightData;

/**
 * Type to hold the parsed response from a device for a specific request.
 * @author renkenh
 */
public class HardwareResponse
{
	
	/** Simple response, that indicates that everything was ok. Does not have payload. */
	public static final HardwareResponse OK = new HardwareResponse(Status.OK);
	/** Simple response, that indicates that the request was canceled by the device. Does not have payload. */
	public static final HardwareResponse CANCELED = new HardwareResponse(Status.CANCELED);
	/** Simple response, that indicates that the device asks to wait for the final response. Does not have payload. */
	public static final HardwareResponse WAIT = new HardwareResponse(Status.WAIT);


	/**
	 * The response states that can be returned by a hardware device.
	 * @author renkenh
	 */
	public static enum Status
	{
		OK, OK_DATA, ERROR, CANCELED, WAIT
	}

	
	//this must be protected because of the header response from bizerba (which does not fit into the current structure).
	/** The status of this response. */
	protected final Status status;
	/** The payload of this response. */
	protected final Object payload;

	
	private HardwareResponse(Status status)
	{
		this(status, null);
	}
	
	/**
	 * Constructor for error responses.
	 * @param errorString A human readable string to describe the error.
	 */
	public HardwareResponse(String errorString)
	{
		this(Status.ERROR, errorString);
	}
	
	/**
	 * Constructor for weight data.
	 * @param weightdata The weight data.
	 */
	public HardwareResponse(WeightData weightdata)
	{
		this(Status.OK_DATA, weightdata);
	}
	

	/**
	 * Internal constructor to set the status and payload to arbitrary values. Should be used internal only.
	 * @param status The status.
	 * @param payload The payload.
	 * 
	 */
	protected HardwareResponse(Status status, Object payload)
	{
		this.status = status;
		this.payload = payload;
	}


	/**
	 * @return The status of this payload.
	 */
	public Status getStatus()
	{
		return this.status;
	}
	
	/**
	 * @return The error string - only valid when {@link #getStatus()} equals {@link Status#ERROR}.
	 */
	public String getErrorString()
	{
		return String.valueOf(this.payload);
	}

	/**
	 * @return weight data from the scale. Only valid when {@link #getStatus()} equals {@link Status#OK_DATA}.
	 */
	public WeightData getWeightData()
	{
		return (WeightData) this.payload;
	}

}
