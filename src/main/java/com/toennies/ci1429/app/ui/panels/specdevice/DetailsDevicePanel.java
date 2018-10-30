
package com.toennies.ci1429.app.ui.panels.specdevice;


import java.util.LinkedHashMap;
import java.util.Map;

import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.network.event.IEventHandler;
import com.toennies.ci1429.app.ui.components.ExtendedMapGrid;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

/**
 * The Class DetailsDevicePanel. Show a grid detail info of a device.
 */
@SuppressWarnings("serial")
public class DetailsDevicePanel extends Panel implements IEventHandler
{

	private final ExtendedMapGrid detailsDeviceGrid = new ExtendedMapGrid("Detail Device", "Key", "Value");
	private final IDevice device;
	{
		this.setSizeFull();
		detailsDeviceGrid.setEditorEnabled(false);
		detailsDeviceGrid.setSizeFull();
		setContent(detailsDeviceGrid);
	}

    /**
     * Instantiates a new details device panel.
     *
     * @param device the device
     */
    public DetailsDevicePanel(IDevice device)
    {
        this.device = device;
    }

    /**
     * Initialize the details device grid.
     */
    private void initializeData()
    {
        Map<String, String> detailsDevice = new LinkedHashMap<>();
        detailsDevice.put("Device ID", String.valueOf(device.getDeviceID()));
        detailsDevice.put("Device Type", device.getType().toString());
        detailsDevice.put("Model", device.getDeviceModel());
        detailsDevice.put("Vendor", device.getVendor());
        detailsDevice.put("Protocol", device.getProtocolClass());
        detailsDevice.putAll(device.getConfiguration());
        this.detailsDeviceGrid.setRows(detailsDevice);
    }

	@Override
	public void attach()
	{
		super.attach();
		this.initializeData();
		this.device.registerEventHandler(this);
	}

	@Override
	public void detach()
	{
		this.device.unregisterEventHandler(this);
		super.detach();
	}


	@Override
	public void handleEvent(String eventID, Object source, Object... params)
	{
		if (!IDevice.EVENT_PARAMS_UPDATED.equals(eventID))
			return;
		
		UI ui = this.getUI();
		if (ui != null)
			ui.access(this::initializeData);
	}

}
