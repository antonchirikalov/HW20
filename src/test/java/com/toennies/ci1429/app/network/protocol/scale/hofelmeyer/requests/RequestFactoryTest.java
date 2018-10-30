package com.toennies.ci1429.app.network.protocol.scale.hofelmeyer.requests;

import com.toennies.ci1429.app.network.protocol.scale.IHardwareRequest;
import org.junit.Test;

import static com.toennies.ci1429.app.model.scale.Commands.Command.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RequestFactoryTest
{
	@Test
	public void factoryShallCreateARequestForSupportedCommand()
	{
		RequestFactory requestFactory = new RequestFactory();

		assertEquals("TA", getMessage(requestFactory.getRequest(TARE)));
		assertEquals("TM000050,00", getMessage(requestFactory.getRequest(TARE_WITH_VALUE, 50.00)));
		assertEquals("TC", getMessage(requestFactory.getRequest(CLEAR_TARE)));
		assertEquals("SZ", getMessage(requestFactory.getRequest(ZERO)));
		assertEquals("RN", getMessage(requestFactory.getRequest(WEIGH)));
		assertEquals("RM", getMessage(requestFactory.getRequest(WEIGH_AUTOMATIC)));
	}

	@Test
	public void factoryShallReturnNullForUnsupportedCommand()
	{
		RequestFactory requestFactory = new RequestFactory();

		assertNull(requestFactory.getRequest(ITEM_ADDING));
	}

	private String getMessage(IHardwareRequest request)
	{
		return new String(request.getRequestMessage().words().get(0));
	}
}
