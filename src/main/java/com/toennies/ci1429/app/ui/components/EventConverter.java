/**
 * 
 */
package com.toennies.ci1429.app.ui.components;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import org.vaadin.spring.events.Event;

import com.toennies.ci1429.app.services.logging.LogEvent;
import com.toennies.ci1574.lib.helper.Generics;
import com.vaadin.data.util.converter.Converter;

/**
 * Event Converter that uses an instance of a {@link HtmlEventFormatter} to convert events into html tags (in a string).
 * @author renkenh
 */
public class EventConverter implements Converter<String, LogEvent[]>
{

	private static final Comparator<LogEvent> TIME_COMPARATOR = (e1, e2) ->
	{
		return e2.timestamp.compareTo(e1.timestamp);
	};
	
	
	private final boolean global;
	
	/**
	 * Creates a local event converter by providing a {@link HtmlEventFormatter} instance.
	 */
	public EventConverter()
	{
		this(false);
	}
	
	/**
	 * Constructor to provide different formatters to change the apperance of the events.
	 * @param formatter The formatter to use in {@link #convertToPresentation(Event[], Class, Locale)}.
	 */
	protected EventConverter(boolean global)
	{
		this.global = global;
	}


	@Override
	public String convertToPresentation(LogEvent[] value, Class<? extends String> targetType, Locale locale) throws ConversionException
	{
		if (value == null)
			return "Error: Unknown Event Source";
		
		Arrays.sort(value, TIME_COMPARATOR);
		return HtmlEventFormatter.format(value, this.global);
	}

	@Override
	public LogEvent[] convertToModel(String value, Class<? extends LogEvent[]> targetType, Locale locale) throws ConversionException
	{
		return null;
	}

	@Override
	public Class<LogEvent[]> getModelType()
	{
		return Generics.convertUnchecked(LogEvent[].class);
	}

	@Override
	public Class<String> getPresentationType()
	{
		return String.class;
	}


	private static final long serialVersionUID = 1L;

}
