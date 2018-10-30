/*
 * ContentSpringComponent.java
 * 
 * Created on Mar 28, 2017
 * 
 * Copyright (C) 2017 Toennies, All rights reserved.
 */
package com.toennies.ci1429.app.ui.panels;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.toennies.ci1429.app.services.logging.ILogbookService;
import com.toennies.ci1429.app.ui.components.EventlogPanel;
import com.toennies.ci1429.app.ui.components.PreferenceWindow;
import com.toennies.ci1429.app.ui.panels.createdevice.UINewDeviceService;
import com.toennies.ci1429.app.ui.panels.listdevices.ListDevicesPanel;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class ContentSpringComponent extends Panel
{
	
    /** HW2.0 logo under webapp/VAADIN/themes/toennies/images */
    private static final String LOGO = "images/logo.svg";

    @Autowired
	private UINewDeviceService uiNewDeviceService;

	@Autowired
	private ILogbookService logbookService;
	
	@Value("${help.url}")
	private String helpUrl;
	
	@Autowired
	private PreferenceWindow preferenceWindow;
	private final Image hwLogo = new Image(null, new ThemeResource(LOGO));
	private final Label spaceLabel = new Label();
	private final ListDevicesPanel listDevicesPanel = new ListDevicesPanel();
	private final EventlogPanel deviceEventsPanel = new EventlogPanel();
	private final Button newDeviceButton = new Button(FontAwesome.PLUS);
	private final Button sysPreferencesButton = new Button(FontAwesome.COGS);
	private final Button helpButton = new Button(FontAwesome.QUESTION);
	{
		newDeviceButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
	    this.newDeviceButton.setDescription("New device");
	    this.newDeviceButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
		this.newDeviceButton.addClickListener(event -> this.uiNewDeviceService.startWizard(this.getUI()));

		this.sysPreferencesButton.setDescription("System Preference");
		this.sysPreferencesButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
		this.helpButton.setDescription("Help");
		this.helpButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
		this.sysPreferencesButton.addClickListener(event -> this.getUI().addWindow(preferenceWindow));
	}
	
	private final VerticalLayout vLayout = new VerticalLayout();
	private final HorizontalLayout headerLayout = new HorizontalLayout();
	{	    
		this.hwLogo.setHeight(37, Unit.PIXELS);
		this.hwLogo.setWidth(50, Unit.PIXELS);
		
		this.headerLayout.addComponents(hwLogo, spaceLabel, newDeviceButton, sysPreferencesButton, helpButton);
		this.headerLayout.setExpandRatio(spaceLabel, 1);
		this.headerLayout.setWidth("100%");
		this.headerLayout.setSpacing(true);

		this.vLayout.addComponents(headerLayout, listDevicesPanel, deviceEventsPanel);
		this.vLayout.setSpacing(true);
		this.vLayout.setMargin(true);
		this.vLayout.setHeight("100%");
		this.vLayout.setExpandRatio(headerLayout, 0);
		this.vLayout.setExpandRatio(listDevicesPanel, 1);
		this.vLayout.setExpandRatio(deviceEventsPanel, 1);
	}
	
	public ContentSpringComponent()
	{
		this.setSizeFull();
		this.setContent(vLayout);
	}
	
	@PostConstruct
	private void init()
	{
		this.deviceEventsPanel.init(this.logbookService);
		this.listDevicesPanel.init(uiNewDeviceService, logbookService);
		// open external link for help button
		BrowserWindowOpener browserWindowOpener = new BrowserWindowOpener(helpUrl);
		browserWindowOpener.extend(helpButton);
	}

}
