package com.toennies.ci1429.app.restcontroller;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.toennies.ci1429.app.model.DeviceException;

/**
 * Transforms {@link DeviceException} objects to {@link DeviceExceptionModel}.
 * 
 * See also {@link DeviceExceptionModel} and {@link DeviceException}.
 */
@ControllerAdvice
public class DeviceExceptionAdvice {

	private final static Logger logger = LogManager.getLogger();

	@ExceptionHandler(value = Throwable.class)
	private ResponseEntity<DeviceExceptionModel> handleException(HttpServletRequest request, Throwable t) {
		// First: we have to catch Throwable, too. Normally this is done by
		// com.toennies.ci1515.lib.restcontroller.RestControllerErrorAdvice. But
		// this class encrypts the Throwable information. And this is a problem,
		// because HW20 instances doesn't have plain text passwort for
		// encrypting information.
		logger.error("Caught Throwable", t);
		return new ResponseEntity<DeviceExceptionModel>(new DeviceExceptionModel(t), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = DeviceException.class)
	private ResponseEntity<DeviceExceptionModel> handleException(HttpServletRequest request, DeviceException de) {
		logger.error("Caught DeviceException: ", de);
		return new ResponseEntity<DeviceExceptionModel>(new DeviceExceptionModel(de), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
