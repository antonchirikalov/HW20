package com.toennies.ci1429.app.network.parameter.typeinformation;

import org.apache.commons.lang3.StringUtils;

import com.toennies.ci1429.app.util.Utils;

/**
 * Currently this is a very basic implementation of a representation of a
 * validation result. In future it's planned to store a detailed validation
 * result information in the result string.
 */
public class ValidationResult
{

	/** The only possible instance of a successful validation. */
	public static final ValidationResult OK = new ValidationResult();


	private final String errorText;
	private final boolean error;


	ValidationResult()
	{
		this.error = false;
		this.errorText = null;
	}


	/**
	 * Constructor for invalid (error) results.
	 * @param errorText The error text may not be empty as of {@link Utils#isEmpty(String)}.
	 */
	public ValidationResult(String errorText)
	{
		this.error = true;
		if (StringUtils.isBlank(errorText))
			throw new IllegalArgumentException("Error Text may not be empty.");
		this.errorText = errorText;
	}

	/**
	 * @return The error text. Only valid when {@link #isError()} returns <code>true</code>.
	 */
	public String getErrorText()
	{
		return errorText;
	}

	/**
	 * @return Whether this result indicates a failed validation or not.
	 */
	public boolean isError()
	{
		return error;
	}
}
