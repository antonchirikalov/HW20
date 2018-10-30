package com.toennies.ci1429.app.services.usb;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.usb.UsbDevice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.toennies.ci1429.app.usbdevicedetection.detectionstrategies.PluginDetection;
import com.toennies.ci1429.app.util.USBDeviceUtil;

@Component
public class UsbDeviceDetectionImpl implements UsbDeviceDetection {

	private final static Logger logger = LogManager.getLogger();

	@Value("${usbdetection.plugindetection.schedulerrate}")
	private long period;

	@Value("${usbdetection.plugindetection.timeout}")
	private int timeout;

	@Override
	public UsbDevice detectUsb() {
		try {
			return PluginDetection.detect(period, timeout);
		} catch (IOException | TimeoutException e) {
			logger.error("Error during usb detection", e);
			return null;
		}
	}

	@Override
	public List<UsbDevice> listUsbDevices() {
		return USBDeviceUtil.getAttatchesUSBDevices();
	}

}
