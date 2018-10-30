/**
 * 
 */
package com.toennies.ci1429.app.network.connector;

import java.io.IOException;

import com.toennies.ci1429.app.network.message.MessageTransformer;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.util.ASCII;

/**
 * @author renkenh
 *
 */
public class ConfigurableDataTransformer extends ADataTransformer
{

	private MessageTransformer transformer;
	
	
	/**
	 * @param connector
	 */
	public ConfigurableDataTransformer(IConnector<byte[]> connector)
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
		String start = config.hasEntry(PARAM_FRAME_START) ? config.getEntry(PARAM_FRAME_START) : null;
		String sep = config.hasEntry(PARAM_FRAME_SEP) ? config.getEntry(PARAM_FRAME_SEP) : null;
		String end = config.hasEntry(PARAM_FRAME_END) ? config.getEntry(PARAM_FRAME_END) : null;
		start = start != null ? ASCII.parseHuman(start) : null;
		sep = sep != null ? ASCII.parseHuman(sep) : null;
		end = end != null ? ASCII.parseHuman(end) : null;
		return new MessageTransformer(start, sep, end);
	}

	@Override
	protected MessageTransformer transformer()
	{
		return this.transformer;
	}

}
