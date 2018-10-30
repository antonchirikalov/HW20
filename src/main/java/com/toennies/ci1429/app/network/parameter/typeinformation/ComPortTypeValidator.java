package com.toennies.ci1429.app.network.parameter.typeinformation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fazecast.jSerialComm.SerialPort;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;

/**
 * Example {@link Parameter} attributes for this validator. No analysis of
 * typeinformation. Valid com-ports are calculated at run time.
 * 
 * <pre>
 * 
 * value="COM1", typeInformation="java:com.toennies.ci1429.app.net.params.typeinformation.ComPortValidator"
 * </pre>
 */
public class ComPortTypeValidator implements IListTypeInformationValidator
{

	public ComPortTypeValidator()
    {
		// default constructor - needed for Validators Framework
    }

	@Override
	public ValidationResult validate(String value2Validate) throws IllegalArgumentException
	{
		boolean isValid = this.getValidValues().stream()
					 			.filter((name) -> name.equals(value2Validate))
					 			.findAny()
					 			.isPresent();
		if (isValid)
			return ValidationResult.OK;
		return new ValidationResult("The given RS232 port " + value2Validate + " is not a available on this system.");
	}


	@Override
	public List<String> getValidValues()
	{
		return Arrays.stream(SerialPort.getCommPorts()).map(SerialPort::getSystemPortName).collect(Collectors.toList());
	}


}
