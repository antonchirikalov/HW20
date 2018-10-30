package com.toennies.ci1429.app.network;

import java.util.HashMap;
import java.util.Map;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.model.ResponseFormat;
import com.toennies.ci1429.app.model.scanner.Scanner;
import com.toennies.ci1429.app.network.connector.ADataTransformer;
import com.toennies.ci1429.app.network.connector.FlexibleTimedHealthCheckTransformer;
import com.toennies.ci1429.app.network.protocol.AProtocol;
import com.toennies.ci1429.app.network.protocol.scanner.DummyScannerSocket;
import com.toennies.ci1429.app.network.protocol.scanner.PM9500ScannerProtocol;
import com.toennies.ci1429.app.network.socket.RS232Socket;
import com.toennies.ci1429.app.network.socket.RS232Socket.FlowControl;
import com.toennies.ci1429.app.network.socket.RS232Socket.Parity;
import com.toennies.ci1429.app.repository.DeviceDescriptionEntity;

/**
 * 
 * @author renkenh
 */
public class ScannerDemo
{

	static final Map<String, String> parametersRS232()
	{
		HashMap<String, String> map = new HashMap<>();
		map.put(RS232Socket.PARAM_BAUDRATE, "9600");
		map.put(RS232Socket.PARAM_STOP_BITS, "2");
//		map.put(AProtocol.PARAM_SOCKET, RS232Socket.class.getName());
		map.put(AProtocol.PARAM_SOCKET, DummyScannerSocket.class.getName());
		map.put(RS232Socket.PARAM_FLOW_CONTROL, FlowControl.XONXOFF.toString());
		map.put(RS232Socket.PARAM_PORT, "COM2");
		map.put(RS232Socket.PARAM_PARITY, Parity.NONE.toString());
		map.put(RS232Socket.PARAM_DATABITS, "8");
		map.put(ADataTransformer.PARAM_FRAME_SEP, "[GS]");
		map.put(RS232Socket.PARAM_TIMEOUT, "2000");
		map.put(FlexibleTimedHealthCheckTransformer.PARAM_HEALTHCHECK, "3000");
		map.put(ADataTransformer.PARAM_FRAME_END, "[CR][LF]");
		return map;
	}

	
	public static void main(String[] args) throws Exception
	{
		System.out.println("Loaded");

		DeviceDescriptionEntity dd = new DeviceDescriptionEntity(DeviceType.SCANNER, "PM9500", "Datalogic", null, null);
		dd.setProtocolClass(PM9500ScannerProtocol.class.getName());
		dd.setParameters(parametersRS232());
		
		Scanner scanner = new Scanner(dd);

		System.out.println(state(scanner) + " Started");
		scanner.activateDevice();
		
		int i = 0;
//		while (i++ < 4)
		while (i < Integer.MAX_VALUE)
		{
			DeviceResponse response = scanner.process(ResponseFormat.HUMAN);
			System.out.println(state(scanner) + " Scanned: " + response(response));
			
			Thread.sleep(1000);
		}
		
		scanner.deactivateDevice();
		System.out.println(state(scanner) + " Ended");
	}

	private static final String state(Scanner device)
	{
		return "[" + device.getDeviceState() + "]";
	}
	
	private static final String response(DeviceResponse response)
	{
		if (response.getPayload() instanceof String)
			return response.getPayload().toString();
		return response.getStatus().toString();
	}
}
