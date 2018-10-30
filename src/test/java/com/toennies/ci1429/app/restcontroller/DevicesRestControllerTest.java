package com.toennies.ci1429.app.restcontroller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.ReflectionUtils;

import com.toennies.ci1429.app.Application;
import com.toennies.ci1429.app.model.DeviceException;
import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.IDeviceDescription;
import com.toennies.ci1429.app.model.printer.Printer;
import com.toennies.ci1429.app.model.scale.Scale;
import com.toennies.ci1429.app.model.scanner.Scanner;
import com.toennies.ci1429.app.model.sps.Sps;
import com.toennies.ci1429.app.model.watcher.Watcher;
import com.toennies.ci1429.app.network.connector.ADataTransformer;
import com.toennies.ci1429.app.network.socket.LPTSocket;
import com.toennies.ci1429.app.network.socket.RS232Socket;
import com.toennies.ci1429.app.network.socket.RS232Socket.BaudRate;
import com.toennies.ci1429.app.network.socket.RS232Socket.FlowControl;
import com.toennies.ci1429.app.network.socket.RS232Socket.Parity;
import com.toennies.ci1429.app.network.socket.RS232Socket.StopBits;
import com.toennies.ci1429.app.repository.DeviceDescriptionEntity;
import com.toennies.ci1429.app.restcontroller.hateos.HateosDevice;
import com.toennies.ci1429.app.restcontroller.hateos.HateosPrinter;
import com.toennies.ci1429.app.restcontroller.hateos.HateosScale;
import com.toennies.ci1429.app.restcontroller.hateos.HateosScanner;
import com.toennies.ci1429.app.restcontroller.hateos.HateosSps;
import com.toennies.ci1429.app.restcontroller.hateos.HateosWatcher;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class DevicesRestControllerTest
{

	private final static Map<String, String> scannerCom2Parameters;
	static
	{
		Map<String, String> temp = new HashMap<String, String>();
		temp.put(RS232Socket.PARAM_PORT, "COM2");
		temp.put(RS232Socket.PARAM_BAUDRATE, BaudRate.R9600.toString());
		temp.put(RS232Socket.PARAM_DATABITS, "8");
		temp.put(RS232Socket.PARAM_STOP_BITS, StopBits.TWO.toString());
		temp.put(RS232Socket.PARAM_PARITY, Parity.NONE.toString());
		temp.put(RS232Socket.PARAM_FLOW_CONTROL, FlowControl.XONXOFF.toString());
		temp.put(ADataTransformer.PARAM_FRAME_SEP, "[GS]");
		temp.put(ADataTransformer.PARAM_FRAME_END, "[CR][LF]");
		scannerCom2Parameters = Collections.unmodifiableMap(temp);
	}

	@Autowired
	private DevicesRestController devicesController;

	private void cleanUpBeforeEachTest()
	{
		if (devicesController.getDevices().getBody().size() > 0)
		{
			devicesController.getDevices().getBody().forEach(d -> devicesController.deleteDevice(d.getDeviceID()));
		}
		assertTrue(devicesController.getDevices().getBody().size() == 0);
	}

	@Test
	public void postDeviceTest() throws DeviceException
	{
		cleanUpBeforeEachTest();
		DeviceDescriptionEntity deviceDescriptionEntity = new DeviceDescriptionEntity(1, DeviceType.SCANNER, "Model1",
				"Vendor1", RS232Socket.class.getCanonicalName(), scannerCom2Parameters);

		HateosDevice<?> justCreatedDevice = devicesController.postDevice(deviceDescriptionEntity).getBody();
		assertNotNull(justCreatedDevice);
		assertDevicesEquals(deviceDescriptionEntity, justCreatedDevice);

		// There must be only one device
		assertTrue(devicesController.getDevices().getBody().size() == 1);
	}

	@Test
	public void putDeviceTest() throws DeviceException
	{
		cleanUpBeforeEachTest();

		// First create a device, that is modified in the next step
		DeviceDescriptionEntity deviceDescriptionEntity = new DeviceDescriptionEntity(1, DeviceType.SCANNER, "Model1",
				"Vendor1", RS232Socket.class.getCanonicalName(), scannerCom2Parameters);
		HateosDevice<?> justCreatedDevice = devicesController.postDevice(deviceDescriptionEntity).getBody();
		assertNotNull(justCreatedDevice);
		assertDevicesEquals(deviceDescriptionEntity, justCreatedDevice);

		// now prepare the device, that differs from above saved device
		deviceDescriptionEntity.setDeviceModel("Model2");
		deviceDescriptionEntity.setVendor("Vendor2");
		deviceDescriptionEntity.setProtocolClass(LPTSocket.class.getCanonicalName());

		// create map with other configuration parameters
		HashMap<String, String> parametersWithOtherPort = new HashMap<String, String>(scannerCom2Parameters);
		parametersWithOtherPort.put(RS232Socket.PARAM_PORT, "COM1");
		parametersWithOtherPort.put(RS232Socket.PARAM_BAUDRATE, BaudRate.R115200.toString());
		parametersWithOtherPort.put(RS232Socket.PARAM_DATABITS, "7");
		parametersWithOtherPort.put(RS232Socket.PARAM_STOP_BITS, StopBits.ONE.toString());
		parametersWithOtherPort.put(RS232Socket.PARAM_PARITY, Parity.EVEN.toString());
		parametersWithOtherPort.put(RS232Socket.PARAM_FLOW_CONTROL, FlowControl.CTS.toString());
		parametersWithOtherPort.put(ADataTransformer.PARAM_FRAME_SEP, "[CR]");
		parametersWithOtherPort.put(ADataTransformer.PARAM_FRAME_END, "[GS]");

		deviceDescriptionEntity.setParameters(parametersWithOtherPort);

		// Next step is updating the device ...
		HateosDevice<?> justUpdatedDevice = devicesController.putDevice(deviceDescriptionEntity).getBody();
		// ... in order to check, if all changes are proper performed
		assertDevicesEquals(deviceDescriptionEntity, justUpdatedDevice);

		// Also check, if device can be found via search with id
		HateosDevice<?> foundById = devicesController.getDevice(1).getBody();
		assertDevicesEquals(deviceDescriptionEntity, foundById);

		// There must be only one device
		assertTrue(devicesController.getDevices().getBody().size() == 1);
	}

	private void assertDevicesEquals(IDeviceDescription deviceA, IDeviceDescription deviceB)
	{
		assertTrue(deviceA.getDeviceID() == deviceB.getDeviceID());
		assertEquals(deviceA.getType(), deviceB.getType());
		assertEquals(deviceA.getDeviceModel(), deviceB.getDeviceModel());
		assertEquals(deviceA.getVendor(), deviceB.getVendor());
		assertEquals(deviceA.getProtocolClass(), deviceB.getProtocolClass());
		assertEquals(deviceA.getParameters().size(), deviceB.getParameters().size());
		deviceA.getParameters().entrySet()
				.forEach(e -> assertEquals(e.getValue(), deviceB.getParameters().get(e.getKey())));
		deviceB.getParameters().entrySet()
				.forEach(e -> assertEquals(e.getValue(), deviceA.getParameters().get(e.getKey())));
	}

	@Test
	public void mapTest()
	{
		final String method2Test = "map";
		Method mapMethod = ReflectionUtils.findMethod(DevicesRestController.class, method2Test, IDeviceDescription.class);
		assertNotNull(mapMethod);
		ReflectionUtils.makeAccessible(mapMethod);

		DeviceDescriptionEntity scannerDescription = new DeviceDescriptionEntity(DeviceType.SCANNER, null, null, null,
				null);
		DeviceDescriptionEntity printerDescription = new DeviceDescriptionEntity(DeviceType.PRINTER, null, null, null,
				null);
		DeviceDescriptionEntity scaleDescription = new DeviceDescriptionEntity(DeviceType.SCALE, null, null, null,
				null);
		DeviceDescriptionEntity spsDescription = new DeviceDescriptionEntity(DeviceType.SPS, null, null, null, null);
		DeviceDescriptionEntity watcherDescription = new DeviceDescriptionEntity(DeviceType.WATCHER, null, null, "",
				null);

		Scanner scanner = new Scanner(scannerDescription);
		Printer printer = new Printer(printerDescription);
		Scale scale = new Scale(scaleDescription);
		Sps sps = new Sps(spsDescription);
		Watcher watcher = new Watcher(watcherDescription);

		HateosDevice<?> hateosScanner = (HateosDevice<?>) ReflectionUtils.invokeMethod(mapMethod, null, scanner);
		HateosDevice<?> hateosPrinter = (HateosDevice<?>) ReflectionUtils.invokeMethod(mapMethod, null, printer);
		HateosDevice<?> hateosScale = (HateosDevice<?>) ReflectionUtils.invokeMethod(mapMethod, null, scale);
		HateosDevice<?> hateosSps = (HateosDevice<?>) ReflectionUtils.invokeMethod(mapMethod, null, sps);
		HateosDevice<?> hateosWatcher = (HateosDevice<?>) ReflectionUtils.invokeMethod(mapMethod, null, watcher);

		assertTrue(hateosScanner instanceof HateosScanner);
		assertTrue(hateosPrinter instanceof HateosPrinter);
		assertTrue(hateosScale instanceof HateosScale);
		assertTrue(hateosSps instanceof HateosSps);
		assertTrue(hateosWatcher instanceof HateosWatcher);
	}

}
