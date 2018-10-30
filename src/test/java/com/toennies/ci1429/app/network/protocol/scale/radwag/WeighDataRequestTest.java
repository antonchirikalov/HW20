package com.toennies.ci1429.app.network.protocol.scale.radwag;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Message;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse.Status;

public class WeighDataRequestTest
{
	private static final WeighDataRequest SAMPLE_REQUEST = new WeighDataRequest("S");

	private static final double BRUTTO = -1.860;
	private static final double NETTO = -1.860;
	private static final double TARE = 0.000;

	private static final IMessage VALID_WEIGH_DIRECT_RESPONSE = new Message(("N" + NETTO + "kg").getBytes(),
			("B" + BRUTTO + "kg").getBytes(), ("T" + TARE + "kg").getBytes());

	private static final IMessage VALID_WEIGH_RESPONSE = new Message("S    -    1.860 kg".getBytes());

	private static final IMessage INVALID_SCALE_RESPONSE = new Message("abcdefghijk".getBytes());

	@Test
	public void testParseWeighDirectData()
	{
		HardwareResponse validWeighDirectResponse = SAMPLE_REQUEST.handleResponse(VALID_WEIGH_DIRECT_RESPONSE);
		assertTrue(Status.OK_DATA == validWeighDirectResponse.getStatus());
		assertTrue(BRUTTO * 1000 == validWeighDirectResponse.getWeightData().getBrutto());
		assertTrue(NETTO * 1000 == validWeighDirectResponse.getWeightData().getNetto());
		assertTrue(TARE * 1000 == validWeighDirectResponse.getWeightData().getTara());
	}

	@Test
	public void testParseWeighData()
	{
		HardwareResponse validWeightResponse = SAMPLE_REQUEST.handleResponse(VALID_WEIGH_RESPONSE);
		assertTrue(Status.OK_DATA == validWeightResponse.getStatus());
		assertTrue(BRUTTO * 1000 == validWeightResponse.getWeightData().getBrutto());
		assertTrue(NETTO * 1000 == validWeightResponse.getWeightData().getNetto());
		assertTrue(TARE * 1000 == validWeightResponse.getWeightData().getTara());
	}

	@Test
	public void testParseWeighDataForError()
	{
		HardwareResponse invalidResp = SAMPLE_REQUEST.handleResponse(INVALID_SCALE_RESPONSE);
		assertTrue(Status.OK_DATA == invalidResp.getStatus());
		assertTrue(0.0 == invalidResp.getWeightData().getBrutto());
		assertTrue(0.0 == invalidResp.getWeightData().getNetto());
		assertTrue(0.0 == invalidResp.getWeightData().getTara());
	}
}
