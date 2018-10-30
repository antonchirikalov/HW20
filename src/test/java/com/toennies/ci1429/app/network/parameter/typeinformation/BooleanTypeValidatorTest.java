package com.toennies.ci1429.app.network.parameter.typeinformation;

import static com.toennies.ci1429.app.network.parameter.typeinformation.ParameterAnnotationUtil.buildParameter;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.toennies.ci1429.app.network.parameter.ParamDescriptor;

public class BooleanTypeValidatorTest
{

	@Test
	public void validateTest()
	{
		ParamDescriptor yesno = buildParameter(null, "boolean", true);
		ParamDescriptor nono = buildParameter(null, "boolean", false);
	
		assertTrue(!yesno.validate("true").isError());
		assertTrue(!yesno.validate("false").isError());
		assertTrue(!yesno.validate("TRUE").isError());
		assertTrue(!yesno.validate("Rubbish asfdasf").isError());
		assertTrue(yesno.validate("").isError());
		assertTrue(yesno.validate(null).isError());
		
		assertFalse(nono.validate("true").isError());
		assertFalse(nono.validate("false").isError());
		assertFalse(nono.validate("TRUE").isError());
		assertFalse(nono.validate("Rubbish asfdasf").isError());
		assertFalse(nono.validate("").isError());
		assertFalse(nono.validate(null).isError());
		
		ParamDescriptor yesyes = buildParameter("true", "boolean", true);
		ParamDescriptor noyes = buildParameter("Rubbish", "boolean", false);

		assertFalse(yesyes.validate("true").isError());
		assertFalse(yesyes.validate("false").isError());
		assertFalse(yesyes.validate("TRUE").isError());
		assertFalse(yesyes.validate("Rubbish asfdasf").isError());
		assertFalse(yesyes.validate("").isError());
		assertFalse(yesyes.validate(null).isError());
		
		assertFalse(noyes.validate("true").isError());
		assertFalse(noyes.validate("false").isError());
		assertFalse(noyes.validate("TRUE").isError());
		assertFalse(noyes.validate("Rubbish asfdasf").isError());
		assertFalse(noyes.validate("").isError());
		assertFalse(noyes.validate(null).isError());
	}

}
