package com.toennies.ci1429.app.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.toennies.ci1429.app.services.devicetemplates.DeviceTemplateService;
import com.toennies.ci1429.app.services.devicetemplates.ITemplateDeviceDescription;

@RestController
@RequestMapping("/templates")
public class SyncDeviceTemplatesRestController
{

	@Autowired
	@Qualifier("deviceTemplateServiceImpl")
	private DeviceTemplateService deviceTemplateService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<List<ITemplateDeviceDescription>> getDevices() {
		return new ResponseEntity<>(deviceTemplateService.findAll(), HttpStatus.OK);
	}

	@RequestMapping(value = "/{templateID}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<ITemplateDeviceDescription> getDevice(@PathVariable("templateID") Integer templateID) {
		return new ResponseEntity<>(deviceTemplateService.findById(templateID), HttpStatus.OK);
	}

	@RequestMapping(value = "/syncTemplates", method = RequestMethod.POST)
	@ResponseBody
	public HttpEntity<List<ITemplateDeviceDescription>> postSyncTemplates() {
		boolean success = deviceTemplateService.syncTemplates();
		return new ResponseEntity<>(success ? HttpStatus.OK : HttpStatus.GATEWAY_TIMEOUT);
	}

}
