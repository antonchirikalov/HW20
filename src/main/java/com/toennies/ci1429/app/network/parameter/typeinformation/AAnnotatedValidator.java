package com.toennies.ci1429.app.network.parameter.typeinformation;

import com.toennies.ci1406.lib.utils.AnnotationUtils;

/**
 * This class provides some basic functionality every
 * {@link ITypeInformationValidator} implementation should extend from.
 * The validation framework expects to find an {@link AtValidator} annotation an an implementing subclass!
 */
public abstract class AAnnotatedValidator implements ITypeInformationValidator
{
	
	private final String validationType;


	/**
	 * Constructor. Loads the identifier to validate type information.
	 */
	public AAnnotatedValidator()
	{
		this.validationType = this.extractValidatorIdentifier();
	}

	
	/**
	 * Validates the given type information, throwing {@link RuntimeException}s when failing.
	 * Extracts the information that have to be parsed by the validator implementation.
	 * @param typeInformation The type information to extract the parameter information from.
	 * @return The parameter information (e.g. given 'int:1..5', '1..5' is returned).
	 * @throws RuntimeException When the typeInformation does not match the validation type given in the {@link AtValidator} annotation.
	 */
	protected String extractValidationParameters(String typeInformation)
	{
		if (!typeInformation.startsWith(this.validationType))
			throw new RuntimeException("Dumb Developer! Get your code correct.");
		if (typeInformation.equals(this.validationType))
			return "";
		if (typeInformation.charAt(this.validationType.length()) != VALIDATOR_DELIMITER)
			throw new RuntimeException("Malformed Type Information found. Delimiter expected, but not found.");
		return typeInformation.substring(this.validationType.length()+1);
	}
	
	private String extractValidatorIdentifier()
	{
		AtValidator annotation = AnnotationUtils.returnAnnotationOfClass(this.getClass(), AtValidator.class);
		if (annotation == null)
			throw new RuntimeException("Validator is missing AtValidator Annotation.");
		if (annotation.value().trim().length() == 0)
			throw new RuntimeException("Found unidentifiable Validator. Identifier is empty.");
		return annotation.value();
	}
}
