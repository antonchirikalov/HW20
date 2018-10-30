package com.toennies.ci1429.app.network.parameter.typeinformation;

import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;

/**
 * This validator validates boolean values. It uses the logic of {@link Boolean#parseBoolean(String)}.
 * Therefore, every possible value is valid!
 * Example {@link Parameter} attributes for this validator.
 * 
 * <pre>
 * typeInformation="boolean", value="true"
 * typeInformation="boolean", value="false"
 * typeInformation="boolean", value="True"
 * typeInformation="boolean", value="TRUE"
 * </pre>
 */
public class BooleanTypeValidator implements ITypeInformationValidator
{
	
	static final BooleanTypeValidator INSTANCE = new BooleanTypeValidator();


	private BooleanTypeValidator()
	{
		//do nothing - default constructor just for the validation framework
	}


	/**
	 * Every value can be validated (and is valid), according to {@link Boolean#parseBoolean(String)}.
	 */
	@Override
	public ValidationResult validate(String value2Validate) throws IllegalArgumentException
	{
		return ValidationResult.OK;
	}

	@Override
	public InputType getInputType()
	{
		return InputType.BOOLEAN;
	}
	
}
