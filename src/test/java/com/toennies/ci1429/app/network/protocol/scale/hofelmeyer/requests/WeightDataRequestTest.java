package com.toennies.ci1429.app.network.protocol.scale.hofelmeyer.requests;

import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Message;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class WeightDataRequestTest
{
	@Test
	public void handleResponseWithCorrectCRCShallReturnOkDataStatus()
	{
		WeightDataRequest request = new WeightDataRequest("RN");
		HardwareResponse response = request.handleResponse(prepareMessageWithValidCRC16());

		assertEquals(response.getStatus(), HardwareResponse.Status.OK_DATA);
	}

	@Test
	public void handleResponseWithIncorrectCRCShallReturnErrorStatus()
	{
		WeightDataRequest request = new WeightDataRequest("RN");
		HardwareResponse response = request.handleResponse(prepareMessageWithInvalidCRC16());

		assertEquals(response.getStatus(), HardwareResponse.Status.ERROR);
	}

	@Test
	public void handleResponseReturnsPopulatedWeightDataExtractedFromMessage()
	{
		WeightDataRequest request = new WeightDataRequest("RN");
		HardwareResponse response = request.handleResponse(prepareValidWeightResponse());

		assertEquals(430.00, response.getWeightData().getBrutto(), 0);
		assertEquals(400.00, response.getWeightData().getNetto(), 0);
		assertEquals(30.00, response.getWeightData().getTara(), 0);
	}

	@Test
	public void handleResponseShallConvertWeightInKilogramsToGrams()
	{
		WeightDataRequest request = new WeightDataRequest("RN");
		HardwareResponse response = request.handleResponse(prepareWeightResponseWithKilogramWeightUnit());

		assertEquals(40000.00, response.getWeightData().getBrutto(), 0);
		assertEquals(30000.00, response.getWeightData().getNetto(), 0);
		assertEquals(10000.00, response.getWeightData().getTara(), 0);
	}

	@Test
	public void handleResponseShallConvertWeightInTonnesToGrams()
	{
		WeightDataRequest request = new WeightDataRequest("RN");
		HardwareResponse response = request.handleResponse(prepareWeightResponseWithTonneWeightUnit());

		assertEquals(1000000.00, response.getWeightData().getBrutto(), 0);
		assertEquals(500000.00, response.getWeightData().getNetto(), 0);
		assertEquals(500000.00, response.getWeightData().getTara(), 0);
	}

	@Test
	public void handleResponseShallConvertWeightInPoundsToGrams()
	{
		WeightDataRequest request = new WeightDataRequest("RN");
		HardwareResponse response = request.handleResponse(prepareWeightResponseWithPoundWeightUnit());

		assertEquals(1133.98, response.getWeightData().getBrutto(), 0.001);
		assertEquals(907.18, response.getWeightData().getNetto(), 0.001);
		assertEquals(226.80, response.getWeightData().getTara(), 0.001);
	}

	@Test
	public void taraValueIsCalculatedFromNettoAndBruttoIgnoringTaraValueFromResponse()
	{
		WeightDataRequest request = new WeightDataRequest("RN");
		HardwareResponse response = request.handleResponse(prepareWeightResponseWithInvalidTara());

		assertNotEquals(10.00, response.getWeightData().getTara(), 0);
		assertEquals(30.00, response.getWeightData().getTara(), 0);
	}

	private IMessage prepareMessageWithValidCRC16()
	{
		return new Message("000023.10.1713:43   11  430.00   30.00  400.00g PT2001   33692".getBytes());
	}

	private IMessage prepareMessageWithInvalidCRC16()
	{
		return new Message("000023.10.1713:43   11  430.00   30.00  400.00g PT2001   99999".getBytes());
	}

	private IMessage prepareValidWeightResponse()
	{
		return new Message("000023.10.1713:43   11  430.00   30.00  400.00g PT2001   33692".getBytes());
	}

	private IMessage prepareWeightResponseWithInvalidTara()
	{
		// Tara value is 10.00 here, which is invalid. Correct value should be 30.00.
		return new Message("000023.10.1713:43   11  430.00   10.00  400.00g PT2001   25023".getBytes());
	}

	private IMessage prepareWeightResponseWithKilogramWeightUnit()
	{
		return new Message("000023.10.1713:43   11   40.00   10.00   30.00kgPT2001   58050".getBytes());
	}

	private IMessage prepareWeightResponseWithTonneWeightUnit()
	{
		return new Message("000023.10.1713:43   11    1.00    0.50    0.50t PT2001   65422".getBytes());
	}

	private IMessage prepareWeightResponseWithPoundWeightUnit()
	{
		return new Message("000023.10.1713:43   11    2.50    0.50    2.00lbPT2001   34334".getBytes());
	}
}
