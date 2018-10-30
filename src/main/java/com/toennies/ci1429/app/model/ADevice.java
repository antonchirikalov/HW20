/**
 * 
 */
package com.toennies.ci1429.app.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.network.event.AEventNotifier;
import com.toennies.ci1429.app.network.event.IEventHandler;
import com.toennies.ci1429.app.network.parameter.ParamDescriptor;
import com.toennies.ci1429.app.network.parameter.Parameters;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.repository.DeviceDescriptionEntity;
import com.toennies.ci1574.lib.helper.Generics;


/**
 * Abstract {@link IDevice} implementation. This handles standard tasks like initialization of the device,
 * status handling, data processing (not defined by IDevice) and shutdown.
 * @author renkenh
 */
public abstract class ADevice<P extends IProtocol> extends AEventNotifier implements IDevice, IEventHandler
{
	
	private static final String ERROR_PROTOCOL_NOT_SPECIFIED = "Protocol not specified.";
	
	private static final Logger LOGGER = LogManager.getLogger();

	
	private final ReentrantLock lock = new ReentrantLock();
	private IDeviceDescription description;
//	private volatile State state = State.NOT_INITIALIZED;
	private volatile P protocol = null;
//	private volatile boolean faulty = false;


	/**
	 * Update the device with a new description. The old description is overwritten.
	 * If the device is connected to hardware. A {@link #deactivateDevice()} is done before
	 * overwriting.
	 * @param description The new description.
	 */
	public void updateDevice(IDeviceDescription description)
	{
		this.lock.lock();
		try
		{
			this.deactivateDevice();

			this.description = new DeviceDescriptionEntity(description);
//			this.setState(State.NOT_INITIALIZED);
		}
		finally
		{
			this.lock.unlock();
		}
		this.publishEvent(EVENT_PARAMS_UPDATED, this);
	}
	
	@Override
	public void activateDevice() throws DeviceException
	{
		if (this.getDeviceState().initialized)
			return;

		this.lock.lock();
		try
		{
			String protocolClass = this.description.getProtocolClass();
			if (protocolClass == null || protocolClass.length() == 0)
			{
				LOGGER.error(ERROR_PROTOCOL_NOT_SPECIFIED);
				this.publishEvent(EVENT_ERROR_OCCURRED, new DeviceResponse(Status.BAD_GENERIC_ERROR, ERROR_PROTOCOL_NOT_SPECIFIED));
				throw new DeviceException(this, ERROR_PROTOCOL_NOT_SPECIFIED);
			}

//			this.faulty = false;
			this.protocol = Generics.convertUnchecked(Class.forName(protocolClass).newInstance());
			this.protocol.setup(this.getParameters());
			this.protocol.registerEventHandler(this);
//			this.setState(State.INITIALIZED);
			this.publishEvent(EVENT_STATE_CHANGED);
		}
		catch (InstantiationException | IllegalAccessException | ClassNotFoundException | ClassCastException e)
		{
			this.protocol = null;
//			this.setState(State.NOT_INITIALIZED);
			LOGGER.error("Configuration Error {}. Could not load specified protocol.", this.description, e);
			this.publishEvent(EVENT_ERROR_OCCURRED, new DeviceResponse(Status.BAD_GENERIC_ERROR, "Could not load specified protocol."));
			throw new DeviceException(this, e, "Configuration Error. Could not load specified protocol.");
		}
		catch (IOException e2)
		{
			this.protocol = null;
//			this.setState(State.NOT_INITIALIZED);
			LOGGER.error("Could setup network stack {}.", this.description, e2);
			this.publishEvent(EVENT_ERROR_OCCURRED, new DeviceResponse(Status.BAD_NETWORK, "Could connect to Hardware: " + e2.getMessage()));
			throw new DeviceException(this, e2, "Could not connect to Hardware.");
		}
		finally
		{
			this.lock.unlock();
		}
	}
	
	@Override
	public DeviceState getDeviceState()
	{
		this.lock.lock();
		try
		{
			if (this.protocol == null)
				return DeviceState.NOT_INITIALIZED;
			if (!this.protocol.isConnected())
				return DeviceState.INITIALIZED;
//			if (this.faulty)
//				return DeviceState.FAULTY;
			return DeviceState.CONNECTED;
		}
		finally
		{
			this.lock.unlock();
		}
	}
	
//	protected void setState(State state)
//	{
//		State old = state;
//		this.lock.lock();
//		try
//		{
//			old = this.state;
//			this.state = state;
//		}
//		finally
//		{
//			this.lock.unlock();
//		}
//		if (old != state)
//			this.publishEvent(EVENT_STATE_CHANGED);
//	}


	protected P protocol()
	{
		this.lock.lock();
		try
		{
			return this.protocol;
		}
		finally
		{
			this.lock.unlock();
		}
	}


	@Override
	public @NotNull DeviceResponse batchProcess(int batch, Object... params)
	{
		List<Object> payloads = new ArrayList<>(batch);
		for (int i = 0; i < batch; i++)
		{
			DeviceResponse response = this.process(params);
			if (response.getStatus() != Status.OK_DATA && response.getStatus() != Status.OK)
				return response;
			payloads.add(response.getPayload());
		}
		return new DeviceResponse(payloads);
	}

	@Override
	public @NotNull DeviceResponse process(Object... params)
	{
		if (this.getDeviceState() == DeviceState.NOT_INITIALIZED)
			return DeviceResponse.BAD_NOT_INITIALIZED;
		if (this.getDeviceState() != DeviceState.CONNECTED)
			return DeviceResponse.BAD_NOT_CONNECTED;
		
		try
		{
			Object response = this.protocol().send(params);	//maybe add new type ProtocolResponse
			if (response instanceof DeviceResponse)
				return (DeviceResponse) response;
			return new DeviceResponse(response);
		}
		catch (IOException e2)
		{
			DeviceResponse badNetwork = new DeviceResponse(Status.BAD_NETWORK, e2.getMessage());
			this.setFaulty(badNetwork, e2);
			return badNetwork;
		}
		catch (TimeoutException te)
		{
			DeviceResponse badNetwork = new DeviceResponse(Status.BAD_NETWORK, "Could not get data from device "+this.getDeviceID()+" in time.");
			this.setFaulty(badNetwork, te);
			return badNetwork;
		}
	}
	
	private void setFaulty(DeviceResponse response, Exception ex)
	{
		this.lock.lock();
		try
		{
//			this.faulty = true;
			LOGGER.error("Could not get data from device in time: {} - {}.", this.description, ex);
			this.publishEvent(EVENT_STATE_CHANGED);
			this.publishEvent(EVENT_ERROR_OCCURRED, response);
		}
		finally
		{
			this.lock.unlock();
		}
	}

	@Override
	public void deactivateDevice()
	{
		this.lock.lock();
		try
		{
			if (!this.getDeviceState().initialized)
				return;

			this.protocol.unregisterEventHandler(this);
			this.protocol.shutdown();
		}
		finally
		{
			this.protocol = null;
//			this.setState(State.NOT_INITIALIZED);
			this.lock.unlock();
		}
		this.publishEvent(EVENT_STATE_CHANGED);
	}

	private volatile AtomicInteger publishedStateOrdinal = new AtomicInteger(DeviceState.NOT_INITIALIZED.ordinal());
	@Override
	protected void publishEvent(String eventID, Object... parameters)
	{
		if (EVENT_STATE_CHANGED.equals(eventID))
		{
			DeviceState currentState = this.getDeviceState();
			int oldOrdinal = this.publishedStateOrdinal.getAndSet(currentState.ordinal());
			if (currentState.ordinal() == oldOrdinal)
				return;
			parameters = new Object[] { currentState };
		}

		super.publishEvent(eventID, parameters[0]);
	}

	@Override
	public void handleEvent(String eventID, Object source, Object... params)
	{
		if (EVENT_ERROR_OCCURRED.equals(eventID))
		{
//			this.faulty = true;
			this.publishEvent(EVENT_STATE_CHANGED);
		}
		this.publishEvent(eventID, params);
	}

	@Override
	public int getDeviceID()
	{
		return this.description.getDeviceID();
	}

	@Override
	public DeviceType getType()
	{
		return this.description.getType();
	}

	@Override
	public String getVendor()
	{
		return this.description.getVendor();
	}

	@Override
	public String getDeviceModel()
	{
		return this.description.getDeviceModel();
	}

	@Override
	public String getProtocolClass()
	{
		return this.description.getProtocolClass();
	}


	@Override
	public Map<String, String> getParameters()
	{
		HashMap<String, String> config = new HashMap<>();
		this.lock.lock();
		try
		{
			config.putAll(this.description.getParameters());
		}
		finally
		{
			this.lock.unlock();
		}
		return config;
	}

	@Override
	public Map<String, String> getConfiguration()
	{
		this.lock.lock();
		try
		{
			if (this.getDeviceState().initialized)
				return this.protocol().getConfig();

			//try to read the parameters from the description and given classnames
			HashMap<String, String> config = new HashMap<>();
			Map<String, ParamDescriptor> parameters = Parameters.getParameters(this.description.getProtocolClass());
			parameters.values().forEach((p) -> config.put(p.getName(), p.getValue()));
			parameters = Parameters.getParameters(this.description.getProtocolClass());
			parameters.values().forEach((p) -> config.put(p.getName(), p.getValue()));
			config.putAll(this.description.getParameters());
			return config;
		}
		finally
		{
			this.lock.unlock();
		}
	}
	
}
