package com.toennies.ci1429.app.network.parameter.typeinformation;

import static com.toennies.ci1429.app.network.parameter.typeinformation.ParameterAnnotationUtil.buildParameter;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.toennies.ci1429.app.network.parameter.ParamDescriptor;

public class CSVListTypeValidatorTest
{

	@Test
	public void validateTest()
	{
		ParamDescriptor yesno = buildParameter(null, "list:a,b,c");
		ParamDescriptor yesyes = buildParameter("b", "list:a,b,c");

		assertFalse(yesno.validate("b").isError());
		assertFalse(yesno.validate("c").isError());
		assertTrue(yesno.validate("ab").isError());
		assertTrue(yesno.validate("").isError());
		assertTrue(yesno.validate(null).isError());
		assertTrue(yesno.validate("A").isError());

		assertTrue(!yesyes.validate("b").isError());
		assertTrue(!yesyes.validate("c").isError());
		assertTrue(yesyes.validate("ab").isError());
		assertTrue(!yesyes.validate("").isError());
		assertTrue(!yesyes.validate(null).isError());
		assertTrue(yesyes.validate("A").isError());
	}

}
