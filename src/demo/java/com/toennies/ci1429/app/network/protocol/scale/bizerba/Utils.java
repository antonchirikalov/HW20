/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.bizerba;

import java.util.HashMap;
import java.util.Map;

import com.toennies.ci1429.app.model.IDeviceDescription;
import com.toennies.ci1429.app.network.connector.ADataTransformer;
import com.toennies.ci1429.app.network.connector.FlexibleTimedHealthCheckTransformer;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.network.socket.TCPSocket;
import com.toennies.ci1429.app.repository.DeviceDescriptionEntity;

/**
 * @author renkenh
 *
 */
final class Utils
{
	
	static final IDeviceDescription descriptionTCP()
	{
		DeviceDescriptionEntity dd = new DeviceDescriptionEntity();
		dd.setParameters(parametersTCP());
		dd.setProtocolClass(BizerbaSTProtocol.class.getName());
		return dd;
	}
	
	static final Map<String, String> parametersTCP()
	{
		HashMap<String, String> map = new HashMap<>();
		map.put(IProtocol.PARAM_SOCKET, TCPSocket.class.getName());
		map.put(TCPSocket.PARAM_HOST, "10.235.50.75");
		map.put(TCPSocket.PARAM_PORT, "10044");
		map.put(TCPSocket.PARAM_TIMEOUT, "10000");
		map.put(TCPSocket.PARAM_PING, "true");
		map.put(ADataTransformer.PARAM_FRAME_END, "[ETB]");
		map.put(BizerbaSTProtocol.PARAM_SCALE_NUMBER, "1");
		map.put(BizerbaSTProtocol.PARAM_SEND_ENQ, "true");
		map.put(ADataTransformer.PARAM_FRAME_SEP, "[ETX]");
		map.put(ADataTransformer.PARAM_FRAME_START, "[SOH]");
		map.put(BizerbaSTProtocol.PARAM_UNIT_NUMBER, "0");
		map.put(FlexibleTimedHealthCheckTransformer.PARAM_HEALTHCHECK, "5000");
		return map;
	}


	private Utils()
	{
		//no instance
	}

}
