/**
 * 
 */
package com.toennies.ci1429.app.restcontroller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.toennies.ci1429.app.model.DeviceException;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.IDeviceDescription;
import com.toennies.ci1429.app.model.printer.Printer;
import com.toennies.ci1429.app.model.scale.Scale;
import com.toennies.ci1429.app.model.scanner.Scanner;
import com.toennies.ci1429.app.model.sps.Sps;
import com.toennies.ci1429.app.model.watcher.Watcher;
import com.toennies.ci1429.app.repository.DeviceDescriptionEntity;
import com.toennies.ci1429.app.restcontroller.hateos.HateosDevice;
import com.toennies.ci1429.app.restcontroller.hateos.HateosPrinter;
import com.toennies.ci1429.app.restcontroller.hateos.HateosScale;
import com.toennies.ci1429.app.restcontroller.hateos.HateosScanner;
import com.toennies.ci1429.app.restcontroller.hateos.HateosSps;
import com.toennies.ci1429.app.restcontroller.hateos.HateosWatcher;
import com.toennies.ci1429.app.services.devices.DevicesService;

/**
 * @author renkenh
 *
 */
@RestController
@RequestMapping("/devices")
public class DevicesRestController
{
	@Autowired
	private DevicesService devicesService;

	@RequestMapping(method=RequestMethod.GET)
	@ResponseBody
	public HttpEntity<List<HateosDevice<?>>> getDevices()
	{
		List<HateosDevice<?>> list = devicesService.getAllDevices().stream().map((d) -> map(d)).collect(Collectors.toList());
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@RequestMapping(value="/{deviceID}", method=RequestMethod.GET)
	@ResponseBody
	public HttpEntity<HateosDevice<?>> getDevice(@PathVariable("deviceID") int deviceID)
	{
		IDevice device = devicesService.getDeviceById(deviceID);
		if (device == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<>(map(device), HttpStatus.OK);
	}
	

	@RequestMapping(method=RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<HateosDevice<?>> postDevice(@RequestBody DeviceDescriptionEntity desc) throws DeviceException
	{
		if (devicesService.deviceExistsWithID(desc.getDeviceID()))
			return new ResponseEntity<>(HttpStatus.CONFLICT);

		IDevice device = devicesService.createNewDevice(desc);
		return new ResponseEntity<>(map(device), HttpStatus.CREATED);
	}
	
	@RequestMapping(method=RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<HateosDevice<?>> putDevice(@RequestBody DeviceDescriptionEntity desc) throws DeviceException
	{
		return putDevice(desc.getDeviceID(), desc);
	}

	@RequestMapping(value="/{deviceID}", method=RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<HateosDevice<?>> putDevice(@PathVariable("deviceID") int deviceID, @RequestBody DeviceDescriptionEntity desc) throws DeviceException
	{
		// Without next line it would be possible to update the device id
		desc.setDeviceID(deviceID);
		IDevice device = devicesService.updateDevice(desc);
		if (device == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<>(map(device), HttpStatus.OK);
	}

	@RequestMapping(method=RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<HateosDevice<?>> deleteDevice(@RequestBody IDeviceDescription desc)
	{
		IDevice device = devicesService.deleteDevice(desc);
		if (device == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<>(map(device), HttpStatus.OK);
	}

	@RequestMapping(value="/{deviceID}", method=RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<HateosDevice<?>> deleteDevice(@PathVariable("deviceID") int deviceID)
	{
		IDevice device = devicesService.deleteDeviceById(deviceID);
		if (device == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return new ResponseEntity<>(map(device), HttpStatus.OK);
	}

	@RequestMapping(value="/{deviceID}/init", method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<HateosDevice<?>> getInitDevice(@PathVariable("deviceID") int deviceID) throws DeviceException
	{
		IDevice device = devicesService.getDeviceById(deviceID);
		if (device == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		device.activateDevice();
		return new ResponseEntity<>(map(device), HttpStatus.OK);
	}

	@RequestMapping(value="/{deviceID}/shutdown", method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<HateosDevice<?>> getShutdownDevice(@PathVariable("deviceID") int deviceID)
	{
		IDevice device = devicesService.getDeviceById(deviceID);
		if (device == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		device.deactivateDevice();
		return new ResponseEntity<>(map(device), HttpStatus.OK);
	}

	private static final HateosDevice<?> map(IDeviceDescription device)
	{
		switch (device.getType())
		{
			case PRINTER:
				return new HateosPrinter((Printer) device);
			case SCALE:
				return new HateosScale((Scale) device);
			case SCANNER:
				return new HateosScanner((Scanner) device);
			case WATCHER:
				return new HateosWatcher((Watcher) device);
			case SPS:
				return new HateosSps((Sps) device);
			default:
				throw new IllegalStateException();
		}
	}
}
