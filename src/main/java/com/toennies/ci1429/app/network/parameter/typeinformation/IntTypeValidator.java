package com.toennies.ci1429.app.network.parameter.typeinformation;

import org.apache.commons.lang3.StringUtils;

import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;

/**
 * Example {@link Parameter} attributes for this validator. Through this
 * validator {@link Parameter#value()} needs to be in a specified range.
 * 
 * <pre>
 * typeInformation="int"
 * typeInformation="int:0.."
 * typeInformation="int:..0"
 * typeInformation="int:1..65000"
 * typeInformation="int:-127..127"
 * </pre>
 */
@AtValidator("int")
public class IntTypeValidator extends AAnnotatedValidator implements IIntTypeInformationValidator
{

	private Integer lowerBound;
	private Integer upperBound;


	/**
	 * Constructor for the validator framework.
	 * @param typeInformation The type information to parse.
	 */
	public IntTypeValidator(String typeInformation)
	{
		this.parseRange(this.extractValidationParameters(typeInformation));
	}


	private void parseRange(String rangeInformation)
	{
		if (!rangeInformation.contains(".."))
			return;
		int index = rangeInformation.indexOf("..");
		String lower = rangeInformation.substring(0, index);
		String upper = rangeInformation.substring(index+2);
		if (lower.trim().length() > 0)
			this.lowerBound = Integer.valueOf(lower);
		if (upper.trim().length() > 0)
			this.upperBound = Integer.valueOf(upper);
		if (this.upperBound != null && this.lowerBound != null && this.lowerBound.intValue() > this.upperBound.intValue())
		{
			Integer tmp = this.lowerBound;
			this.lowerBound = this.upperBound;
			this.upperBound = tmp;
		}
	}

	@Override
	public Integer getLowerBound()
	{
		return this.lowerBound;
	}

	@Override
	public Integer getUpperBound()
	{
		return this.upperBound;
	}

	@Override
	public ValidationResult validate(String value2Validate)
	{
		if (StringUtils.isBlank(value2Validate))
			return new ValidationResult("Empty string is not a number.");
		try
		{
			final int value = Integer.parseInt(value2Validate);
			if (this.lowerBound != null && value < this.lowerBound.intValue())
				return new ValidationResult(value2Validate + " is to low.");
			if (this.upperBound != null && value > this.upperBound.intValue())
				return new ValidationResult(value2Validate + " is to high.");
			return ValidationResult.OK;
		}
		catch (NumberFormatException ex)
		{
			// If value is not numeric, it can not be valid...
			return new ValidationResult(value2Validate + " is not an integer number.");
		}
	}

	@Override
	public InputType getInputType()
	{
		return InputType.INT;
	}

}
