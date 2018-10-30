package com.toennies.ci1429.app.ui.components;

import org.vaadin.spring.events.EventBusListener;

import com.toennies.ci1429.app.services.logging.ILogbookService;
import com.toennies.ci1429.app.services.logging.LogEvent;
import com.toennies.ci1429.app.ui.CI1429UI;
import com.toennies.ci1574.lib.helper.Generics;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

/**
 * This panel presents a list of events from the {@link ILogbookService}.
 * It uses an HTML formatter and the FTL engine to convert the events into HTML code.
 * It supports two modes: It can be used to show a global event log getting the event list by calling
 * {@link ILogbookService#latestEvents()}, but also supports to show a source specific event log by calling
 * {@link ILogbookService#latestEventsBySource(String)}. For the second mode, the source-name must be provided
 * in the constructor.
 * @author renkenh
 */
@SuppressWarnings("serial")
public class EventlogPanel extends Panel implements EventBusListener<LogEvent>
{
	private final ObjectProperty<LogEvent[]> property = new ObjectProperty<>(null, Generics.convertUnchecked(LogEvent[].class));

	private final Label eventTextArea = new Label();
	{
		eventTextArea.setContentMode(ContentMode.HTML);
		eventTextArea.setImmediate(true);
		eventTextArea.setPropertyDataSource(property);
		CssLayout cssLayout = new CssLayout(this.eventTextArea)
		{
		    @Override
		    protected String getCss(Component c) {
		        return "overflow: unset";
		    }
		};
		this.setSizeFull();
		this.setContent(cssLayout);
	}
	private final String sourceToTrack;
	private volatile ILogbookService logbook;


	/**
	 * Creates a global event log.
	 * {@link ILogbookService#latestEvents()} is used as event source.
	 */
	public EventlogPanel()
	{
		this(null);
	}
	
	/**
	 * Creates a source specific event log.
	 * {@link ILogbookService#latestEventsBySource(String)} is used as event source.
	 */
	public EventlogPanel(String sourceToTrack)
	{
		setIcon(FontAwesome.LIST);
		this.sourceToTrack = sourceToTrack;
		if (this.sourceToTrack == null)
		{
			this.setCaption("Global Event Log");
			this.eventTextArea.setConverter(new EventConverter(true));
		}
		else
		{
			this.setCaption("Device Event Log");
			this.eventTextArea.setConverter(new EventConverter());
		}
	}

	
	/**
	 * Used to introduce the {@link ILogbookService} instance.
	 * @param logbook The service from which to get the events
	 */
	public void init(ILogbookService logbook)
	{
		this.logbook = logbook;
		updateTextArea();
	}

	private void updateTextArea()
	{
		if (this.logbook == null)
			return;

		LogEvent[] events = null;
		if (this.sourceToTrack != null)
			events = this.logbook.latestEventsBySource(this.sourceToTrack);
		else 
			events = this.logbook.latestEvents();
		
		this.property.setValue(events);
	}
	
	@Override
	public void attach()
	{
		super.attach();
		this.getUI().getAppEventBus().subscribe(this);
		this.getUI().access(this::updateTextArea);
	}

	@Override
	public void detach()
	{
		this.getUI().getAppEventBus().unsubscribe(this);
		super.detach();
	}

	@Override
	public void onEvent(org.vaadin.spring.events.Event<LogEvent> event)
	{
		if (this.sourceToTrack == null || this.sourceToTrack.equals(event.getPayload().getSourceUID()))
			getUI().access(this::updateTextArea);
	}

	@Override
	public CI1429UI getUI()
	{
		return (CI1429UI) super.getUI();
	}

}
