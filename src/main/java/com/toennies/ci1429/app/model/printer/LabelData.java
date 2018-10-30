package com.toennies.ci1429.app.model.printer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.restcontroller.PrinterRestController;

import freemarker.template.Template;

/**
 * 
 * This class is used for defining printjobs.
 * 
 * <pre>
  	{
		"templateFile" : "TEST2.ZPL",
		"fields" :
		{
			"11" : { "type" : "CHAR", "value": "This text gets printed." }
		}
	}
 * </pre>
 * 
 */
public class LabelData
{
	
	private static final Logger LOGGER = LogManager.getLogger();


	public enum ValueType
	{
		CHAR, HEX
	}

	/**
	 * Data that get's printed can be stored in several ways. Every
	 * {@link ValueType} represents another way to store the data.
	 */
	public static final class Value
	{
		
		/**
		 * {@link ValueType} information is needed for distinction in LabelFields.ftl
		 */
		private ValueType type;
		private String value;

		public Value()
		{
			//
		}

		public Value(String value)
		{
			this(ValueType.CHAR, value);
		}

		public Value(ValueType type, String value)
		{
			this.setType(type);
			this.setValue(value);
		}

		public void setValue(String value)
		{
			if (value == null)
			{
				value = "";	//empty string to prevent problems in template engine.
				LOGGER.debug("Got null value to print as field data. Replaced by empty string.");
			}
			this.value = value;
		}

		public void setType(ValueType type)
		{
			if (type == null)
				type = ValueType.CHAR;	//default to prevent problems in template engine
			this.type = type;
		}

		public ValueType getType() {
			return type;
		}

		/**
		 * Method is important because
		 * {@link Template#process(Object, java.io.Writer)} method invokes
		 * this toString method. See
		 * {@link PrinterRestController#putPrintBatch(int, LabelData, int)}
		 *
		 * So we can control here in which manner the data get's printed.
		 */
		@Override
		public String toString() {
			return value;
		}
	}

	private boolean smallFont;
	private boolean bigFont;
	private boolean isPreview;
	private String templateFile;
	private Map<String, Value> fields;
	
	public String getTemplateFile()
	{
		return templateFile;
	}
	
	public void setTemplate(String templateFile)
	{
		this.templateFile = templateFile;
	}
	
	public Collection<Entry<String, Value>> getFields()
	{
		return fields.entrySet();
	}
	
	public void addField(String key, Value value)
	{
		if (this.fields == null)
			this.fields = new HashMap<>();
		this.fields.put(key, value);
	}
	
	public void setFields(Map<String, Value> fields)
	{
		this.fields = fields;
	}
	
	public boolean getSmallFont()
	{
		return this.smallFont;
	}
	
	public boolean getBigFont()
	{
		return this.bigFont;
	}
	
	public boolean isPreview()
	{
		return this.isPreview;
	}
	
	public void setPreview(boolean preview)
	{
		this.isPreview = preview;
	}
	
	public void setFontSize(boolean smallFont, boolean bigFont)
	{
		this.smallFont = smallFont;
		this.bigFont = bigFont;
	}

}
