/**
 * 
 */
package com.toennies.ci1429.app.restcontroller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
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
import com.toennies.ci1429.app.model.printer.LabelData;
import com.toennies.ci1429.app.model.printer.Preview;
import com.toennies.ci1429.app.util.RestControllerUtil;


/**
 * @author renkenh
 *
 */
@RestController
@RequestMapping("/devices")
@EnableAutoConfiguration
public class PrinterRestController extends ADeviceSpecificRestController
{

//	private static final Logger LOGGER = LogManager.getLogger();

	/** Default value when no batch parameter is given in request. */
	public static final String PARAM_DEFAULT_BATCH_VALUE = "1";
	/** Integer value of {@link #PARAM_DEFAULT_BATCH_VALUE}. */
	public static final int DEFAULT_BATCH_VALUE = Integer.parseInt(PARAM_DEFAULT_BATCH_VALUE);
	

	
	public PrinterRestController()
	{
		super(DeviceType.PRINTER);
	}


	/**
	 * Invoke this endpoint, if you want to print a label. Through batch
	 * parameter it's possible to print respective label as often as one likes
	 *
	 * Example urls:
	 * http://localhost:8080/devices/1/print?batch=12 prints 12 labels
	 *
	 * http://localhost:8080/devices/1/print prints only 1 label
	 *
	 *
	 * Example LabelData json
	 * <pre>
	 * {
	"templateFile" : "TEST2.ZPL",
	"fields" :
	{
		"11" : { "type" : "CHAR", "value": "11" },
		"22" : { "type" : "HEX", "value": "22" },
		"33" : "33"
	}
	 * }
	 * </pre>
	 *
	 * The 33 entry in map is of type CHAR
	 */
	@RequestMapping(value = "/{deviceID}/print", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> putPrintBatch(@PathVariable("deviceID") int deviceID,
			@RequestBody LabelData fieldData,
			@RequestParam(value = "batch", required = false, defaultValue = PARAM_DEFAULT_BATCH_VALUE) int batch)
	{
		if (batch == 1)
			return RestControllerUtil.map(this.processRequest(deviceID, fieldData));
		
		ResponseEntity<?> entity = RestControllerUtil.map(this.batchProcessRequest(deviceID, batch, fieldData));
		if (entity != null)
			return entity;
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Endpoint which can be used to preview label data.
	 * @param deviceID The device id.
	 * @param fieldData The actual label data.
	 * @return An image as a http response.
	 */
	@RequestMapping(value = "/{deviceID}/preview", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> putPreview(@PathVariable("deviceID") int deviceID, @RequestBody LabelData fieldData)
	{
		fieldData.setPreview(true);
		DeviceResponse response = this.processRequest(deviceID, fieldData);
		if (response.getPayload() instanceof Preview)
		{
			Preview preview = response.getPayload();
			//return special image entity
			return ResponseEntity.status(HttpStatus.OK).contentType(preview.type).body(preview.imageData);
		}
		return RestControllerUtil.map(response);
	}

	/**
	 * Endpoint for uploading a template to a zebra printer.
	 * 
	 * Important: this enpoint can also be used to send raw text to printers. Store raw text in body of request.
	 */
	@RequestMapping(value="/{deviceID}/upload", method=RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> putUpload(@PathVariable("deviceID") int deviceID, @RequestBody String template)
	{
		return RestControllerUtil.map(this.processRequest(deviceID, template));
	}

}
