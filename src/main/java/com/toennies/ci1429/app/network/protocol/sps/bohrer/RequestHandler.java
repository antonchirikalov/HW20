/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.sps.bohrer;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeoutException;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.network.connector.AFlexibleWrapperTransformer;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;
import com.toennies.ci1429.app.network.protocol.scale.IHardwareRequest;
import com.toennies.ci1429.app.network.protocol.sps.SpsRequest;
import com.toennies.ci1429.app.network.protocol.sps.telegram.Telegram;

/**
 * Scale protocol transformer that uses {@link IHardwareRequest}s and {@link HardwareResponse}s to parse client requests into
 * device specific messages.
 * @author renkenh
 */
class RequestHandler extends AFlexibleWrapperTransformer<SpsRequest, DeviceResponse, Telegram, Telegram>
{

	private final Deque<SpsRequest> requestQueue = new ArrayDeque<>();


	/**
	 * Constructor.
	 */
	public RequestHandler(InfoCollector collector)
	{
		super(collector);
	}


	@Override
	public synchronized void connect(IConfigContainer config) throws IOException
	{
		this.requestQueue.clear();
		super.connect(config);
	}

	
	@Override
	public DeviceResponse poll() throws IOException
	{
		SpsRequest request = this.requestQueue.pop();
		if (request.command == SpsEnddarmbohrerProtocol.GET_INFOS)
			return new DeviceResponse(((InfoCollector) this.connector).pollAvailableInfos());
		if (request.command == SpsEnddarmbohrerProtocol.GET_STATUS)
		{
			this.connector.push(new StatusRequest());
			return new DeviceResponse(this.connector.poll());
		}
		return new DeviceResponse(Status.BAD_REQUEST, "Command " + request.getCommandID() + " unknown. ");
	}

	@Override
	public DeviceResponse pop() throws IOException, TimeoutException
	{
		SpsRequest request = this.requestQueue.pop();
		if (request.command == SpsEnddarmbohrerProtocol.GET_INFOS)
			return new DeviceResponse(((InfoCollector) this.connector).pollAvailableInfos());
		if (request.command == SpsEnddarmbohrerProtocol.GET_STATUS)
		{
			this.connector.push(new StatusRequest());
			return new DeviceResponse(this.connector.poll());
		}
		return new DeviceResponse(Status.BAD_REQUEST, "Command " + request.getCommandID() + " unknown. ");
	}

	@Override
	public synchronized void push(SpsRequest entity) throws IOException
	{
		this.requestQueue.push(entity);
	}

	@Override
	protected synchronized DeviceResponse transformToOut(Telegram telegram)
	{
		//not called.
		return null;
	}
	

	@Override
	protected Telegram transformToConIn(SpsRequest entity)
	{
		//not called.
		return null;
	}

}
