package com.toennies.ci1429.app.network.parameter.typeinformation;

import static com.toennies.ci1429.app.network.parameter.typeinformation.ParameterAnnotationUtil.buildParameter;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.toennies.ci1429.app.model.ResponseFormat;
import com.toennies.ci1429.app.network.parameter.ParamDescriptor;

public class EnumTypeValidatorTest
{

	@Test
	public void validateTest()
	{
		ParamDescriptor yesno = buildParameter(null, "enum" + ITypeInformationValidator.VALIDATOR_DELIMITER + ResponseFormat.class.getCanonicalName());
		ParamDescriptor yesyes = buildParameter(ResponseFormat.STRING.name(), "enum" + ITypeInformationValidator.VALIDATOR_DELIMITER + ResponseFormat.class.getCanonicalName());

		assertTrue(!yesno.validate(ResponseFormat.EAN128.name()).isError());
		assertTrue(!yesno.validate(ResponseFormat.STRING.name()).isError());
		assertTrue(!yesno.validate(ResponseFormat.RAW.name()).isError());
		assertTrue(!yesno.validate(ResponseFormat.HUMAN.name()).isError());
		assertTrue(yesno.validate("").isError());
		assertTrue(yesno.validate(null).isError());
		assertTrue(yesno.validate("Rubbish").isError());
		assertTrue(yesno.validate(ResponseFormat.HUMAN.presentation).isError());

		assertTrue(!yesyes.validate(ResponseFormat.EAN128.name()).isError());
		assertTrue(!yesyes.validate(ResponseFormat.STRING.name()).isError());
		assertTrue(!yesyes.validate(ResponseFormat.RAW.name()).isError());
		assertTrue(!yesyes.validate(ResponseFormat.HUMAN.name()).isError());
		assertTrue(!yesyes.validate("").isError());
		assertTrue(!yesyes.validate(null).isError());
		assertTrue(yesyes.validate("Rubbish").isError());
		assertTrue(yesyes.validate(ResponseFormat.HUMAN.presentation).isError());
	}

}
