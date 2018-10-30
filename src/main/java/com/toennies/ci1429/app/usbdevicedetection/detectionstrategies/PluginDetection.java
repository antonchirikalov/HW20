package com.toennies.ci1429.app.usbdevicedetection.detectionstrategies;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import javax.usb.UsbDevice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.util.Compute;
import com.toennies.ci1429.app.util.IExecutors;
import com.toennies.ci1429.app.util.USBDeviceUtil;

/**
 * Device detection based on a plugin detection approach.
 */
public class PluginDetection
{
	
	private final static Logger LOGGER = LogManager.getLogger();

	
	private static final class DetectionTask implements Runnable
	{
		
		private List<UsbDevice> latestUSBDevices;
		

		public DetectionTask()
		{
			latestUSBDevices = USBDeviceUtil.getAttatchesUSBDevices();
			LOGGER.debug("Number of initial usb devices {}", latestUSBDevices.size());
		}

	
		@Override
		public void run()
		{
			try
			{
				// Check if the number if _currently_ attached devices differs
				// from initially plugged in devices ...
				List<UsbDevice> currentlyAttachedDevices = USBDeviceUtil.getAttatchesUSBDevices();
				if (latestUSBDevices.size() >= currentlyAttachedDevices.size())
				{
					this.latestUSBDevices = currentlyAttachedDevices;
					LOGGER.debug("No device detected yet");
					return;
				}

				LOGGER.info("New usb device detected");

				// Search for newly plugged device
				for (UsbDevice usbDevice : USBDeviceUtil.getAttatchesUSBDevices()) {
					if (!latestUSBDevices.contains(usbDevice)) {
						// if device not in initially device list, it's the
						// recently added device!
						finishDetection(usbDevice);
					}
				}
			}
			catch (Exception e)
			{
				finishDetection(e);
			}
		}
		
	}

	private static final Set<Compute> COMPUTES = new HashSet<>();
	private static final ReentrantLock COMPUTE_LOCK = new ReentrantLock();

	private static ScheduledExecutorService CURRENT_EXECUTOR;
	private static DetectionTask CURRENT_TASK;

	
	public static final UsbDevice detect(long period, int timeout) throws IOException, TimeoutException
	{
		Compute compute = new Compute();
		initDetection(compute, period);

		try
		{
			return compute.get(timeout);
		}
		catch (TimeoutException toe)
		{
			cancelDetection(compute);
			throw toe;
		}
		catch (IOException ioe)
		{
			cancelDetection(compute);
			throw ioe;
		}
		catch (Exception e)
		{
			cancelDetection(compute);
			throw new IOException(e);
		}
	}
	
	private static final void initDetection(Compute compute, long period)
	{
		COMPUTE_LOCK.lock();
		try
		{
			COMPUTES.add(compute);
			if (CURRENT_EXECUTOR == null)
			{
				CURRENT_TASK = new DetectionTask();
				// Through ScheduledExecutorService the following Runnable object is
				// executed at a fixed rate
				CURRENT_EXECUTOR = Executors.newScheduledThreadPool(1, IExecutors.NETWORK_FACTORY);
				CURRENT_EXECUTOR.scheduleAtFixedRate(CURRENT_TASK, 0l /* start delay */, period /* duration */, TimeUnit.MILLISECONDS);
			}
		}
		finally
		{
			COMPUTE_LOCK.unlock();
		}
	}
	
	private static final void finishDetection(Object result)
	{
		COMPUTE_LOCK.lock();
		try
		{
			for (Compute compute : COMPUTES)
				compute.put(result);
			COMPUTES.clear();
			CURRENT_TASK = null;
			CURRENT_EXECUTOR.shutdown();
			CURRENT_EXECUTOR = null;
		}
		finally
		{
			COMPUTE_LOCK.unlock();
		}
	}
	
	private static final void cancelDetection(Compute compute)
	{
		COMPUTE_LOCK.lock();
		try
		{
			COMPUTES.remove(compute);
			if (COMPUTES.isEmpty())
			{
				CURRENT_TASK = null;
				CURRENT_EXECUTOR.shutdown();
				CURRENT_EXECUTOR = null;
			}
		}
		finally
		{
			COMPUTE_LOCK.unlock();
		}
	}

}
