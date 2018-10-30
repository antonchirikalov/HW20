package com.toennies.ci1429.app.network.parameter.typeinformation;

import static com.toennies.ci1429.app.network.parameter.typeinformation.ParameterAnnotationUtil.buildParameter;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.toennies.ci1429.app.network.parameter.ParamDescriptor;

public class IntTypeValidatorTest
{
	
	private static final String[] TYPE_INFOS = { "int:..0", "int:0..", "int:1..5", "int:-13..65", "int:65..-13", "int" };

	@Test
	public void parseTypeInfosTest()
	{
		IntTypeValidator i0 = new IntTypeValidator(TYPE_INFOS[0]);
		assertTrue(i0.getLowerBound() == null);
		assertTrue(i0.getUpperBound().intValue() == 0);
		assertTrue(!i0.validate("-13").isError());
		assertTrue(i0.validate("13").isError());
		
		IntTypeValidator i1 = new IntTypeValidator(TYPE_INFOS[1]);
		assertTrue(i1.getLowerBound().intValue() == 0);
		assertTrue(i1.getUpperBound() == null);
		assertTrue(i1.validate("-13").isError());
		assertTrue(!i1.validate("13").isError());

		IntTypeValidator i2 = new IntTypeValidator(TYPE_INFOS[2]);
		assertTrue(i2.getLowerBound().intValue() == 1);
		assertTrue(i2.getUpperBound().intValue() == 5);
		assertTrue(!i2.validate("4").isError());
		assertTrue(i2.validate("14").isError());
		assertTrue(i2.validate("-14").isError());

		IntTypeValidator i3 = new IntTypeValidator(TYPE_INFOS[3]);
		assertTrue(i3.getLowerBound().intValue() == -13);
		assertTrue(i3.getUpperBound().intValue() == 65);
		assertTrue(!i3.validate("4").isError());
		assertTrue(i3.validate("66").isError());
		assertTrue(i3.validate("-100").isError());

		IntTypeValidator i4 = new IntTypeValidator(TYPE_INFOS[4]);
		assertTrue(i4.getLowerBound().intValue() == -13);
		assertTrue(i4.getUpperBound().intValue() == 65);
		assertTrue(!i4.validate("4").isError());
		assertTrue(i4.validate("66").isError());
		assertTrue(i4.validate("-100").isError());

		IntTypeValidator i5 = new IntTypeValidator(TYPE_INFOS[5]);
		assertTrue(i5.getLowerBound() == null);
		assertTrue(i5.getUpperBound() == null);
		assertTrue(!i5.validate("66").isError());
		assertTrue(!i5.validate("-100").isError());
	}
	
	@Test
	public void validateTest()
	{
		ParamDescriptor yesno = buildParameter(null, "int:-13..87");
		ParamDescriptor yesyes = buildParameter("14", "int:-13..65");

		assertTrue(!yesno.validate("0").isError());
		assertTrue(yesno.validate("").isError());
		assertTrue(yesno.validate("-100").isError());
		assertTrue(yesno.validate("65000").isError());

		assertTrue(!yesyes.validate("0").isError());
		assertTrue(!yesyes.validate("").isError());
		assertTrue(yesyes.validate("-100").isError());
		assertTrue(yesyes.validate("65000").isError());
	}

}
