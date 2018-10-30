package com.toennies.ci1429.app.network.parameter.typeinformation;

import static com.toennies.ci1429.app.network.parameter.typeinformation.ParameterAnnotationUtil.buildParameter;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.toennies.ci1429.app.network.parameter.ParamDescriptor;

public class RegexTypeValidatorTest
{

	@Test
	public void validateTest()
	{
		ParamDescriptor yesno = buildParameter(null, "regex:^a$");
		ParamDescriptor yesyes = buildParameter("a", "regex:a");

		assertTrue(!yesno.validate("a").isError());
		assertTrue(yesno.validate("ab").isError());
		assertTrue(yesno.validate(null).isError());

		assertTrue(!yesyes.validate("a").isError());
		assertTrue(yesyes.validate("ab").isError());
		assertTrue(!yesyes.validate(null).isError());
	}

}
