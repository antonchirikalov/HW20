package com.toennies.ci1429.app.usb;

import java.util.List;

import javax.usb.UsbConfiguration;
import javax.usb.UsbDevice;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbInterface;
import javax.usb.UsbPipe;
import javax.usb.event.UsbDeviceDataEvent;
import javax.usb.event.UsbDeviceErrorEvent;
import javax.usb.event.UsbDeviceEvent;
import javax.usb.event.UsbDeviceListener;
import javax.usb.event.UsbPipeDataEvent;
import javax.usb.event.UsbPipeErrorEvent;
import javax.usb.event.UsbPipeListener;

import com.toennies.ci1429.app.usbdevicedetection.detectionstrategies.PluginDetection;
import com.toennies.ci1429.app.util.ASCII;

public class USBChipreaderDemo
{


	public static final void main(String[] args) throws Exception
	{
		UsbDevice device = PluginDetection.detect(100l, 60);
		System.out.println(device.isConfigured());
		device.addUsbDeviceListener(new UsbDeviceListener()
		{
			
			@Override
			public void usbDeviceDetached(UsbDeviceEvent event)
			{
				System.out.println("Device Detached");
			}
			
			@Override
			public void errorEventOccurred(UsbDeviceErrorEvent event)
			{
		        UsbException error = event.getUsbException();
		        System.err.println(error);
			}
			
			@Override
			public void dataEventOccurred(UsbDeviceDataEvent event)
			{
		        byte[] data = event.getData();
		        System.out.println(ASCII.formatHuman(data));
			}
		});
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
							System.err.println(error);
						}
						
						@Override
						public void dataEventOccurred(UsbPipeDataEvent event)
						{
							byte[] data = event.getData();
							System.out.println(ASCII.formatHuman(data));
						}
					});
				}
			}
		}
		UsbConfiguration configuration = device.getActiveUsbConfiguration();
//		if (configuration != null)
//		{
//			System.out.println(ReflectionToStringBuilder.toString(configuration));
//			UsbInterface iface = configuration.getUsbInterface((byte) 1);
//			UsbEndpoint endpoint = iface.getUsbEndpoint((byte) 0x83);
//			UsbPipe pipe = endpoint.getUsbPipe();
//			pipe.addUsbPipeListener(new UsbPipeListener()
//			{
//				
//			    @Override
//			    public void errorEventOccurred(UsbPipeErrorEvent event)
//			    {
//			        UsbException error = event.getUsbException();
//			        System.err.println(error);
//			    }
//			    
//			    @Override
//			    public void dataEventOccurred(UsbPipeDataEvent event)
//			    {
//			        byte[] data = event.getData();
//			        System.out.println(Utils.formatHuman(data));
//			    }
//			});
//		}
		System.out.println("Waiting for data");
		Thread.sleep(1000 * 60 * 60); //sleep for one hour
	}

	
}
