package com.toennies.ci1429.app.services.usb;

import java.util.List;

import javax.usb.UsbDevice;

public interface UsbDeviceDetection {

	UsbDevice detectUsb();

	List<UsbDevice> listUsbDevices();

}
