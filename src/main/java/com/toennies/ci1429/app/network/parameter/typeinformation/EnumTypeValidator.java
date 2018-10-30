package com.toennies.ci1429.app.network.parameter.typeinformation;

import java.util.List;

import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.util.EnumValueUtils;

/**
 * Example {@link Parameter} attributes for this validator.
 * 
 * <pre>
 * typeInformation="enum:com.toennies.ci1429.app.model.ResponseFormat", value="RAW"
 * typeInformation="enum:com.toennies.ci1429.app.model.ResponseFormat", value="STRING"
 * </pre>
 */
@AtValidator("enum")
public class EnumTypeValidator extends AListTypeValidator
{


	public EnumTypeValidator(String typeInformation)
	{
		super(typeInformation);
	}
	

	@Override
	protected List<String> parseValidationParameters(String strippedTypeInformation)
	{
		return EnumValueUtils.getEnumValueStringsByClassName(strippedTypeInformation);
	}

}
