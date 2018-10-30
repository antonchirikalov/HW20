package com.toennies.ci1429.app.network.protocol.sps.bohrer;

import com.toennies.ci1429.app.network.protocol.sps.telegram.AtTelegramMapping;
import com.toennies.ci1429.app.network.protocol.sps.telegram.Telegram;
import com.toennies.ci1429.app.util.binary.pojo.AtBField;
import com.toennies.ci1429.app.util.binary.pojo.AtFixedBField;

@AtTelegramMapping(id=1,dialog=2)
public class StatusResponse extends Telegram
{
	
	@AtBField
	private short infoId;
	@AtBField
	private short statusType;
	@AtBField
	private short operatingMode;
	@AtFixedBField(10)
	private byte[] empties = new byte[10];
	
	
	public short getInfoId()
	{
		return infoId;
	}

	public void setInfoId(short infoId)
	{
		this.infoId = infoId;
	}
	
	public short getOperatingMode()
	{
		return operatingMode;
	}

	public void setOperatingMode(short operatingMode)
	{
		this.operatingMode = operatingMode;
	}

	public short getStatusType()
	{
		return statusType;
	}
	
	public void setStatusType(short statusType)
	{
		this.statusType = statusType;
	}

	public void setEmpties(byte[] empties)
	{
		this.empties = empties;
	}

}
