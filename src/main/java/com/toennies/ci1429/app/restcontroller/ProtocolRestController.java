/**
 * 
 */
package com.toennies.ci1429.app.restcontroller;

import java.util.Collection;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.toennies.ci1429.app.network.parameter.ParameterInstance;
import com.toennies.ci1429.app.services.parameter.ParameterService;

/**
 * @author renkenh
 *
 */
@RestController
@RequestMapping("/protocols")
public class ProtocolRestController
{

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<Collection<ParameterInstance>> getProtocols()
	{
		return new ResponseEntity<Collection<ParameterInstance>>(ParameterService.getProtocolParameterInstances(),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/{protocolClass}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<ParameterInstance> getProtocol(@PathVariable("protocolClass") String protocolClass)
	{
		ParameterInstance foundProtocol = ParameterService.getProtocolClassByName(protocolClass);
		if (foundProtocol != null)
		{
			return new ResponseEntity<>(foundProtocol, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

}
