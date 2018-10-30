package com.toennies.ci1429.app.restcontroller;

import java.util.Collection;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.network.protocol.sps.RawSpsRequest;
import com.toennies.ci1429.app.util.RestControllerUtil;

@RestController
@RequestMapping("/devices")
public class SpsRestController extends ADeviceSpecificRestController
{

	protected SpsRestController()
	{
		super(DeviceType.SPS);
	}


	@RequestMapping(value = "/{deviceID}/execute", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> putSpsCommand(@PathVariable("deviceID") int deviceID, @RequestParam("cmd") String command, @RequestBody(required=false) Map<String, Object> body)
	{
		DeviceResponse processRequest = this.processRequest(deviceID, new RawSpsRequest(command, body));
		ResponseEntity<?> entity = RestControllerUtil.map(processRequest);
		if (entity != null)
			return entity;

		return new ResponseEntity<>(mapPayload(processRequest), RestControllerUtil.mapStatus(processRequest));
	}

	
	private static final Object mapPayload(DeviceResponse response)
	{
		Object payload = response.getPayload();
		if (payload instanceof Collection)
			return ((Collection<?>) payload).toArray();
		return payload;
	}
}
