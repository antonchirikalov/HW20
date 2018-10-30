package com.toennies.ci1429.app.restcontroller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.toennies.ci1429.app.model.WhoAmI;

@RestController
@RequestMapping("/whoami")
public class WhoAmIRestController {

	/**
	 * Returns the ip adress of client invoking this endpoint.
	 * 
	 * Attention: you have to call this like http://rhe3759:8080/.../whoami/
	 * 
	 * If you call it like http://localhost:8080/.../whoami/ you don't get the
	 * proper ip adress.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public @ResponseBody WhoAmI getWhoAmI(HttpServletRequest request) {
		return new WhoAmI(request.getRemoteAddr());
	}

}
