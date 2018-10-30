
package com.toennies.ci1429.app.ui.components;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.services.logging.LogEvent;
import com.toennies.ci1429.app.services.logging.LogEvent.EventType;
import com.toennies.ci1429.app.util.Colors;
import com.toennies.ci1429.app.util.FTLUtils;

import freemarker.template.Template;

/**
 * The Class HtmlDeviceEventsFormatter the formatter for building event log in
 * HTML format. Use freemaker with pre-defined template at
 * templates/deviceEventLogTemplate.ftl and combine data from current event to
 * build one line event log in HTML format.
 */
public class HtmlEventFormatter
{

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LogManager.getLogger();

	/** The Constant DEVICE_EVENT_LOG_TEMPLATE. */
	private static final String TEMPLATE_NAME = "eventLogPanel";
	private static final Template EVENT_LOG_TEMPLATE = FTLUtils.getTemplate(TEMPLATE_NAME);

	private static final int[] SYSTEM_COLORS = { Colors.BLACK, Colors.GREEN, Colors.BLUE, Colors.darker(Colors.ORANGE, 0.5), Colors.darker(Colors.VIOLET, 0.5) };

	public static final class EventWrapper
	{
		/** time format for the event panel. */
		private static final DateTimeFormatter FORMAT_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
				.withZone(ZoneId.systemDefault());
		private static final int MAX_LOGLINE_LENGTH = 130;

		private final LogEvent event;

		public EventWrapper(LogEvent event)
		{
			this.event = event;
		}

		public String getSystem()
		{
			return this.event.system;
		}

		private int baseColor()
		{
			return SYSTEM_COLORS[this.event.system.hashCode() % SYSTEM_COLORS.length];
		}
		
		public String getBaseColor()
		{
			return Colors.convertHex(this.baseColor());
		}

		public String getInColor()
		{
			return Colors.convertHex(Colors.brighter(this.baseColor()));
		}

		public String getOutColor()
		{
			return Colors.convertHex(Colors.darker(this.baseColor()));
		}

		public String getSourceID()
		{
			return this.event.sourceID;
		}

		public EventType getType()
		{
			return this.event.type;
		}

		public String getTime()
		{
			return FORMAT_TIME.format(this.event.timestamp);
		}

		public String getPayload()
		{
			return StringUtils.abbreviateMiddle(this.event.payload, "...", MAX_LOGLINE_LENGTH);
		}
	}

	/**
	 * Format the log event to HTML format
	 *
	 * @param event
	 *            the event happening
	 * @param isGlobal
	 *            the is global variable to check if this device is at global
	 *            scope
	 * @return the event log built with HTML tag included
	 */
	public static final String format(LogEvent[] events, boolean global)
	{
		try
		{
			Map<String, Object> model = new HashMap<>();
			model.put("global", Boolean.valueOf(global));
			model.put("events", Arrays.stream(events).map(EventWrapper::new).collect(Collectors.toList()));
			return FTLUtils.convert(model, EVENT_LOG_TEMPLATE);
		}
		catch (IOException e)
		{
			LOGGER.error("Could not process template for global event log", e);
			return null;
		}
	}

}
