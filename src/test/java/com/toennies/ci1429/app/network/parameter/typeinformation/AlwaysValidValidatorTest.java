package com.toennies.ci1429.app.network.parameter.typeinformation;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AlwaysValidValidatorTest
{
	private ITypeInformationValidator alwaysValidValidator;

	@Test
	public void validateTest()
	{
		alwaysValidValidator = new AlwaysValidValidator();

		assertTrue(!alwaysValidValidator.validate("").isError());
		assertTrue(!alwaysValidValidator.validate(null).isError());
		assertTrue(!alwaysValidValidator.validate("true").isError());
		assertTrue(!alwaysValidValidator.validate("int:1..5").isError());
		assertTrue(!alwaysValidValidator.validate("Rubbish jksdfhjkahsfjkh").isError());
	}

}
