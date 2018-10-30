/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.bizerba;

import java.util.concurrent.locks.ReentrantLock;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.scale.Commands.Command;
import com.toennies.ci1429.app.model.scale.Scale;

/**
 * @author renkenh
 *
 */
public class BizerbaDemo
{
	
	public static final void main(String[] args) throws Exception
	{
		Scale scale = new Scale(Utils.descriptionTCP());
		
		scale.activateDevice();
		
		DeviceResponse resp = scale.process(Command.WEIGH_DIRECT);
		System.out.println(resp.getStatus());
		
		resp = scale.process(Command.WEIGH_DIRECT);
		System.out.println(resp.getStatus());
		
		ReentrantLock lock = new ReentrantLock();
		lock.lock();
		lock.newCondition().await();
	}

}
