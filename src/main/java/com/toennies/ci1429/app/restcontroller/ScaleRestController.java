/**
 * 
 */
package com.toennies.ci1429.app.restcontroller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.ResponseFormat;
import com.toennies.ci1429.app.model.scale.Commands.Command;
import com.toennies.ci1429.app.model.scale.Scale;
import com.toennies.ci1429.app.model.scale.Scale.Unit;
import com.toennies.ci1429.app.model.scale.WeightData;
import com.toennies.ci1429.app.model.scale.WeightDataFormatter;
import com.toennies.ci1429.app.util.RestControllerUtil;

/**
 * @author renkenh
 *
 */
@RestController
@RequestMapping("/devices")
public class ScaleRestController extends ADeviceSpecificRestController
{
	
	public ScaleRestController()
	{
		super(DeviceType.SCALE);
	}


	@RequestMapping(value="/{deviceID}/send", method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getSend(@PathVariable("deviceID") int deviceID,
									 @RequestParam("cmd") Command cmd,
									 @RequestParam(value="precision", required=false, defaultValue="3") Integer precision,
									 @RequestParam(value="response", required=false, defaultValue="EAN128") ResponseFormat responseFormat,
									 @RequestParam(value="unit", required=false, defaultValue="KG") Unit unit,
									 @RequestParam(value="value", required=false) Double value)
	{
		DeviceResponse response = this.processRequest(deviceID, cmd, value);
		ResponseEntity<?> entity = RestControllerUtil.map(response);
		if (entity != null)
			return entity;

		precision = precision != null ? precision.intValue() : 3;
		responseFormat = responseFormat != null ? responseFormat : ResponseFormat.EAN128;
		unit = unit != null ? unit : Unit.KG;
		
		WeightData data = response.getPayload();
		Scale.scaleLogger.info("Scale: {} REST API: {}", deviceID, WeightDataFormatter.formatWeightData(data, ResponseFormat.HUMAN, 2, Unit.KG));
		return map(response, precision, responseFormat, unit);
	}

	private static ResponseEntity<?> map(DeviceResponse protocolResult, int precision, ResponseFormat format, Unit unit)
	{
		WeightData data = protocolResult.getPayload();
		Map<String, String> formatted = WeightDataFormatter.formatWeightData(data, format, precision, unit);
		return new ResponseEntity<>(formatted, HttpStatus.OK);
	}


}
