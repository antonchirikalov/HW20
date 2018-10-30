package com.toennies.ci1429.app.network.parameter.typeinformation;

import com.toennies.ci1429.app.network.parameter.ParamDescriptor;

public class ParameterAnnotationUtil
{

	public static ParamDescriptor buildParameter(final String value, final String typeInformation)
	{
		return buildParameter(value, typeInformation, true);
	}

	public static ParamDescriptor buildParameter(final String value, final String typeInformation,
			final boolean required)
	{
		return buildParameter(value, typeInformation, null, required);
	}

	public static ParamDescriptor buildParameter(final String value, final String typeInformation, final String name,
			final boolean required)
	{
		return new ParamDescriptor(name, value, required, typeInformation, "DUMMY_TOOLTIP");
	}

}
