package com.toennies.ci1429.app.network.protocol.scale;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.model.ResponseFormat;
import com.toennies.ci1429.app.model.scale.Scale.Unit;
import com.toennies.ci1429.app.model.scale.WeightData;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.protocol.AProtocol;
import com.toennies.ci1429.app.network.protocol.SerialRequestHandler;
import com.toennies.ci1429.app.network.protocol.SerialRequestHandler.ISend;
import com.toennies.ci1429.app.network.socket.ISocket;

/**
 * Abstract scale protocol that parses the given parameters and calls the network stack.
 * Does specify some generic parameters for scales.
 * @author renkenh
 */
@Parameter(name=AScaleProtocol.PARAM_UNIT, value="KG", typeInformation="enum:com.toennies.ci1429.app.model.scale.Scale$Unit", toolTip="Weight unit used for communication with scale hardware.")
@Parameter(name=AScaleProtocol.PARAM_RESPONSE, value="EAN128", typeInformation="enum:com.toennies.ci1429.app.model.ResponseFormat", toolTip="Specifies which format the response should have.")
@Parameter(name=AScaleProtocol.PARAM_PRECISION, value="3", typeInformation="int:0..9", toolTip="Specify the accuracy (number of digits) after the decimal point.")
@Parameter(name=AScaleProtocol.PARAM_UNIT, value="KG", typeInformation="enum:com.toennies.ci1429.app.model.scale.Scale$Unit", toolTip="The default unit in which to weight.")
public abstract class AScaleProtocol extends AProtocol<IFlexibleConnector<IHardwareRequest, HardwareResponse>, IHardwareRequest, HardwareResponse>
{
	
	/** Response format. Must be of type {@link ResponseFormat}. */
	public static final String PARAM_RESPONSE = "response";
	/** Parameter to control the precision of the returned value. Must be a positive number. */
	public static final String PARAM_PRECISION = "precision";
	/** Parameter to control the unit of the returned value. Must be of type {@link Unit}. */
	public static final String PARAM_UNIT = "unit";

	private final ReentrantLock serialLock = new ReentrantLock();
	private SerialRequestHandler<AScaleProtocol> serializer;

	
	private ISend sender = (params) ->
	{
			this.pipeline().connect(this.config());
		try
		{
			this.pipeline().push(this.transform(params));
			return mapResponse(this.pipeline().pop());
		}
		catch (IOException | TimeoutException e)
		{
			this.pipeline().disconnect();
			throw e;
		}
	};


	@Override
	public void setup(Map<String, String> parameters) throws IOException
	{
		parameters = checkRequestTimeout(parameters);
		super.setup(parameters);
		this.serialLock.lock();
		try
		{
			if (this.serializer != null)
				this.serializer.shutdown();
			this.serializer = new SerialRequestHandler<>(this.sender, this.config().getIntEntry(PARAM_REQUEST_TIMEOUT));
		}
		finally
		{
			this.serialLock.unlock();
		}
	}
	
	private static final Map<String, String> checkRequestTimeout(Map<String, String> parameters)
	{
		HashMap<String, String> modded = new HashMap<>(parameters);
		String sTimeout = parameters.get(ISocket.PARAM_TIMEOUT);
		String sRequestTimeout = parameters.get(AProtocol.PARAM_REQUEST_TIMEOUT);
		if (sTimeout != null && sRequestTimeout != null)
		{
			int timeout = Integer.parseInt(sTimeout);
			int requestTimeout = Integer.parseInt(sRequestTimeout);
			if (requestTimeout < 5 * timeout)
			{
				requestTimeout = 5 * timeout;
				logger.warn("Scale miss-configuration request timeout must be five times higher than protocol timeout. New requestTimeout: {}", requestTimeout);
				modded.put(AProtocol.PARAM_REQUEST_TIMEOUT, String.valueOf(requestTimeout));
			}
		}
		return modded;
	}
	
	@Override
	protected Object _send(Object... params) throws IOException, TimeoutException
	{
		this.serialLock.lock();
		try
		{
			return this.serializer.send(params);
		}
		finally
		{
			this.serialLock.unlock();
		}
	}

	protected abstract IHardwareRequest transform(Object... params);
	
	@Override
	public void shutdown()
	{
		super.shutdown();
		this.serialLock.lock();
		try
		{
			if (this.serializer != null)
			{
				this.serializer.shutdown();
				this.serializer = null;
			}
		}
		finally
		{
			this.serialLock.unlock();
		}
	}

	private static final DeviceResponse mapResponse(HardwareResponse hwResponse)
	{
		switch (hwResponse.status)
		{
			case CANCELED:
				return DeviceResponse.CANCELED_REQUEST;
			case ERROR:
				return new DeviceResponse(Status.BAD_DEVICE_RESPONSE, hwResponse.getErrorString());
			case OK:
				return DeviceResponse.OK;
			case OK_DATA:
				WeightData data = hwResponse.getWeightData();
				return new DeviceResponse(data);
			default:
				break;
		}
		return new DeviceResponse(Status.BAD_SERVER, "Unknown Response: " + String.valueOf(hwResponse));
	}
}
