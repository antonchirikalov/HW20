/**
 * 
 */
package com.toennies.ci1429.app.restcontroller;

import java.time.Instant;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.watcher.Watcher;
import com.toennies.ci1429.app.util.RestControllerUtil;


/**
 * Rest Controller for interaction with {@link Watcher} devices.
 * @author renkenh
 */
@RestController
@RequestMapping("/devices")
@EnableAutoConfiguration
public class WatcherRestController extends ADeviceSpecificRestController
{

	private static final String DEFAULT_PARAM_LOGSIZE = "-1";
	
	
	/**
	 * Constructor.
	 */
	public WatcherRestController()
	{
		super(DeviceType.WATCHER);
	}


	/**
	 * Activate a subsystem - subsystem has to be provided.
	 * @param deviceID The device ID of the watcher.
	 * @param system The subsystem to activate - may not be null.
	 * @return Whether the change could be made or not.
	 */
	@RequestMapping(value = "/{deviceID}/"+Watcher.CMD_ACTIVATE_SYSTEM, method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> putActivate(@PathVariable("deviceID") int deviceID, @RequestParam(value="system", required=true) String system)
	{
		return RestControllerUtil.map(this.processRequest(deviceID, Watcher.CMD_ACTIVATE_SYSTEM, system));
	}

	/**
	 * Deactivate a subsystem - subsystem has to be provided.
	 * @param deviceID The device ID of the watcher.
	 * @param system The subsystem to deactivate - may not be null.
	 * @return Whether the change could be made or not.
	 */
	@RequestMapping(value="/{deviceID}/"+Watcher.CMD_DEACTIVATE_SYSTEM, method=RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> putDeactivate(@PathVariable("deviceID") int deviceID, @RequestParam(value="system", required=true) String system)
	{
		return RestControllerUtil.map(this.processRequest(deviceID, Watcher.CMD_DEACTIVATE_SYSTEM, system));
	}

	/**
	 * Returns a full overview over a watcher. Makes a snapshot of the watcher and all of its subsystems.
	 * @param deviceID The device id of the watcher.
	 * @return A sorted list of subsystems. 
	 */
	@RequestMapping(value="/{deviceID}/fulloverview", method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getFullOverview(@PathVariable("deviceID") int deviceID)
	{
		IDevice device = devicesService.getDeviceById(deviceID);

		Watcher watcher = (Watcher) device;
		return new ResponseEntity<>(watcher.getSystemOverview(), HttpStatus.OK);
	}

	/**
	 * Returns the event log of the specified watcher.
	 * @param deviceID The device id of the watcher.
	 * @param size Restrict the returned size of the event log directly to a specific amount of events.
	 * @param time Restrict the returned size of the event log by specifying a point in time. Only newer events are returned.
	 * @return A list of events.
	 */
	@RequestMapping(value="/{deviceID}/eventlog", method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getEventlog(@PathVariable("deviceID") int deviceID,
			@RequestParam(value="size", required=false, defaultValue=WatcherRestController.DEFAULT_PARAM_LOGSIZE) int size,
			@RequestParam(value="from", required=false) Instant time)
	{
		IDevice device = devicesService.getDeviceById(deviceID);

		Watcher watcher = (Watcher) device;
		return new ResponseEntity<>(watcher.getEventLog(size, time), HttpStatus.OK);
	}

}
