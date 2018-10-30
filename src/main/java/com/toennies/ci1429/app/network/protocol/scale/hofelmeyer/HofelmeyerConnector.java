package com.toennies.ci1429.app.network.protocol.scale.hofelmeyer;

import com.toennies.ci1429.app.network.connector.IConnector;
import com.toennies.ci1429.app.network.connector.IConnectorWrapper;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class HofelmeyerConnector  implements IConnector<IMessage>, IConnectorWrapper<IFlexibleConnector<IMessage, IExtendedMessage>, IMessage, IExtendedMessage>
{
	private final IFlexibleConnector<IMessage, IExtendedMessage> connector;

	public HofelmeyerConnector(IFlexibleConnector<IMessage, IExtendedMessage> connector)
	{
		this.connector = connector;
	}

	@Override
	public IFlexibleConnector<IMessage, IExtendedMessage> getWrappedConnector()
	{
		return this.connector;
	}

	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		this.connector.connect(config);
	}

	@Override
	public boolean isConnected()
	{
		return this.connector.isConnected();
	}

	@Override
	public IMessage poll() throws IOException
	{
		return this.connector.poll();
	}

	@Override
	public IMessage pop() throws IOException, TimeoutException
	{
		return this.connector.pop();
	}

	@Override
	public void push(IMessage entity) throws IOException
	{
		this.connector.push(entity);
	}

	@Override
	public void disconnect() throws IOException
	{
		this.connector.disconnect();;
	}

	@Override
	public void shutdown()
	{
		this.connector.shutdown();
	}
}
