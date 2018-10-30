package com.toennies.ci1429.app.network.test;

import com.toennies.ci1429.app.Application;
import com.toennies.ci1429.app.model.DeviceException;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.scale.Commands;
import com.toennies.ci1429.app.model.scale.WeightData;
import com.toennies.ci1429.app.network.connector.ADataTransformer;
import com.toennies.ci1429.app.network.connector.FlexibleTimedHealthCheckTransformer;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.network.protocol.scale.AScaleProtocol;
import com.toennies.ci1429.app.network.protocol.scale.bizerba.BizerbaSTProtocol;
import com.toennies.ci1429.app.network.socket.ISocket;
import com.toennies.ci1429.app.network.socket.TCPSocket;
import com.toennies.ci1429.app.repository.DeviceDescriptionEntity;
import com.toennies.ci1429.app.services.devices.IDevicesService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertFalse;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class TestSocketBizerbaProtocolTest
{

	@Autowired
	private IDevicesService devicesService;

	private  Map<String, String> parameters = new HashMap<>();

	@Before
	public void setUp(){
		parameters.put(IProtocol.PARAM_SOCKET, TestSocket.class.getName());
		parameters.put(ISocket.PARAM_TIMEOUT, "2000");
		parameters.put(IProtocol.PARAM_REQUEST_TIMEOUT, "500000");
		parameters.put(TCPSocket.PARAM_PING, "true");
		parameters.put(ADataTransformer.PARAM_FRAME_END, "[ETB]");
		parameters.put(BizerbaSTProtocol.PARAM_SCALE_NUMBER, "0");
		parameters.put(BizerbaSTProtocol.PARAM_SEND_ENQ, "true");
		parameters.put(ADataTransformer.PARAM_FRAME_SEP, "[ETX]");
		parameters.put(ADataTransformer.PARAM_FRAME_START, "[SOH]");
		parameters.put(BizerbaSTProtocol.PARAM_UNIT_NUMBER, "1");
		parameters.put(BizerbaSTProtocol.PARAM_UNIT, "KG");
		parameters.put(AScaleProtocol.PARAM_PRECISION, "3");
		parameters.put(AScaleProtocol.PARAM_RESPONSE, "EAN128");
		parameters.put(FlexibleTimedHealthCheckTransformer.PARAM_HEALTHCHECK, "5000");
	}

	@Test
	public void testActivateDeactivate() throws DeviceException
	{

		parameters.put(ScriptDevice.PARAM_HANDSHAKE_SCRIPT_URI, "testscripts/bizerba/handshake_bizerba.script");
		parameters.put(ScriptDevice.PARAM_TEST_SCRIPT, "testscripts/bizerba/bizerba_weight.script");
		DeviceDescriptionEntity deviceDescriptionEntity = new DeviceDescriptionEntity(10, DeviceType.SCALE, "Model1",
				"Vendor1", BizerbaSTProtocol.class.getCanonicalName(), parameters);
		Iterator<IDevice> iterator = devicesService.getAllDevices().iterator();
		while (iterator.hasNext()) {
			devicesService.deleteDeviceById(iterator.next().getDeviceID());
		}

		IDevice testDevice = devicesService.createNewDevice(deviceDescriptionEntity);

		testDevice.activateDevice();
		assertTrue(testDevice.getDeviceState() == IDevice.DeviceState.CONNECTED);
		testDevice.deactivateDevice();
		assertTrue(testDevice.getDeviceState() == IDevice.DeviceState.NOT_INITIALIZED);
		testDevice.activateDevice();
		assertTrue(testDevice.getDeviceState() == IDevice.DeviceState.CONNECTED);
		testDevice.deactivateDevice();

	}

	@Test
	public void testItemAdding() throws DeviceException
	{
		parameters.put(ScriptDevice.PARAM_HANDSHAKE_SCRIPT_URI, "testscripts/bizerba/handshake_bizerba.script");
		parameters.put(ScriptDevice.PARAM_TEST_SCRIPT, "testscripts/bizerba/bizerba_item_adding.script");
		DeviceDescriptionEntity deviceDescriptionEntity = new DeviceDescriptionEntity(10, DeviceType.SCALE, "Model1",
				"Vendor1", BizerbaSTProtocol.class.getCanonicalName(), parameters);
		devicesService.deleteDeviceById(10);
		IDevice testDevice = devicesService.createNewDevice(deviceDescriptionEntity);

		testDevice.activateDevice();
		assertTrue(testDevice.getDeviceState() == IDevice.DeviceState.CONNECTED);

		DeviceResponse response = testDevice.process(Commands.Command.ITEM_ADDING);
		assertTrue(response.getPayload() instanceof WeightData);
		assertTrue(((WeightData)response.getPayload()).getBrutto() == 101000);
		testDevice.deactivateDevice();

	}

	@Test
	public void testWeigh() throws DeviceException
	{

		parameters.put(ScriptDevice.PARAM_HANDSHAKE_SCRIPT_URI, "testscripts/bizerba/handshake_bizerba.script");
		parameters.put(ScriptDevice.PARAM_TEST_SCRIPT, "testscripts/bizerba/bizerba_weight.script");
		DeviceDescriptionEntity deviceDescriptionEntity = new DeviceDescriptionEntity(10, DeviceType.SCALE, "Model1",
				"Vendor1", BizerbaSTProtocol.class.getCanonicalName(), parameters);
		Iterator<IDevice> iterator = devicesService.getAllDevices().iterator();
		while (iterator.hasNext()) {
			devicesService.deleteDeviceById(iterator.next().getDeviceID());
		}

		IDevice testDevice = devicesService.createNewDevice(deviceDescriptionEntity);

		testDevice.activateDevice();

		DeviceResponse response = testDevice.process(Commands.Command.WEIGH);
		assertTrue(response.getPayload() instanceof WeightData);
		assertTrue(((WeightData)response.getPayload()).getBrutto() == 102000);

		testDevice.deactivateDevice();
		response = testDevice.process(Commands.Command.WEIGH);
		assertFalse(response.getPayload() instanceof WeightData);

	}

}
