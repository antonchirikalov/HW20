package com.toennies.ci1429.app.hw10.processing.devices.scanner.functions;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vaadin.spring.events.Event;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventBusListener;

import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.IDevice.DeviceState;
import com.toennies.ci1429.app.model.scanner.Scanner;
import com.toennies.ci1429.app.services.devices.IDevicesService;

/**
 * 
 * @author Kai Stenzel, Alban Maxhuni
 * 
 *         This class is for managing the process of receiving and processing
 *         scan responses. Therefore it holds a {@link ScannerResponseRequester}
 *         for each {@link Scanner} a {@link IScanResponseListener} wants to
 *         listen to.
 *         <p>
 *         It is also the interface between to the
 *         {@link ScannerResponseRequester}s so when a
 *         {@link IScanResponseListener} should be added or removed it is done
 *         here.
 *         <p>
 *         In addition to that, this also takes care of important Events
 *         regarding the Scanning process in the HW10 Integration. It takes care
 *         of those to inform the {@link IScanResponseListener}s when there was
 *         an shutdown or disconnect of the Scanner which is unexpected for the
 *         {@link IScanResponseListener}
 *
 */
@Component
@SuppressWarnings("serial")
public class HW10ScanRegistry implements EventBusListener<Object>
{

	@Autowired
	private IDevicesService service;

	@Autowired
	private EventBus.ApplicationEventBus eventBus;

	private final Map<Integer, ScannerResponseRequester> scanners = new HashMap<Integer, ScannerResponseRequester>();

	@PostConstruct
	void init()
	{
		eventBus.subscribe(this);
	}

	/**
	 * This method is invoked for adding ScanResponseListeners to the registry
	 * based on the device id param.
	 * <p>
	 * This method creates a new {@link ScannerResponseRequester} when this
	 * listener is the first one for the specified Scanner. Otherwise it adds
	 * this listener to the existing {@link ScannerResponseRequester}.
	 * <p>
	 * There can only be as many instances of an
	 * {@link ScannerResponseRequester} as there are Scanners plugged in to the
	 * PC.
	 * 
	 * 
	 * @param listener
	 *            the {@link IScanResponseListener} that should be added to the
	 *            Scan Registry
	 * @param deviceId
	 *            an Integer stating the device id of the Scanner the
	 *            {@link IScanResponseListener} wants to listen to.
	 * 
	 */
	public synchronized void addScanListener(ScanResponseListener listener, int deviceId)
	{
		ScannerResponseRequester requester = this.startResponseRequester(deviceId);
		requester.addListener(listener);
	}
	
	private synchronized ScannerResponseRequester startResponseRequester(int deviceId)
	{
		ScannerResponseRequester requester = this.scanners.get(deviceId);
		if (requester == null)
		{
			IDevice device = this.service.getDeviceById(deviceId);
			requester = new ScannerResponseRequester((Scanner) device);
			this.scanners.put(deviceId, requester);
		}
		return requester;
	}

	private synchronized ScannerResponseRequester getResponseRequester(int deviceId)
	{
		return this.scanners.get(deviceId);
	}
	

	/**
	 * This method can is invoked to remove a ScanResponseListener without
	 * knowing the device it is currently listening to.
	 * <p>
	 * It basically iterates over all {@link ScannerResponseRequester} and
	 * removes the {@link IScanResponseListener} if it is stored in that.
	 * <p>
	 * There can only be as many instances of an
	 * {@link ScannerResponseRequester} as there are Scanners plugged in to the
	 * PC.
	 * 
	 * @param id
	 *            the identifier of a {@link IScanResponseListener} as a
	 *            {@link String} value
	 */
	public synchronized void removeScanListenerById(String clientId)
	{
		scanners.forEach((deviceId, listener) ->
		{
			this.removeScanListener(clientId, deviceId);
		});
	}

	/**
	 * This method can is invoked to remove a ScanResponseListener knowing the
	 * device it is currently listening to.
	 * 
	 * @param id
	 *            the identifier of a {@link IScanResponseListener} as a
	 *            {@link String} value
	 * @param deviceId
	 *            the unique id of the Scanner the {@link IScanResponseListener}
	 *            is listening to
	 */
	public void removeScanListener(String clientId, int deviceId)
	{
		ScannerResponseRequester scannerResponseRequester = this.getResponseRequester(deviceId);
		if (scannerResponseRequester != null)
			scannerResponseRequester.removeListenerById(clientId);
	}

	/**
	 * This handles the events which are important for the ScanRegistry.
	 * Important event topics are:
	 * <ul>
	 * <li>{@link IDevicesService#EVENT_DEVICE_DELETED}</li>
	 * <li>{@link IDevice#EVENT_STATE_CHANGED} with device state
	 * {@link DeviceState#NOT_INITIALIZED}</li>
	 * </ul>
	 * 
	 * <p>
	 * These are important in case there is a HW10 client listening for a scan
	 * response of a certain scanner and this scanner is disconnected or deleted
	 * via the web UI of HW2.0. When the client did not turn off the Scanner it
	 * is still waiting for a response but it will not get one. To avoid this
	 * the {@link ScannerResponseRequester} is removed and a NAK is sent to the
	 * client.
	 */
	@Override
	public void onEvent(Event<Object> event)
	{
		if (!(event.getPayload() instanceof IDevice))
			return;

		switch (event.getTopic())
		{
			case IDevicesService.EVENT_DEVICE_DELETED:
				this.handleDeleteEvent((IDevice) event.getPayload());
				return;
		}
	}
	
	private void handleDeleteEvent(IDevice device)
	{
		if (device.getType() == DeviceType.SCANNER)
			this.shutdownResponseRequester(device.getDeviceID());
	}

	/**
	 * Stops the thread listening to a response of the scanner specified by
	 * device by the {@link ScannerResponseRequester#shutdown()}
	 * <p>
	 * In addition it removes the {@link ScannerResponseRequester}.
	 * 
	 * 
	 * @param deviceId
	 *            the number identifieng the Scanner which has been shut down
	 */
	private synchronized void shutdownResponseRequester(int deviceId)
	{
		ScannerResponseRequester requester = this.scanners.remove(deviceId);
		if (requester != null)
			requester.shutdown();
	}

}
