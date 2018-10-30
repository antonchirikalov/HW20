package com.toennies.ci1429.app.model.sps;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.repository.DeviceDescriptionEntity;

//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.assertTrue;
//
//import java.util.Map;
//
//import org.apache.commons.lang3.StringUtils;
//import org.junit.Test;
//
//import com.toennies.ci1429.app.model.DeviceResponse;
//import com.toennies.ci1429.app.model.DeviceResponse.PayloadType;
//import com.toennies.ci1429.app.model.DeviceResponse.Status;
//import com.toennies.ci1429.app.network.protocol.sps.SpsResponse;
//import com.toennies.ci1429.app.network.protocol.sps.Telegram;

public class SpsTest
{
	@Test
	public void getSupportedCommandsTest()
	{
		DeviceDescriptionEntity spsDescription = new DeviceDescriptionEntity(DeviceType.SPS, null, null, null, null);
		Sps sps = new Sps(spsDescription);
		assertTrue(StringUtils.isBlank(sps.getProtocolClass()));
		// Important is, that there is no exception, even though there is no
		// protocol
		assertNotNull(sps.getSupportedCommands());
	}
}
