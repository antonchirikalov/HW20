package com.toennies.ci1429.app.restcontroller;

import java.util.List;

import javax.usb.UsbDevice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.toennies.ci1429.app.services.usb.UsbDeviceDetection;

@RestController
@RequestMapping("/devices")
public class DeviceDetectionRestController {

	@Autowired
	@Qualifier("usbDeviceDetectionImpl")
	private UsbDeviceDetection usbDeviceDetection;

	@RequestMapping(value = "/blockingusbdetection", method = RequestMethod.GET)
	public @ResponseBody UsbDevice getBlockingUSBDetection() {
		return usbDeviceDetection.detectUsb();
	}

	@RequestMapping(value = "/listusb", method = RequestMethod.GET)
	public @ResponseBody List<UsbDevice> getListUsbDevices() {
		return usbDeviceDetection.listUsbDevices();
	}

}
