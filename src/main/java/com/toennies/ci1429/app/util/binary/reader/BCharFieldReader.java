/**
 * 
 */
package com.toennies.ci1429.app.util.binary.reader;

import com.toennies.ci1429.app.util.binary.model.BField;

/**
 * Implementation of the {@link ABFieldReader} class to read a character.
 * @author renkenh
 */
public class BCharFieldReader extends ABFieldReader
{

	/**
	 * Constructor.
	 * @param field The field this reader is associated with.
	 * @param position The position where the character is located.
	 * @param rawData The data to read.
	 */
	public BCharFieldReader(BField field, int position, byte[] rawData)
	{
		super(field, position, rawData);
	}


	@Override
	public Character getValue()
	{
		return Character.valueOf((char) this.rawData[this.position]);
	}

}
