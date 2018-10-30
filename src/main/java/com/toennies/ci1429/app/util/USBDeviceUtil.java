package com.toennies.ci1429.app.util;

import java.util.ArrayList;
import java.util.List;

import javax.usb.UsbConfiguration;
import javax.usb.UsbDevice;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbInterface;
import javax.usb.UsbPipe;
import javax.usb.UsbServices;
import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;
import javax.usb.event.UsbPipeListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1574.lib.helper.Generics;


public class USBDeviceUtil {

	private final static Logger LOGGER = LogManager.getLogger();
	
	/**
	 * For debugging only
	 */
	private static final void listenOnAllUSBDevices()
	{
		for (UsbDevice device : getAttatchesUSBDevices())
		{
			List<UsbConfiguration> configs = device.getUsbConfigurations();
			for (UsbConfiguration config  : configs)
			{
				List<UsbInterface> ifaces = config.getUsbInterfaces();
				for (UsbInterface iface : ifaces)
				{
					List<UsbEndpoint> points = iface.getUsbEndpoints();
					for (UsbEndpoint point : points)
					{
	//					iface.claim();
						UsbPipe pipe = point.getUsbPipe();
						pipe.addUsbPipeListener(new UsbPipeListener()
						{
							
							@Override
							public void errorEventOccurred(UsbPipeErrorEvent event)
							{
								UsbException error = event.getUsbException();
								LOGGER.error(error);
							}
							
							@Override
							public void dataEventOccurred(UsbPipeDataEvent event)
							{
								byte[] data = event.getData();
								LOGGER.info(ASCII.formatHuman(data));
							}
						});
					}
				}
			}
		}
	}

	public static final int getNumberOfCurrentlyAttachedUSBDevices() {

		int numberOfCureentlyAttachedUSBDevices = getAttatchesUSBDevices().size();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Currently attached USB devices: {}", numberOfCureentlyAttachedUSBDevices);
		}

		return numberOfCureentlyAttachedUSBDevices;
	}

	/**
	 * Taken from here:
	 * https://github.com/usb4java/usb4java-javax-examples/blob/master/src/main/
	 * java/org/usb4java/javax/examples/DumpDeviceTree.java
	 *
	 */
	public static final List<UsbDevice> getAttatchesUSBDevices() {
		List<UsbDevice> usbdevices = new ArrayList<>();
		try {
			// ((org.usb4java.javax.Services)
			// UsbHostManager.getUsbServices()).scan();
			UsbServices services = UsbHostManager.getUsbServices();
			dump(usbdevices, services.getRootUsbHub(), 0);
		} catch (SecurityException | UsbException e) {
			LOGGER.error("Error during returning attached usb devices list. May return a incomplete device list!", e);
		}

		return usbdevices;
	}

	private static final List<UsbDevice> dump(List<UsbDevice> list, UsbDevice device, int level) {
		for (int i = 0; i < level; i += 1) {
			list.add(device);
		}
		if (device.isUsbHub()) {
			final UsbHub hub = (UsbHub) device;
			
			for (UsbDevice child : Generics.<List<UsbDevice>> convertUnchecked(hub.getAttachedUsbDevices())) {
				dump(list, child, level + 1);
			}
		}
		return list;
	}

	public static UsbDevice returnDeviceByDeviceId(String deviceIdString) {
		List<UsbDevice> attatchesUSBDevices = getAttatchesUSBDevices();
		for (UsbDevice ud : attatchesUSBDevices) {
			if (deviceIdString.equalsIgnoreCase(ud.toString())) {
				return ud;
			}
		}
		return null;
	}

	private USBDeviceUtil() {
		// no instance
	}
}
