package com.toennies.ci1429.app.network.test;

import com.toennies.ci1429.app.Application;
import com.toennies.ci1429.app.model.DeviceException;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.scale.Commands;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.network.protocol.scale.hofelmeyer.HofelmeyerProtocol;
import com.toennies.ci1429.app.network.protocol.scale.radwag.RadwagProtocol;
import com.toennies.ci1429.app.repository.DeviceDescriptionEntity;
import com.toennies.ci1429.app.services.devices.IDevicesService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class TestSocketWithDeviceTest
{

	@Autowired
	private IDevicesService devicesService;

	private static final Logger logger = LogManager.getLogger();



	private  Map<String, String> parameters = new HashMap<>();
	URI uri;

	@Before
	public void setUp(){
		parameters.put(ScriptDevice.PARAM_TEST_SCRIPT, "testscripts/testscript.script");
		parameters.put(IProtocol.PARAM_SOCKET, TestSocket.class.getName());
	}

	@Test
	public void testRadwagDevice() throws DeviceException, IOException
	{

		DeviceDescriptionEntity deviceDescriptionEntity = new DeviceDescriptionEntity(1, DeviceType.SCALE, "Model1",
				"Vendor1", RadwagProtocol.class.getCanonicalName(), parameters);
		IDevice testDevice = devicesService.createNewDevice(deviceDescriptionEntity);
		testDevice.activateDevice();
		DeviceResponse response = testDevice.process(Commands.Command.WEIGH);
	}

	@Test
	public void testHofelMayerDevice() throws DeviceException
	{
		parameters.put(ScriptDevice.PARAM_TEST_SCRIPT, "testscripts/hofelmeyer/weight.script");
		DeviceDescriptionEntity deviceDescriptionEntity = new DeviceDescriptionEntity(1, DeviceType.SCALE, "Model1",
				"Vendor1", HofelmeyerProtocol.class.getCanonicalName(), parameters);
		IDevice testDevice = devicesService.createNewDevice(deviceDescriptionEntity);
		testDevice.activateDevice();
		DeviceResponse response = testDevice.process(Commands.Command.WEIGH);
		logger.info(response);


	}
}
