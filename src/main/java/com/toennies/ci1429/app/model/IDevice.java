package com.toennies.ci1429.app.model;

import java.util.Map;

import javax.validation.constraints.NotNull;

import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.network.event.IEventNotifier;
import com.toennies.ci1429.app.network.message.IMessage;

/**
 * Type that represents a hardware device. This is the main object to interact with.
 * It is responsible for initialization of the network stack.
 * @author renkenh
 */
public interface IDevice extends IDeviceDescription, IEventNotifier
{

	/**
	 * Published whenever the state of the device ({@link #getDeviceState()}) has changed.
	 * Payload is the new state.
	 */
	public static final String EVENT_STATE_CHANGED = "EVENT_STATE_CHANGED";
	/**
	 * Published whenever the parameters of the device are changed.
	 * Payload is the device.
	 */
	public static final String EVENT_PARAMS_UPDATED = "EVENT_PARAMS_UPDATED";
	/**
	 * Published when data has been send to the hardware device.
	 * The event usually has a byte[] or an {@link IMessage} object as payload.
	 */
	public static final String EVENT_DATA_SEND = "EVENT_DATA_SEND";
	/**
	 * Published when data has been received from the hardware device.
	 * The event usually has a byte[] or an {@link IMessage} object as payload.
	 */
	public static final String EVENT_DATA_RECEIVED = "EVENT_DATA_RECEIVED";
	/**
	 * Published whenever an error during the communication with the device occurs.
	 * Payload is an instance of {@link DeviceResponse} containing the error string.
	 */
	public static final String EVENT_ERROR_OCCURRED = "EVENT_ERROR_OCCURRED";
	
	
	/**
	 * States which the device can be in.
	 */
	public enum DeviceState
	{
		/** The device is in this state after creation. The device is idle and does not try to connect to the hardware. */
		NOT_INITIALIZED(false, false),
		/**
		 * The device is in this state after calling {@link IDevice#activateDevice()}. The device now tries to connect to the
		 * hardware.
		 */
		INITIALIZED(true, false),
		/** In this state the device has successfully connected to the hardware (i.e. handshake was a success, health check is ok). */
		CONNECTED(true, true),
		/**
		 * This state is only reached when the device got an error after being {@link DeviceState#INITIALIZED}. The state is
		 * equal to {@link DeviceState#INITIALIZED}. The device occasionally tries to reestablish a working connection to the hardware.
		 */
		FAULTY(true, false);
		
		public final boolean initialized;
		public final boolean connected;
		
		private DeviceState(boolean initialized, boolean connected)
		{
			this.initialized = initialized;
			this.connected   = connected;
		}
	}
	

	/**
	 * Initializes the device. Must be called after the device has been created or its parameters have been updated.
	 * Only if the network stack could be initialized the device ({@link DeviceState#initialized} is true).
	 * @throws DeviceException If the device could not be initialized.
	 */
	public void activateDevice() throws DeviceException;

	/**
	 * @return The current state of the device. See state description for details.
	 */
	public @NotNull DeviceState getDeviceState();

	/**
	 * Method to process a request several times. The request is executed in serial and
	 * returned responses are added to a list.
	 * However, if one of the returned responses has something else than state {@link Status#OK} or
	 * {@link Status#OK_DATA} then the batch processing is stopped. All previous responses are discarded
	 * and a list with a single response is returned where the response contains the "error" or problem.
	 * @param batch The number of times the request should be processed.
	 * @param params The parameters of the request.
	 * @return A list of responses (where the size matches the batch parameter) or a list with a single response 
	 * describing the problem why the batch processing was stopped.
	 */
	public @NotNull DeviceResponse batchProcess(int batch, Object... params);

	/**
	 * Executes a single request and returns a response with the result.
	 * @param params The parameters of the request.
	 * @return A response object with the result of the request execution.
	 */
	public @NotNull DeviceResponse process(Object... params);

	/**
	 * Does a shutdown of the device, i.e. if connected, a disconnect from the hardware is done.
	 * Furthermore, the protocol is shutdown, such that the device goes into {@link DeviceState#NOT_INITIALIZED}.
	 */
	public void deactivateDevice();
	
	/**
	 * Returns the complete configuration of the device. This includes default values from the network stack.
	 * @return The complete configuration.
	 */
	public Map<String, String> getConfiguration();

}