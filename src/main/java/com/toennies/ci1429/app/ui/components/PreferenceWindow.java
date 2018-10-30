
package com.toennies.ci1429.app.ui.components;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.toennies.ci1429.app.services.devicetemplates.DeviceTemplateService;
import com.toennies.ci1429.app.services.shutdown.ShutdownService;
import com.toennies.ci1480.lib.version.VersionFile;
import com.toennies.ci1480.lib.version.VersionFileReader;
import com.toennies.ci1501.lib.components.ActionConfirmationDialog;
import com.toennies.ci1501.lib.components.DeleteButton;
import com.toennies.ci1501.lib.components.MapGrid;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * The Class PreferenceWindow.
 */
@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class PreferenceWindow extends Window {

    /** The Constant WINDOW_SIZE. */
    private static final String WINDOW_WIDTH = "700px";

    /** The Constant WINDOW_SIZE. */
    private static final String WINDOW_HEIGHT = "550px";

    /** The shutdown service. */
    @Autowired
    private ShutdownService shutdownService;

    /** The device template service. */
    @Autowired
    @Qualifier("deviceTemplateServiceImpl")
    private DeviceTemplateService deviceTemplateService;

    /** The sync templates button. */
    private final Button syncTemplatesButton = new Button(FontAwesome.DATABASE);

    /** The shutdown button. */
    private final Button shutdownButton = new Button("Shutdown server", FontAwesome.BOLT);

    /** The close button. */
    private final Button closeButton = new Button("Close");

    /** The version info grid. */
    private Grid versionInfoGrid;

    /**
     * Instantiates a new preference window.
     */
    public PreferenceWindow() {
        this.setWidth(WINDOW_WIDTH);
        this.setHeight(WINDOW_HEIGHT);
        this.setModal(true);
        this.setResizable(false);
        this.setClosable(false);
        this.center();
        initialize();
    }

    /**
     * Initialize the window.
     */
    private void initialize() {
        VerticalLayout mainLayout = new VerticalLayout();
        // init system buttons
        shutdownButton.addStyleName(ValoTheme.BUTTON_DANGER);
        HorizontalLayout sysButtonLayout = new HorizontalLayout();
        sysButtonLayout.setSpacing(true);
        sysButtonLayout.addComponent(this.syncTemplatesButton);
        sysButtonLayout.addComponent(this.shutdownButton);
        this.syncTemplatesButton.setDescription("Synchronize templates");
        this.syncTemplatesButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        this.syncTemplatesButton.setEnabled(false);
        // init version info grid
        Map<String, String> versionInfoMap = getVersionInfo();
        versionInfoGrid = new MapGrid<>(versionInfoMap, "Key", "Value");
        versionInfoGrid.setHeaderVisible(false);
        versionInfoGrid.setSizeFull();
        // handle actions for buttons
        handleListeners();
        // add components to main layout
        mainLayout.addComponent(sysButtonLayout);
        mainLayout.addComponent(versionInfoGrid);
        mainLayout.addComponent(closeButton);
        mainLayout.setComponentAlignment(closeButton, Alignment.BOTTOM_RIGHT);
        mainLayout.setSpacing(true);
        mainLayout.setMargin(true);
        this.setContent(mainLayout);
    }

    /**
     * Handle listeners for buttons.
     */
    private void handleListeners() {
        // handle actions
        this.syncTemplatesButton.addClickListener(event -> {
            boolean synced = deviceTemplateService.syncTemplates();
            if (!synced)
                Notification.show("Error during sync of templates", Notification.Type.ERROR_MESSAGE);
        });

        this.shutdownButton.addClickListener(event -> {
            UI.getCurrent().addWindow(
                    new ActionConfirmationDialog(DeleteButton.DEFAULT_OK_TEXT, DeleteButton.DEFAULT_CANCEL_TEXT, "Do you want to shutdown server?", () -> {
                        shutdownService.shutdown();
                        PreferenceWindow.this.close();
                    }));
        });

        this.closeButton.addClickListener(event -> {
            PreferenceWindow.this.close();
        });
    }

    
    /**
     * Gets the version information.
     *
     * @return the map of version information
     */
    private Map<String, String> getVersionInfo() {
        VersionFile versionFile = VersionFileReader.readVersionFile();
        return versionFile.properties();
    }

}
