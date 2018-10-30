/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.sps.telegram;

import com.toennies.ci1429.app.util.binary.pojo.AtBField;
import com.toennies.ci1429.app.util.binary.pojo.BPojoReader;
import com.toennies.ci1429.app.util.binary.pojo.BPojoWriter;

/**
 * Basis type of the user payload defined by sps connections within Tönnies. The basic Tönnies telegram consists of three values:
 * An id, a dialog number and a telegram number.
 * The id describes the process to be executed. The dialog is an identifier for the message within the process. That means, id and dialog together
 * unqiuely describe a telegram for a sps. The telegram number must be continuously counting within the range 0..32000 - it must be reset after reaching 32000.
 * 
 * This type is a bean, perpared to be used with the binary package {@link BPojoReader} and {@link BPojoWriter}.
 * @author renkenh
 */
public class Telegram
{

	@AtBField
	private byte id;
	@AtBField
	private byte dialog;
	@AtBField
	private short number;
	

	/**
	 * The id of the process.
	 * @return The id of the process.
	 */
	public byte getId()
	{
		return id;
	}

	/**
	 * Set the id of the process.
	 * @param id The id of the process.
	 */
	public void setId(int id)
	{
		this.id = (byte) id;
	}

	/**
	 * Returns the id of the message within a process.
	 * @return The id of the message within a process.
	 */
	public byte getDialog()
	{
		return dialog;
	}
	
	/**
	 * Set the message id.
	 * @param dialog The message id.
	 */
	public void setDialog(int dialog)
	{
		this.dialog = (byte) dialog;
	}

	/**
	 * Returns the number of the telegram.
	 * @return The number of the telegram.
	 */
	public short getNumber()
	{
		return number;
	}

	/**
	 * Sets the number of the telegram.
	 * @param number The number to be set.
	 */
	public void setNumber(int number)
	{
		this.number = (short) number;
	}

	/**
	 * Generates a unique identifier for the this telegram based on the process id {@link #getId()} and the telegram id within the process {@link #getDialog()}. The unique
	 * id follows the scheme defined by the portal team.
	 * @return The unqiue id of the telegram type.
	 */
	public String getKey()
	{
		return this.getId() + "|" + this.getDialog();
	}
}
