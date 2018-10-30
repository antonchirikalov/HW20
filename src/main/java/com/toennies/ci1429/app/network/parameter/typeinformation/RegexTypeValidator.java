package com.toennies.ci1429.app.network.parameter.typeinformation;

import java.util.regex.Pattern;

import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;

/**
 * Define a regex that needs to be matched by {@link Parameter#value()}.
 * 
 * Example {@link Parameter} attributes for this validator.
 * 
 * <pre>
 * typeInformation="regex:a"
 * typeInformation="regex:^a$"
 * </pre>
 */
@AtValidator("regex")
public class RegexTypeValidator extends AAnnotatedValidator
{
	
	private final Pattern pattern;
	

	public RegexTypeValidator(String typeInformation)
	{
		this.pattern = Pattern.compile(this.extractValidationParameters(typeInformation));
	}


	@Override
	public ValidationResult validate(String value2Validate)
	{
		boolean success = this.pattern.matcher(value2Validate).matches();
		if (success)
			return ValidationResult.OK;
		return new ValidationResult(value2Validate+" does not match "+pattern.pattern());
	}

	@Override
	public InputType getInputType()
	{
		return InputType.TEXT;
	}

}
