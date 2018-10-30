package com.toennies.ci1429.app.network.protocol.scale.hofelmeyer;

import com.toennies.ci1429.app.network.connector.ConfigurableDataTransformer;
import com.toennies.ci1429.app.network.connector.IConnector;
import com.toennies.ci1429.app.network.message.MessageTransformer;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.util.ASCII;

import java.io.IOException;

public class HofelmeyerConfigurableDataTransformer extends ConfigurableDataTransformer
{
	private MessageTransformer transformer;

	public HofelmeyerConfigurableDataTransformer(IConnector<byte[]> connector)
	{
		super(connector);
	}

	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		this.transformer = this.createTransformer(config);
		super.connect(config);
	}

	protected MessageTransformer createTransformer(IConfigContainer config)
	{
		String startControl = config.hasEntry(PARAM_FRAME_START) ? config.getEntry(PARAM_FRAME_START) : null;
		String endControl = config.hasEntry(PARAM_FRAME_END) ? config.getEntry(PARAM_FRAME_END) : null;
		startControl = (startControl != null) ? ASCII.parseHuman(startControl) : null;
		endControl = (endControl != null) ? ASCII.parseHuman(endControl) : null;
		return new HofelmeyerMSGTransformer(startControl, endControl);
	}

	@Override
	protected MessageTransformer transformer()
	{
		return this.transformer;
	}
}
