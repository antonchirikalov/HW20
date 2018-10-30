/**
 * 
 */
package com.toennies.ci1429.app.util;

import java.util.Base64;

/**
 * @author renkenh
 *
 */
public class ScannerUtil
{

	public static final Object formatResponse(Object response)
	{
		if (response instanceof byte[])
			return Base64.getEncoder().encodeToString((byte[]) response);
		return response;
	}

	private ScannerUtil()
	{
		//no instance
	}

}
