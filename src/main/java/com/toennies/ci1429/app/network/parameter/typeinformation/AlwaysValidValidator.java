package com.toennies.ci1429.app.network.parameter.typeinformation;

/**
 * This validator is always valid. This can be used if no suitable validator can
 * be found. Only for internal purposes. Therefore no annotation.
 * The always valid validator can (more or less) mimic every validator.
 */
public class AlwaysValidValidator implements ITypeInformationValidator
{

	@Override
	public InputType getInputType()
	{
		return InputType.TEXT;
	}

	@Override
	public ValidationResult validate(String value2Validate) throws IllegalArgumentException
	{
		return ValidationResult.OK;
	}

}
