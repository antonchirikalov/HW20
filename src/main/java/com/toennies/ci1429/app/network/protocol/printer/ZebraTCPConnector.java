/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.printer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.toennies.ci1429.app.network.connector.AWrappedConnector;
import com.toennies.ci1429.app.network.connector.IConnector;
import com.toennies.ci1429.app.network.parameter.AConfigContainer;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.socket.TCPSocket;

/**
 * Special connector for zebra printers to circumvent network break down
 * problems.
 * 
 * @author renkenh
 */
class ZebraTCPConnector extends AWrappedConnector<byte[]>
{

	// private static final Logger LOGGER = LogManager.getLogger();

	private volatile IConfigContainer config;

	public ZebraTCPConnector(IConnector<byte[]> connector)
	{
		super(connector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		Map<String, String> modConfig = new HashMap<>(config.getConfig());
		modConfig.put(TCPSocket.PARAM_PING, "false");
		this.config = new AConfigContainer()
		{
			
			@Override
			protected Map<String, String> _config()
			{
				return modConfig;
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected()
	{
		return this.config != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void push(byte[] data) throws IOException
	{
		if (!this.isConnected())
			throw new IOException("Not connected"); // see isConnected also

		ZebraTCPConnector.super.connect(config);
		ZebraTCPConnector.super.push(data);
		ZebraTCPConnector.super.disconnect();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void disconnect() throws IOException
	{
		this.config = null;
		super.disconnect();
		// this.publishEvent(EVENT_STATE_CHANGED);
	}

	@Override
	public byte[] poll()
	{
		return null;
	}

	@Override
	public byte[] pop()
	{
		return null;
	}

}
