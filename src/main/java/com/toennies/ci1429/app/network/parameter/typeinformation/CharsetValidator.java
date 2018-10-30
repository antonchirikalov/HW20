/**
 * 
 */
package com.toennies.ci1429.app.network.parameter.typeinformation;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;

/**
 * Example {@link Parameter} attributes for this validator. No analysis of
 * typeinformation. Valid charsets are calculated at run time.
 * 
 * <pre>
 * 
 * value="UTF-8", typeInformation="java:com.toennies.ci1429.app.network.parameter.typeinformation.CharsetValidator"
 * </pre>
 */
public class CharsetValidator implements IListTypeInformationValidator
{

    public CharsetValidator()
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
		return new ValidationResult("The given Charset " + value2Validate + " is not a available on this system.");
	}


	@Override
	public List<String> getValidValues()
	{
		return new ArrayList<>(Charset.availableCharsets().keySet());
	}

}
