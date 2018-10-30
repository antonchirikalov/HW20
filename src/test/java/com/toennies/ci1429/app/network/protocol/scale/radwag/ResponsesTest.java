package com.toennies.ci1429.app.network.protocol.scale.radwag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.nio.charset.Charset;

import org.junit.Test;

import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Message;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse.Status;

public class ResponsesTest
{

	private static final IMessage SCALE_WAIT_RESPONSE = new Message("Z A".getBytes());

	private static final IMessage SCALE_CANCELLED_RESPONSE = new Message("Z I".getBytes());

	private static final IMessage SCALE_OK_RESPONSE = new Message("UT OK".getBytes());
	private static final IMessage SCALE_D_RESPONSE = new Message("Z D".getBytes());

	private static final IMessage SCALE_ERROR_UNKNOWN_RESPONSE = new Message("ES".getBytes());
	private static final IMessage SCALE_ERROR_E_RESPONSE = new Message("SS E".getBytes());
	private static final IMessage SCALE_ERROR_MAX_RESPONSE = new Message("T ^".getBytes());
	private static final IMessage SCALE_ERROR_MIN_RESPONSE = new Message("T v".getBytes());

	private static final IMessage SCALE_WEIGHT_RESPONSE = new Message("N-1.860".getBytes(), "B-1.860".getBytes(),
			"T0.000".getBytes());

	@Test
	public void testMap2ResultForWait()
	{
		assertEquals(Status.WAIT, Responses.map2Result(SCALE_WAIT_RESPONSE).getStatus());
		assertNotEquals(Status.WAIT, Responses.map2Result(SCALE_CANCELLED_RESPONSE).getStatus());
		assertNotEquals(Status.WAIT, Responses.map2Result(SCALE_OK_RESPONSE).getStatus());
		assertNotEquals(Status.WAIT, Responses.map2Result(SCALE_D_RESPONSE).getStatus());
		assertNotEquals(Status.WAIT, Responses.map2Result(SCALE_ERROR_UNKNOWN_RESPONSE).getStatus());
		assertNotEquals(Status.WAIT, Responses.map2Result(SCALE_ERROR_E_RESPONSE).getStatus());
		assertNotEquals(Status.WAIT, Responses.map2Result(SCALE_ERROR_MAX_RESPONSE).getStatus());
		assertNotEquals(Status.WAIT, Responses.map2Result(SCALE_ERROR_MIN_RESPONSE).getStatus());
	}

	@Test
	public void testMap2ResultForCancelled()
	{
		assertEquals(Status.CANCELED, Responses.map2Result(SCALE_CANCELLED_RESPONSE).getStatus());
		assertNotEquals(Status.CANCELED, Responses.map2Result(SCALE_WAIT_RESPONSE).getStatus());
		assertNotEquals(Status.CANCELED, Responses.map2Result(SCALE_OK_RESPONSE).getStatus());
		assertNotEquals(Status.CANCELED, Responses.map2Result(SCALE_D_RESPONSE).getStatus());
		assertNotEquals(Status.CANCELED, Responses.map2Result(SCALE_ERROR_UNKNOWN_RESPONSE).getStatus());
		assertNotEquals(Status.CANCELED, Responses.map2Result(SCALE_ERROR_E_RESPONSE).getStatus());
		assertNotEquals(Status.CANCELED, Responses.map2Result(SCALE_ERROR_MAX_RESPONSE).getStatus());
		assertNotEquals(Status.CANCELED, Responses.map2Result(SCALE_ERROR_MIN_RESPONSE).getStatus());
	}

	@Test
	public void testMap2ResultForOK()
	{
		assertEquals(Status.OK, Responses.map2Result(SCALE_OK_RESPONSE).getStatus());
		assertEquals(Status.OK, Responses.map2Result(SCALE_D_RESPONSE).getStatus());
		assertNotEquals(Status.OK, Responses.map2Result(SCALE_CANCELLED_RESPONSE).getStatus());
		assertNotEquals(Status.OK, Responses.map2Result(SCALE_WAIT_RESPONSE).getStatus());
		assertNotEquals(Status.OK, Responses.map2Result(SCALE_ERROR_UNKNOWN_RESPONSE).getStatus());
		assertNotEquals(Status.OK, Responses.map2Result(SCALE_ERROR_E_RESPONSE).getStatus());
		assertNotEquals(Status.OK, Responses.map2Result(SCALE_ERROR_MAX_RESPONSE).getStatus());
		assertNotEquals(Status.OK, Responses.map2Result(SCALE_ERROR_MIN_RESPONSE).getStatus());
	}

	@Test
	public void testMap2ResultForError()
	{
		assertEquals(Status.ERROR, Responses.map2Result(SCALE_ERROR_UNKNOWN_RESPONSE).getStatus());
		assertEquals(Status.ERROR, Responses.map2Result(SCALE_ERROR_E_RESPONSE).getStatus());
		assertEquals(Status.ERROR, Responses.map2Result(SCALE_ERROR_MAX_RESPONSE).getStatus());
		assertEquals(Status.ERROR, Responses.map2Result(SCALE_ERROR_MIN_RESPONSE).getStatus());
		assertNotEquals(Status.ERROR, Responses.map2Result(SCALE_OK_RESPONSE).getStatus());
		assertNotEquals(Status.ERROR, Responses.map2Result(SCALE_D_RESPONSE).getStatus());
		assertNotEquals(Status.ERROR, Responses.map2Result(SCALE_CANCELLED_RESPONSE).getStatus());
		assertNotEquals(Status.ERROR, Responses.map2Result(SCALE_WAIT_RESPONSE).getStatus());
	}

	@Test
	public void testMap2ResultForNull()
	{
		assertNull(Responses.map2Result(SCALE_WEIGHT_RESPONSE));
		assertNotNull(Responses.map2Result(SCALE_ERROR_UNKNOWN_RESPONSE));
		assertNotNull(Responses.map2Result(SCALE_ERROR_E_RESPONSE));
		assertNotNull(Responses.map2Result(SCALE_ERROR_MAX_RESPONSE));
		assertNotNull(Responses.map2Result(SCALE_ERROR_MIN_RESPONSE));
		assertNotNull(Responses.map2Result(SCALE_OK_RESPONSE));
		assertNotNull(Responses.map2Result(SCALE_D_RESPONSE));
		assertNotNull(Responses.map2Result(SCALE_CANCELLED_RESPONSE));
		assertNotNull(Responses.map2Result(SCALE_WAIT_RESPONSE));
	}

	private static final byte[] VALID_CLEAR_TARE_COMMAND = "UT 0 kg".getBytes(Charset.forName("US-ASCII"));
	private static final byte[] INVALID_CLEAR_TARE_COMMAND = "0 kg".getBytes(Charset.forName("US-ASCII"));

	@Test
	public void testMap2ResultWithCommand()
	{
		assertEquals(Status.OK, Responses.map2Result(SCALE_OK_RESPONSE, VALID_CLEAR_TARE_COMMAND).getStatus());
		assertEquals(Status.ERROR,
				Responses.map2Result(SCALE_OK_RESPONSE, INVALID_CLEAR_TARE_COMMAND).getStatus());
		assertEquals(Status.ERROR, Responses.map2Result(SCALE_D_RESPONSE, INVALID_CLEAR_TARE_COMMAND).getStatus());
	}
}
