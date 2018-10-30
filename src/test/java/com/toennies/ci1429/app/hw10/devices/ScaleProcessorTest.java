package com.toennies.ci1429.app.hw10.devices;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.toennies.ci1429.app.hw10.processing.devices.scale.ScaleProcessor;

public class ScaleProcessorTest
{
	private static final String CLEAR_TARE_COMMAND = "MCI011TL";
	private static final String INVALID_CLEAR_TARE_COMMAND = "SCA011TL";
	private static final String NULLIFY_COMMAND = "MCI011NU";
	private static final String INVALID_NULLIFY_COMMAND = "SCA011NU";
	private static final String REG_WEIGH_COMMAND = "MCI011WT";
	private static final String INVALID_REG_WEIGH_COMMAND = "SCA011WT";
	private static final String SCALE_TARE_COMMAND = "MCI011TA";
	private static final String INVALID_SCALE_TARE_COMMAND = "SCA011TA";
	private static final String TARE_WITH_VALUE_COMMAND = "MCI011TW123456";
	private static final String INVALID_TARE_WITH_VALUE_COMMAND = "SCA011TW";
	private static final String WEIGH_ATUOMATIC_COMMAND = "MCI011AW";
	private static final String INVALID_WEIGH_ATUOMATIC_COMMAND = "SCA011AW";
	private static final String WEIGH_COMMAND = "MCI011WI";
	private static final String INVALID_WEIGH_COMMAND = "SCA011WI";

	@Test
	public void isClearTaraTest()
	{
		assertTrue(ScaleProcessor.isClearTara(CLEAR_TARE_COMMAND));
		assertFalse(ScaleProcessor.isClearTara(INVALID_CLEAR_TARE_COMMAND));
		assertFalse(ScaleProcessor.isClearTara(NULLIFY_COMMAND));
	}

	@Test
	public void isSetNullTest()
	{
		assertTrue(ScaleProcessor.isSetNull(NULLIFY_COMMAND));
		assertFalse(ScaleProcessor.isSetNull(INVALID_NULLIFY_COMMAND));
		assertFalse(ScaleProcessor.isSetNull(REG_WEIGH_COMMAND));
	}

	@Test
	public void isWeightWithRegTest()
	{
		assertTrue(ScaleProcessor.isWeightWithReg(REG_WEIGH_COMMAND));
		assertFalse(ScaleProcessor.isWeightWithReg(INVALID_REG_WEIGH_COMMAND));
		assertFalse(ScaleProcessor.isWeightWithReg(SCALE_TARE_COMMAND));
	}

	@Test
	public void isTareTest()
	{
		assertTrue(ScaleProcessor.isTare(SCALE_TARE_COMMAND));
		assertFalse(ScaleProcessor.isTare(INVALID_SCALE_TARE_COMMAND));
		assertFalse(ScaleProcessor.isTare(TARE_WITH_VALUE_COMMAND));
	}

	@Test
	public void isTareWithValueTest()
	{
		assertTrue(ScaleProcessor.isTareWithValue(TARE_WITH_VALUE_COMMAND));
		assertFalse(ScaleProcessor.isTareWithValue(INVALID_TARE_WITH_VALUE_COMMAND));
		assertFalse(ScaleProcessor.isTareWithValue(WEIGH_ATUOMATIC_COMMAND));
	}

	@Test
	public void isAutoWeighTest()
	{
		assertTrue(ScaleProcessor.isAutoWeigh(WEIGH_ATUOMATIC_COMMAND));
		assertFalse(ScaleProcessor.isAutoWeigh(INVALID_WEIGH_ATUOMATIC_COMMAND));
		assertFalse(ScaleProcessor.isAutoWeigh(WEIGH_COMMAND));
	}

	@Test
	public void isNetWeighRequestTest()
	{
		assertTrue(ScaleProcessor.isNetWeighRequest(WEIGH_COMMAND));
		assertFalse(ScaleProcessor.isNetWeighRequest(INVALID_WEIGH_COMMAND));
		assertFalse(ScaleProcessor.isNetWeighRequest(CLEAR_TARE_COMMAND));
	}

}
