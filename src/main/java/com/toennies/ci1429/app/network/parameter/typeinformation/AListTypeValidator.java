package com.toennies.ci1429.app.network.parameter.typeinformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Through this {@link ITypeInformationValidator} it's possible to check, if
 * given value is present in a specific {@link Collection}. This collection is
 * returned by {@link #provideValidValues(String)} method.
 */
public abstract class AListTypeValidator extends AAnnotatedValidator implements IListTypeInformationValidator
{

	private final List<String> values;
	
	
	/**
	 * Constructor for the validation framework. 
	 * @param typeInformation The type information with the parameters for subclasses to extract lists of values from.
	 */
	public AListTypeValidator(String typeInformation)
	{
		List<String> extracted = this.parseValidationParameters(this.extractValidationParameters(typeInformation));
		this.values = new ArrayList<>(extracted);
	}
	
	/**
	 * Called by the super-implementation to initialize the validator with a set of values. This method is called only
	 * once! I.e. the values are fixed. If dynamic values are needed, one must implement the {@link IListTypeInformationValidator}
	 * interface directly. 
	 * @param strippedTypeInformation The stripped information. Based on {@link #extractValidationParameters(String)}.
	 * @return A list of possible values against which to validate.
	 */
	protected abstract List<String> parseValidationParameters(String strippedTypeInformation);


	@Override
	public List<String> getValidValues()
	{
		return Collections.unmodifiableList(this.values);
	}
	
	@Override
	public ValidationResult validate(String value2Validate) throws IllegalArgumentException
	{
		boolean isValid = this.values.stream().anyMatch(validValue -> validValue.equals(value2Validate));
		if (isValid)
			return ValidationResult.OK;
		return new ValidationResult("The given value "+value2Validate+" could not be found in the list of valid values.");
	}

}
