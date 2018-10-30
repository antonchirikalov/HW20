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
@RequestMapping("/sockets")
public class SocketRestController
{

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<Collection<ParameterInstance>> getConnectors()
	{
		return new ResponseEntity<Collection<ParameterInstance>>(ParameterService.getSocketParameterInstances(),
				HttpStatus.OK);
	}

	@RequestMapping(value = "/{socketClass}", method = RequestMethod.GET)
	@ResponseBody
	public HttpEntity<ParameterInstance> getConnector(@PathVariable("socketClass") String socketClass)
	{
		ParameterInstance socket = ParameterService.getSocketClassByName(socketClass);
		if (socket != null)
		{
			return new ResponseEntity<>(socket, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

}
