package com.toennies.ci1429.app.network.parameter.typeinformation;

import java.util.Arrays;
import java.util.List;

/**
 * CSV list validator. Validates against a given (comma separated) list of values.
 * Example:
 * 
 * list:a,b,c
 * 
 * @author renkenh
 */
@AtValidator("list")
public class CSVListTypeValidator extends AListTypeValidator
{

	public CSVListTypeValidator(String typeInformation)
	{
		super(typeInformation);
	}


	@Override
	protected List<String> parseValidationParameters(String strippedTypeInformation)
	{
		return Arrays.asList(strippedTypeInformation.split(","));
	}

}
