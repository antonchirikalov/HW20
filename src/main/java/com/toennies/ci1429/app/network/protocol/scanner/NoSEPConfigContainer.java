/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scanner;

import java.util.HashMap;
import java.util.Map;

import com.toennies.ci1429.app.network.connector.ADataTransformer;
import com.toennies.ci1429.app.network.parameter.AConfigContainer;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;

/**
 * @author renkenh
 *
 */
class NoSEPConfigContainer extends AConfigContainer
{

	private final HashMap<String, String> config;
	private final String frameSep;


	/**
	 * 
	 */
	public NoSEPConfigContainer(IConfigContainer container)
	{
		HashMap<String, String> copy = new HashMap<>(container.getConfig());
		this.frameSep = copy.remove(ADataTransformer.PARAM_FRAME_SEP);
		this.config = copy;
	}
	
	
	public String frameSep()
	{
		return this.frameSep;
	}

	@Override
	protected Map<String, String> _config()
	{
		return this.config;
	}
}
