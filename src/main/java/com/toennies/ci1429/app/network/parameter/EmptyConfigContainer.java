/**
 * 
 */
package com.toennies.ci1429.app.network.parameter;

import java.util.Collections;
import java.util.Map;

/**
 * @author renkenh
 *
 */
public class EmptyConfigContainer extends AConfigContainer
{
	
	public final static IConfigContainer INSTANCE = new EmptyConfigContainer();


	@Override
	protected Map<String, String> _config()
	{
		return Collections.emptyMap();
	}
	
	

	private EmptyConfigContainer()
	{
		//no instance
	}

}
