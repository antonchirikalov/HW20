package com.toennies.ci1429.app.model.printer;

import java.util.ArrayList;
import java.util.List;

/**
 * This structure holds all the ICS template data. Its the java ICS model.
 * @author renkenh
 */
public final class ICSTemplate
{

	/**
	 * The type that holds the data for a field on a label.
	 * @author renkenh
	 */
	public final class FieldData
	{
		/** The field id. Used in the label data to reference this field. */
		public final String fieldId;
		/** The xPos of the field. */
		public final int xPos;
		/** The yPos of this field. */
		public final int yPos;
		/** The number of characters that this field can contain. If length > 0 it is a fixed size field. */ 
		public final int length;
		/** The font ID. */
		public final int font;
		/** The width of the field. */
		public final int xWidth;
		/** The height of the field. */
		public final int yHeight;
		/** The rotation of the field. */
		public final String rotation;
		/** The text to add to this field. */
		public final String text;
		
		/** The template this field belongs to. */
		public final ICSTemplate format = ICSTemplate.this;
		
		private FieldData(String fieldId, int xPos, int yPos, int length, int font, int xWidth, int yWidth, String rotation, String text)
		{
			this.fieldId = fieldId;
			this.xPos = xPos;
			this.yPos = yPos;
			this.length = length;
			this.font = font;
			this.xWidth = xWidth;
			this.yHeight = yWidth;
			this.rotation = rotation;
			this.text = text;
		}
		
	}
	
	/** The character density of this template. Used to calculate xPos, yPos, etc. for a TEXT field. */
	public final int density;
	/** The speed with which to print the data. */
	public final String printSpeed;
	/** The id of the characterset to use. */
	public final String characterSet;
	/** The length (height?) of this label. */
	public final int labelLength;
	/** The home pos x of the label - probably the initial position on the label itself. */
	public final int homePosX;
	/** The home pos y of the label - probably the initial position on the label itself. */
	public final int homePosY;
	/** The direction of the text on the label. */
	public final String direction; // ausricht
	/** The quantity - how often the data + template should be printed. */
	public final int printQuantity;
	/** 'SchnickSchnack' */				//FIXME ask Schipper where this belongs
	public final String additional;

	private final List<FieldData> fields = new ArrayList<>();
	
	
	/**
	 * Constructor for the type.
	 */
	public ICSTemplate(int density, String speed, String characterSet, int labelLength, int homePosX, int homePosY, String direction, int quantity, String additional)
	{
		this.density = density;
		this.printSpeed = speed;
		this.characterSet = characterSet;
		this.labelLength = labelLength;
		this.homePosX = homePosX;
		this.homePosY = homePosY;
		this.direction = direction;
		this.printQuantity = quantity;
		this.additional = additional;
	}
	
	/**
	 * Adds a new field to the template.
	 */
	public void addField(String fieldId, int xPos, int yPos, int length, int font, int xWidth, int yWidth, String rotation, String text)
	{
		this.fields.add(new FieldData(fieldId, xPos, yPos, length, font, xWidth, yWidth, rotation, text));
	}
	
	/**
	 * Returns all the fields contained in this template.
	 * @return All the fields contained in this template.
	 */
	public List<FieldData> getFields()
	{
		return this.fields;
	}

}