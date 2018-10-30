/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.watcher.espa;

import java.util.HashMap;
import java.util.Map;

import com.toennies.ci1429.app.model.IDeviceDescription;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.network.protocol.watcher.AWatcherProtocol;
import com.toennies.ci1429.app.network.protocol.watcher.espa.ESPAProtocol;
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
		dd.setProtocolClass(ESPAProtocol.class.getName());
		return dd;
	}
	
	static final Map<String, String> parametersTCP()
	{
		HashMap<String, String> map = new HashMap<>();
		map.put(IProtocol.PARAM_SOCKET, TCPSocket.class.getName());
		map.put(TCPSocket.PARAM_HOST, "10.235.50.202");
		map.put(TCPSocket.PARAM_PORT, "8000");
		map.put(TCPSocket.PARAM_TIMEOUT, "2500");
		map.put(TCPSocket.PARAM_PING, "true");
		map.put(AWatcherProtocol.PARAM_NAME, "Test");
		return map;
	}


	private Utils()
	{
		//no instance
	}

}
