package com.toennies.ci1429.app.util;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import com.toennies.ci1429.app.model.ResponseFormat;
import com.toennies.ci1429.app.restcontroller.DevicesRestController;

public class EnumValueUtilsTest
{

	@Test
	public void getEnumValuesByClassNameTest()
	{
		String ResponseFormatClassName = ResponseFormat.class.getCanonicalName();
		Collection<String> responseFormatEnumValues = EnumValueUtils.getEnumValueStringsByClassName(ResponseFormatClassName);

		assertTrue(responseFormatEnumValues != null);
		assertTrue(!responseFormatEnumValues.isEmpty());
		assertTrue(ResponseFormat.values().length == responseFormatEnumValues.size());
		assertTrue(responseFormatEnumValues.contains(ResponseFormat.RAW.toString()));
		Arrays.stream(ResponseFormat.values()).forEach(r -> assertTrue(responseFormatEnumValues.contains(r.toString())));

		String notAnEnumClass = DevicesRestController.class.getCanonicalName();
		Collection<String> noEnumValues = EnumValueUtils.getEnumValueStringsByClassName(notAnEnumClass);

		assertTrue(noEnumValues != null);
		assertTrue(noEnumValues.isEmpty());
	}

}
