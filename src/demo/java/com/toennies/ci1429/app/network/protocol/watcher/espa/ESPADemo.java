/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.watcher.espa;

import com.toennies.ci1429.app.model.watcher.Watcher;

/**
 * @author renkenh
 *
 */
public class ESPADemo
{

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		
		Watcher watcher = new Watcher(Utils.descriptionTCP());

		watcher.activateDevice();
		for (;;)
		{
			Thread.sleep(1000);
//			System.out.println();
		}
	}

}
