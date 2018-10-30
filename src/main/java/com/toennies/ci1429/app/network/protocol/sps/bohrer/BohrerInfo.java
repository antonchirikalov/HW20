/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.sps.bohrer;

import com.toennies.ci1429.app.network.protocol.sps.telegram.AtTelegramMapping;
import com.toennies.ci1429.app.network.protocol.sps.telegram.Telegram;
import com.toennies.ci1429.app.util.binary.pojo.AtBField;

/**
 * @author renkenh
 *
 */
@AtTelegramMapping(id=2,dialog=1)
public class BohrerInfo extends Telegram
{

	@AtBField
	private short foerdererID;
	@AtBField
	private int takt;
	@AtBField
	private short stich;
	@AtBField
	private short puls;
	@AtBField
	private int transponder;
	@AtBField
	private short addInfo;
	

	public short getFoerdererID()
	{
		return foerdererID;
	}
	
	public void setFoerdererID(int foerdererID)
	{
		this.foerdererID = (short) foerdererID;
	}
	
	public int getTakt()
	{
		return takt;
	}
	
	public void setTakt(int takt)
	{
		this.takt = takt;
	}
	
	public short getStich()
	{
		return stich;
	}
	
	public void setStich(int stich)
	{
		this.stich = (short) stich;
	}
	
	public short getPuls()
	{
		return puls;
	}
	
	public void setPuls(int puls)
	{
		this.puls = (short) puls;
	}
	
	public int getTransponder()
	{
		return transponder;
	}
	
	public void setTransponder(int transponder)
	{
		this.transponder = transponder;
	}
	
	public short getAddInfo()
	{
		return addInfo;
	}
	
	public void setAddInfo(int addInfo)
	{
		this.addInfo = (short) addInfo;
	}
	
}
