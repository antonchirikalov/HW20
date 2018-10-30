/**
 * 
 */
package com.toennies.ci1429.app.restcontroller;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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
import com.toennies.ci1429.app.util.RestControllerUtil;
import com.toennies.ci1429.app.util.ScannerUtil;


/**
 * @author renkenh
 *
 */
@RestController
@RequestMapping("/devices")
public class ScannerRestController extends ADeviceSpecificRestController
{
	
	/** Default value when no batch parameter is given in request. */
	public static final String PARAM_DEFAULT_BATCH_VALUE = "1";
	/** Integer value of {@link #PARAM_DEFAULT_BATCH_VALUE}. */
	public static final int DEFAULT_BATCH_VALUE = Integer.parseInt(PARAM_DEFAULT_BATCH_VALUE);
	

	public ScannerRestController()
	{
		super(DeviceType.SCANNER);
	}


	@RequestMapping(value="/{deviceID}/scan", method=RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getScan(@PathVariable("deviceID") int deviceID,
			@RequestParam(value="response", required=false) String response,
			@RequestParam(value = "batch", required = false, defaultValue=ScannerRestController.PARAM_DEFAULT_BATCH_VALUE) int batch)
	{
		ResponseFormat responseFormat = valueOfResponseFormat(response);
		if (batch == 1)
			return map(this.processRequest(deviceID, responseFormat));
		
		return mapBatch(this.batchProcessRequest(deviceID, batch, responseFormat));
	}
	
	private static final ResponseEntity<?> map(DeviceResponse response)
	{
		ResponseEntity<?> entity = RestControllerUtil.map(response);
		if (entity != null)
			return entity;
		return new ResponseEntity<>(ScannerUtil.formatResponse(response.getPayload()), HttpStatus.OK);
	}

	private static final ResponseEntity<?> mapBatch(DeviceResponse response)
	{
		ResponseEntity<?> entity = RestControllerUtil.map(response);
		if (entity != null)
			return entity;
		List<Object> responses = response.getPayload();
		List<Object> formattedResponses = responses.stream().map((r) -> ScannerUtil.formatResponse(r)).collect(Collectors.toList());
		return new ResponseEntity<>(formattedResponses, HttpStatus.OK);
	}

	private static final ResponseFormat valueOfResponseFormat(String response)
	{
		if (StringUtils.isBlank(response))
			return null;	//default response
		return ResponseFormat.valueOf(response);
	}

}
