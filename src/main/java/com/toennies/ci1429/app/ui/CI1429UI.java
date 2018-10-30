package com.toennies.ci1429.app.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventBus.ApplicationEventBus;
import org.vaadin.spring.events.EventBus.UIEventBus;

import com.toennies.ci1429.app.services.devices.DevicesService;
import com.toennies.ci1429.app.ui.panels.ContentSpringComponent;
import com.toennies.ci1429.app.ui.panels.testdevice.UITestService;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@SpringUI
@Theme("toennies")
@Push
public class CI1429UI extends UI
{

	@Autowired
	private ContentSpringComponent contentSpringComponent;
	
	@Autowired
	private EventBus.UIEventBus uiEventBus;
	
	@Autowired
	private EventBus.ApplicationEventBus appEventBus;
	
	@Autowired
	private DevicesService devicesService;

	@Autowired
	private UITestService testService;


	@Override
	protected void init(VaadinRequest request) {
		this.setContent(this.contentSpringComponent);
	}

	
	public UIEventBus getUIEventBus()
	{
		return this.uiEventBus;
	}
	
	public ApplicationEventBus getAppEventBus()
	{
		return this.appEventBus;
	}
	
	public DevicesService getDevicesService()
	{
		return this.devicesService;
	}

	public UITestService getUITestService()
	{
		return this.testService;
	}

}
