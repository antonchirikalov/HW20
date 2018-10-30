package com.toennies.ci1429.app.network.protocol.sps;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.protocol.AProtocol;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.network.protocol.SerialRequestHandler;
import com.toennies.ci1429.app.network.protocol.SerialRequestHandler.ISend;
import com.toennies.ci1429.app.network.protocol.scale.AScaleProtocol;

/**
 * Abstract protocol for SPS. Provides the basic features needed for SPS implementation - like list of executable commands and list of currently active commands.
 * @author renkenh
 */
public abstract class ASpsProtocol extends AProtocol<IFlexibleConnector<SpsRequest, DeviceResponse>, SpsRequest, DeviceResponse> implements IProtocol
{

	private final ReentrantLock serialLock = new ReentrantLock();
	private final ISend sender = this.createSender();
	private SerialRequestHandler<AScaleProtocol> serializer;

	
	/**
	 * Small method that creates the basic sender for the serial adapter.
	 * @return The basic sender logic.
	 */
	protected ISend createSender()
	{
		return (params) ->
		{
			Object validated = this.validateRequest((ASpsRequest<?>) params[0]);
			if (validated instanceof DeviceResponse)
				return (DeviceResponse) validated;
			pipeline().push((SpsRequest) validated);
			return pipeline().pop();
		};
	}
	
	
	@Override
	public void setup(Map<String, String> parameters) throws IOException
	{
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

	
	private Object validateRequest(ASpsRequest<?> request)
	{
		if (request instanceof RawSpsRequest)
			return this.validateRawRequest((RawSpsRequest) request);
		return this.validatePojoRequest((SpsRequest) request);
	}

	private Object validatePojoRequest(SpsRequest request)
	{
		if (!request.command.expectsPayload())
			return request;
		
		if (request.getPayload() == null)
			return new DeviceResponse(Status.BAD_REQUEST, "Expected Payload of Type "+request.command.payloadClass.getSimpleName());

		if (!request.command.payloadClass.isAssignableFrom(request.getPayload().getClass()))
			return new DeviceResponse(Status.BAD_REQUEST, "Expected Payload of Type "+request.command.payloadClass.getSimpleName()+" bot got "+request.getPayload().getClass());
		return request;
	}

	private Object validateRawRequest(RawSpsRequest request)
	{
		SpsCommand cmd = this.findCommand(request.getCommandID());
		if (cmd == null)
			return new DeviceResponse(Status.BAD_REQUEST, "Command with ID "+request.getCommandID()+" not supported by this Sps.");
		
		try
		{
			Object payload = this.pojoFromPayload(cmd, ((RawSpsRequest) request).getPayload());
			return new SpsRequest(cmd, payload);
		}
		catch (IllegalArgumentException ex)
		{
			return Utils.generateWrongPayloadResponse(cmd);
		}
	}
	
	private SpsCommand findCommand(String cmdID)
	{
		return this.getSupportedCommands().stream().filter((c) -> c.name.equals(cmdID)).findAny().orElse(null);
	}
	
	private static final ObjectMapper mapper = new ObjectMapper();
	private Object pojoFromPayload(SpsCommand cmd, Map<String, Object> payload) throws IllegalArgumentException
	{
		if (!cmd.expectsPayload())
			return null;
		return mapper.convertValue(payload, cmd.payloadClass);
	}
	
	/**
	 * Returns a list of commands that is supported by this SPS. This list will never change.
	 * @return A list of supported commands.
	 */
	public abstract List<SpsCommand> getSupportedCommands();
	
	/**
	 * Based on the current state of the SPS this method returns all commands that can be executed at this moment. By default the
	 * supported command list is returned {@link #getSupportedCommands()}.
	 * This list may change when the SPS changes its internal state.
	 * @return A list of commands that can be executed.
	 */
	public List<SpsCommand> getActiveCommands()
	{
		return this.getSupportedCommands();
	}

}
