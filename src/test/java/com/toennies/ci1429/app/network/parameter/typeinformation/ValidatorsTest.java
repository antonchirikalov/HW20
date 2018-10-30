package com.toennies.ci1429.app.network.parameter.typeinformation;

import static com.toennies.ci1429.app.network.parameter.typeinformation.Validators.createValidatorFrom;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ValidatorsTest
{

	@Test
	public void createValidatorFromTests()
	{
		ITypeInformationValidator intValidator1 = createValidatorFrom("int");
		ITypeInformationValidator intValidator2 = createValidatorFrom("int:0..");
		ITypeInformationValidator floatValidator1 = createValidatorFrom("float");
		ITypeInformationValidator floatValidator2 = createValidatorFrom("float:0.0..");
		ITypeInformationValidator regexValidator1 = createValidatorFrom("regex:a");
		ITypeInformationValidator booleanValidator1 = createValidatorFrom("boolean");
		// no delimiter allowed after boolean
		ITypeInformationValidator booleanValidator2 = createValidatorFrom("boolean:");
		ITypeInformationValidator enumValidator1 = createValidatorFrom("enum:");
		ITypeInformationValidator enumValidator2 = createValidatorFrom(
				"enum:com.toennies.ci1429.app.model.ResponseFormat");
		ITypeInformationValidator socketValidator1 = createValidatorFrom(
				"java:com.toennies.ci1429.app.network.parameter.typeinformation.SocketTypeValidator");
		ITypeInformationValidator charsetValidator = createValidatorFrom(
				"java:com.toennies.ci1429.app.network.parameter.typeinformation.CharsetValidator");
		ITypeInformationValidator comPortValidator1 = createValidatorFrom(
				"java:com.toennies.ci1429.app.network.parameter.typeinformation.ComPortTypeValidator");
		ITypeInformationValidator protocolValidator = createValidatorFrom(
				"java:com.toennies.ci1429.app.network.parameter.typeinformation.ProtocolNameValidator");
		ITypeInformationValidator listValidator = createValidatorFrom("list");

		assertTrue(intValidator1 instanceof IntTypeValidator);
		assertTrue(intValidator2 instanceof IntTypeValidator);
		assertTrue(floatValidator1 instanceof FloatTypeValidator);
		assertTrue(floatValidator2 instanceof FloatTypeValidator);
		assertTrue(regexValidator1 instanceof RegexTypeValidator);
		assertTrue(booleanValidator1 instanceof BooleanTypeValidator);
		assertTrue(booleanValidator2 instanceof BooleanTypeValidator);
		assertTrue(enumValidator1 instanceof EnumTypeValidator);
		assertTrue(enumValidator2 instanceof EnumTypeValidator);
		assertTrue(socketValidator1 instanceof SocketTypeValidator);
		assertTrue(charsetValidator instanceof CharsetValidator);
		assertTrue(comPortValidator1 instanceof ComPortTypeValidator);
		assertTrue(protocolValidator instanceof ProtocolNameValidator);
		assertTrue(listValidator instanceof CSVListTypeValidator);

	}
}
