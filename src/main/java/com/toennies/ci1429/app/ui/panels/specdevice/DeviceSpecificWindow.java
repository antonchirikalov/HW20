/*
 * 
 */

package com.toennies.ci1429.app.ui.panels.specdevice;


import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.services.logging.ILogbookService;
import com.toennies.ci1429.app.ui.components.EventlogPanel;
import com.toennies.ci1429.app.ui.panels.createdevice.UINewDeviceService;
import com.toennies.ci1429.app.ui.panels.testdevice.UITestService;
import com.toennies.ci1429.app.util.Utils;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * The Class SpecDeviceWindow.
 */
@SuppressWarnings("serial")
public class DeviceSpecificWindow extends Window {

    /** The device. */
    private IDevice device;

    /** The ui new device service. */
    private UINewDeviceService uiNewDeviceService;

    /** The ui test service. */
    private UITestService uiTestService;

    /** The log service. */
    private ILogbookService logService;

    /** The edit button. */
    private Button editButton;

    /** The close button. */
    private Button closeButton;

    /**
     * Instantiates a new specify device window.
     *
     * @param device the device
     * @param uiNewDeviceService the ui new device service
     * @param uiTestService the ui test service
     * @param logService the log service
     */
    public DeviceSpecificWindow(IDevice device, UINewDeviceService uiNewDeviceService, UITestService uiTestService, ILogbookService logService) {
        setHeight(90, Unit.PERCENTAGE);
        setWidth(90, Unit.PERCENTAGE);
        center();
        setModal(true);
        setResizable(false);
        setClosable(false);
        this.device = device;
        this.uiNewDeviceService = uiNewDeviceService;
        this.uiTestService = uiTestService;
        this.logService = logService;
        init();
    }

    /**
     * Inits the windows.
     */
    private void init() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        // create tabsheet
        TabSheet tabSheet = new TabSheet();
        Panel testDevicePanel = uiTestService.createTestPanel(device);
        DetailsDevicePanel detailsDeviceTab = new DetailsDevicePanel(device);
        EventlogPanel eventLogPanel = createEventLogTab();
        tabSheet.addTab(testDevicePanel, "Test");
        tabSheet.addTab(detailsDeviceTab, "Details");
        tabSheet.addTab(eventLogPanel, "Event Log");
        tabSheet.addStyleName(ValoTheme.TABSHEET_FRAMED);
        tabSheet.setSizeFull();
        tabSheet.addSelectedTabChangeListener(e -> {
            editButton.setVisible(e.getTabSheet().getSelectedTab().equals(detailsDeviceTab));
        });
        mainLayout.addComponent(tabSheet);
        // create buttons
        HorizontalLayout buttonLayout = buildButtonLayout();
        mainLayout.addComponent(buttonLayout);
        mainLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_RIGHT);
        
        mainLayout.setExpandRatio(tabSheet, 1);
        mainLayout.setExpandRatio(buttonLayout, 0);
        this.setContent(mainLayout);
    }

    /**
     * Builds the button layout.
     *
     * @param device the device
     */
    private HorizontalLayout buildButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        editButton = new Button("Edit");
        editButton.addClickListener(e -> uiNewDeviceService.startUpdateWizard(getUI(), device));
        closeButton = new Button("Close");
        editButton.setVisible(false);
        closeButton.addClickListener(e -> this.close());
        buttonLayout.addComponents(editButton, closeButton);
        buttonLayout.setSpacing(true);
        return buttonLayout;
    }

    /**
     * Creates the event log tab.
     *
     * @return the device events panel
     */
    private EventlogPanel createEventLogTab() {
        EventlogPanel eventLogTab = new EventlogPanel(Utils.createSourceByDeviceId(device.getDeviceID()));
        eventLogTab.init(logService);
        eventLogTab.setCaption(null);
        eventLogTab.setIcon(null);
        return eventLogTab;
    }

}
