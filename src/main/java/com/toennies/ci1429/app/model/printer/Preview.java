/**
 * 
 */
package com.toennies.ci1429.app.model.printer;

import org.springframework.http.MediaType;

/**
 * Type that represents a preview rendered by a printer. It contains information about the media type and
 * the actual image data.
 * @author renkenh
 */
public class Preview
{

	/** The media type. e.g. image/png */
	public final MediaType type;
	/** The raw image data. */
	public final byte[] imageData;
	

	/**
	 * Constructor.
	 */
	public Preview(MediaType type, byte[] imageData)
	{
		this.type = type;
		this.imageData = imageData;
	}

}
