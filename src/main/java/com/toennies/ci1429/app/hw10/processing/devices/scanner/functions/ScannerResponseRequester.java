package com.toennies.ci1429.app.hw10.processing.devices.scanner.functions;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceResponse.Status;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.IDevice.DeviceState;
import com.toennies.ci1429.app.model.ResponseFormat;
import com.toennies.ci1429.app.model.scanner.Scanner;
import com.toennies.ci1429.app.network.event.IEventHandler;

/**
 * 
 * @author Kai Stenzel
 * 
 *         An instance of this class is used for requesting a response or a
 *         scanned barcode label by a certain {@link Scanner}.
 *         <p>
 *         It also takes care of the {@link IScanResponseListener}s waiting for
 *         responses of this {@link Scanner} by storing these and dispatching
 *         the results.
 *
 */
public class ScannerResponseRequester implements IEventHandler
{
	private final class ScanResponseThread extends Thread
	{

		private volatile boolean isShutdown = false;

		{
			this.setDaemon(true);
		}
		
		@Override
		public void run()
		{
			while (!this.isShutdown && isRunning())
			{
				DeviceResponse response = device.process(ResponseFormat.STRING);
				if (response.getStatus() == Status.OK_DATA)
					ScannerResponseRequester.this.sendMessageToListeners(response);
			}
		}

		public void shutdown()
		{
			this.isShutdown = true;
			this.interrupt();
		}
	}


	private final ReentrantLock listenerLock = new ReentrantLock();
	private final HashMap<String, ScanResponseListener> listeners = new HashMap<>();
	private final IDevice device;
	private final ReentrantLock threadLock = new ReentrantLock();
	private ScanResponseThread responseThread;


	/**
	 * This constructor creates a new instance of a
	 * {@link ScannerResponseRequester}. Additionally it initiates and starts a
	 * Thread which is listening for scan responses of the scanner specified by
	 * the deviceId param. The requesting is done through the specified
	 * {@link ScannerModelImpl}
	 * 
	 * @param scannerModel
	 * @param deviceId
	 */
	public ScannerResponseRequester(IDevice device)
	{
		this.device = device;
		this.device.registerEventHandler(this);
	}

	/**
	 * This adds a {@link IScanResponseListener} to this
	 * {@link ScannerResponseRequester}.
	 * <p>
	 * If a response is received by the scanner this object will dispatch it to
	 * the added {@link IScanResponseListener}.
	 * <p>
	 * the listener is mapped as a value to its ID (received by the
	 * {@link IScanResponseListener#getID()} method, which makes it accessible
	 * in a very conveniant way.
	 * 
	 * @param listener
	 *            an {@link IScanResponseListener} instance which listens to the
	 *            result of the Scanner this {@link ScannerResponseRequester} is
	 *            requesting
	 */
	public void addListener(ScanResponseListener listener)
	{
		this.listenerLock.lock();
		try
		{
			this.listeners.put(listener.getID(), listener);
		}
		finally
		{
			this.listenerLock.unlock();
		}
		this.checkStart();
	}

	/**
	 * Sends the specified message to all clients which are registered in this
	 * {@link ScannerResponseRequester} instance
	 * 
	 * @param message
	 *            a {@link String} containing the message which should be sent
	 *            to the {@link IScanResponseListener}
	 */
	private void sendMessageToListeners(DeviceResponse message)
	{
		this.listenerLock.lock();
		try
		{
			this.listeners.forEach((id, listener) -> listener.onScanResponse(message));
		}
		finally
		{
			this.listenerLock.unlock();
		}
	}

	/**
	 * Removes a {@link IScanResponseListener} by its id.
	 * <p>
	 * if the {@link IScanResponseListener} with the specified id is not
	 * contained in this {@link ScannerResponseRequester} instance, nothing
	 * happens.
	 * 
	 * @param id
	 *            identifier for the {@link IScanResponseListener} which should
	 *            be removed
	 */
	public void removeListenerById(String id)
	{
		this.listenerLock.lock();
		try
		{
			this.listeners.remove(id);
		}
		finally
		{
			this.listenerLock.unlock();
		}
		this.checkStop();
	}
	
	private void checkStart()
	{
		this.threadLock.lock();
		try
		{
			if (this.isRunning() && this.responseThread == null)
			{
				this.responseThread = new ScanResponseThread();
				this.responseThread.start();
			}
		}
		finally
		{
			this.threadLock.unlock();
		}
	}
	
	private boolean isRunning()
	{
		return this.device.getDeviceState() == DeviceState.CONNECTED && !this.listeners.isEmpty();
	}

	private void checkStop()
	{
		this.threadLock.lock();
		try
		{
			if (!this.isRunning() && this.responseThread != null)
			{
				this.responseThread.shutdown();
				this.responseThread = null;
			}
		}
		finally
		{
			this.threadLock.unlock();
		}
	}
	
	/**
	 * This stops the processor searching for scan responses. This sets an
	 * interrupt flag on the Thread running.
	 *
	 */
	public void shutdown()
	{
		this.device.unregisterEventHandler(this);
		this.listenerLock.lock();
		try
		{
			this.listeners.clear();
		}
		finally
		{
			this.listenerLock.unlock();
		}
		this.checkStop();
	}

	@Override
	public void handleEvent(String eventID, Object source, Object... params)
	{
		if (IDevice.EVENT_STATE_CHANGED.equals(eventID))
		{
			this.checkStart();
			this.checkStop();
		}
	}

}