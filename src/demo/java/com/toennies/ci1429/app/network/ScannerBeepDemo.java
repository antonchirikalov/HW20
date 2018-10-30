package com.toennies.ci1429.app.network;

import java.util.HashMap;
import java.util.Map;

import com.toennies.ci1429.app.network.connector.ADataTransformer;
import com.toennies.ci1429.app.network.connector.FlexibleTimedHealthCheckTransformer;
import com.toennies.ci1429.app.network.parameter.AConfigContainer;
import com.toennies.ci1429.app.network.protocol.AProtocol;
import com.toennies.ci1429.app.network.protocol.scanner.DummyScannerSocket;
import com.toennies.ci1429.app.network.socket.RS232Socket;
import com.toennies.ci1429.app.network.socket.RS232Socket.FlowControl;
import com.toennies.ci1429.app.network.socket.RS232Socket.Parity;
import com.toennies.ci1429.app.util.ASCII;

/**
 * 
 * @author renkenh
 */
public class ScannerBeepDemo
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
		RS232Socket socket = new RS232Socket();
		socket.connect(new AConfigContainer() {
			
			@Override
			protected Map<String, String> _config() {
				return parametersRS232();
			}
		});

		
		System.out.println("Started");
		socket.push(ASCII.parseHuman("[ESC][3q[CR]").getBytes());

		socket.push(ASCII.parseHuman("[ESC][0q[CR]").getBytes());

		System.out.println("Send");
		
		socket.disconnect();
	}

}
