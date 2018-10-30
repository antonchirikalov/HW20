/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.sps.bohrer;

import com.toennies.ci1429.app.network.protocol.sps.telegram.AtTelegramMapping;
import com.toennies.ci1429.app.network.protocol.sps.telegram.Telegram;
import com.toennies.ci1429.app.util.binary.pojo.AtBField;
import com.toennies.ci1429.app.util.binary.pojo.AtFixedBField;

/**
 * @author renkenh
 *
 */
@AtTelegramMapping(id=1,dialog=1)
public class StatusRequest extends Telegram
{
	@AtBField
	private short requestId;
	@AtBField
	private short statusType;
	@AtFixedBField(12)
	private byte[] empties = new byte[12];

	/**
	 * 
	 */
	public StatusRequest()
	{
		this.setId(1);
		this.setDialog(1);
	}

	public short getRequestId()
	{
		return requestId;
	}

	public void setRequestId(short requestId)
	{
		this.requestId = requestId;
	}

	public short getStatusType()
	{
		return statusType;
	}

	public void setStatusType(short statusType)
	{
		this.statusType = statusType;
	}

	public byte[] getEmpties()
	{
		return empties;
	}
	
}
