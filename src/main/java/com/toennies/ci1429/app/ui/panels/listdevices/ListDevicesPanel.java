package com.toennies.ci1429.app.ui.panels.listdevices;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.spring.events.EventBusListener;

import com.toennies.ci1429.app.model.DeviceException;
import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.IDevice.DeviceState;
import com.toennies.ci1429.app.services.devices.IDevicesService;
import com.toennies.ci1429.app.services.logging.ILogbookService;
import com.toennies.ci1429.app.ui.CI1429UI;
import com.toennies.ci1429.app.ui.components.TrafficLightConverter;
import com.toennies.ci1429.app.ui.panels.createdevice.UINewDeviceService;
import com.toennies.ci1429.app.ui.panels.specdevice.DeviceSpecificWindow;
import com.toennies.ci1501.lib.components.DeleteButton;
import com.toennies.ci1574.lib.helper.Generics;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class ListDevicesPanel extends VerticalLayout implements EventBusListener<IDevice>
{
    
    private UINewDeviceService uiNewDeviceService;
    
    private ILogbookService logBookService;
    
	private final static Logger logger = LogManager.getLogger();

	private final BeanContainer<Integer, IDevice> gridContainer = new BeanContainer<Integer, IDevice>(IDevice.class)
	{
		{
			for (String property : DeviceItem.PARAMS_PROPERTIES)
				this.addContainerProperty(property, new ParameterPropertyDescriptor(property));
			this.addContainerProperty(SocketPropertyDescriptor.NAME, new SocketPropertyDescriptor());
		}
		@Override
		protected BeanItem<IDevice> createBeanItem(IDevice bean)
		{
			if (bean == null)
				return null;
			return new DeviceItem(bean, ListDevicesPanel.this.getUI());
		}
	};

    private final Table devicesTable = new Table();
    {
        this.devicesTable.setReadOnly(true);
        this.devicesTable.setSizeFull();
        this.devicesTable.setContainerDataSource(gridContainer);
        this.devicesTable.setVisibleColumns(DeviceItem.getPropertyModel().toArray());
        this.devicesTable.addGeneratedColumn(DeviceItem.BEAN_PROPERTIES[0], this.getDeviceStateColumn());
        this.devicesTable.setColumnHeader(DeviceItem.BEAN_PROPERTIES[0], "Device State");
        this.devicesTable.setColumnHeader(DeviceItem.BEAN_PROPERTIES[1], "Device ID");
        this.devicesTable.setColumnHeader(DeviceItem.BEAN_PROPERTIES[2], "Type");
        this.devicesTable.setColumnHeader(DeviceItem.BEAN_PROPERTIES[3], "Device Model");
        this.devicesTable.setColumnHeader(DeviceItem.BEAN_PROPERTIES[4], "Vendor");
        this.devicesTable.setColumnHeader(SocketPropertyDescriptor.NAME, "Socket");
        this.devicesTable.addGeneratedColumn("Tools", this.getToolsColumn());
        this.devicesTable.setSelectable(true);
        this.devicesTable.setImmediate(true);
        this.setSizeFull();
        this.addComponent(this.devicesTable);
    }
	
    private ColumnGenerator getDeviceStateColumn() {
        return (source, itemId, columnId) -> {
            Property<?> property = source.getItem(itemId).getItemProperty(DeviceItem.BEAN_PROPERTIES[0]);
            Label label = new Label();
            label.setContentMode(ContentMode.HTML);
            label.setConverter(TrafficLightConverter.INSTANCE);
            label.setPropertyDataSource(property);
            return label;
        };
    }
	 
    private ColumnGenerator getToolsColumn() {
        return (source, itemId, columnId) -> {
            HorizontalLayout toolsLayout = new HorizontalLayout();
            toolsLayout.setSpacing(true);
            Property<?> property = source.getItem(itemId).getItemProperty(DeviceItem.BEAN_PROPERTIES[0]);
            DeviceState deviceState = (DeviceState) property.getValue();
            if (DeviceState.NOT_INITIALIZED.equals(deviceState)) {
                Button initSelectedDevices = new Button(FontAwesome.PLAY);
                initSelectedDevices.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
                initSelectedDevices.addClickListener(e -> initDevices(source, itemId, columnId));
                initSelectedDevices.setDescription("Init selected device");
                toolsLayout.addComponent(initSelectedDevices);
            } else {
                Button shutdownSelectedDevices = new Button(FontAwesome.STOP);
                shutdownSelectedDevices.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
                shutdownSelectedDevices.addClickListener(e -> shutdownDevices(source, itemId, columnId));
                shutdownSelectedDevices.setDescription("Shutdown selected device");
                toolsLayout.addComponent(shutdownSelectedDevices);
            }
            Button detailSelectedDevices = new Button(FontAwesome.INFO);
            detailSelectedDevices.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
            detailSelectedDevices.setDescription("Details selected device");
            detailSelectedDevices.addClickListener(e -> showDetailsDevice(source, itemId, columnId));
            DeleteButton deleteSelectedDevices = new DeleteButton("", () -> deleteDevices(source, itemId, columnId));
            deleteSelectedDevices.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
            deleteSelectedDevices.setDescription("Delete selected device");
            toolsLayout.addComponent(detailSelectedDevices);
            toolsLayout.addComponent(deleteSelectedDevices);
            return toolsLayout;
        };
    }
    
    private void initialListSetup() 
    {
        IDevicesService service = this.getUI().getDevicesService();
        Set<IDevice> devices = service.getAllDevices();
        for (IDevice device : devices) {
            this.addNewDevice(device);
        }
    }

	private void addNewDevice(IDevice device)
	{
		BeanItem<IDevice> item = this.gridContainer.getItem(Integer.valueOf(device.getDeviceID()));
		if (item == null)
			this.gridContainer.addItem(Integer.valueOf(device.getDeviceID()), device);
	}

	private void removeDevice(IDevice device)
	{
		BeanItem<IDevice> item = this.gridContainer.getItem(Integer.valueOf(device.getDeviceID()));
		if (item != null)
		{
			this.gridContainer.removeItem(Integer.valueOf(device.getDeviceID()));
			((DeviceItem) item).dispose();
		}
		
	}

	private void deleteDevices(Table source, Object itemId, Object columnId)
	{
	    BeanItem<IDevice> item = Generics.convertUnchecked(source.getItem(itemId));
        IDevice device = item.getBean();
		if (device == null)
			return;
		this.getUI().getDevicesService().deleteDeviceById(device.getDeviceID());
	}

	private void initDevices(Table source, Object itemId, Object columnId)
	{
	    BeanItem<IDevice> item = Generics.convertUnchecked(source.getItem(itemId));
        IDevice device = item.getBean();
		if (device == null)
			return;
		try
		{
			device.activateDevice();
		}
		catch (DeviceException e)
		{
			this.handleOperationError(e);
		}
		devicesTable.refreshRowCache();
	}
	
	private void shutdownDevices(Table source, Object itemId, Object columnId)
	{
	    BeanItem<IDevice> item = Generics.convertUnchecked(source.getItem(itemId));
        IDevice device = item.getBean();
		if (device == null)
			return;
		device.deactivateDevice();
		devicesTable.refreshRowCache();
	}

	private void showDeviceSpecificWindow(IDevice device)
	{
		if (device == null)
			return;
		DeviceSpecificWindow specDeviceWindow = new DeviceSpecificWindow(device, uiNewDeviceService, getUI().getUITestService(), logBookService);
		UI.getCurrent().addWindow(specDeviceWindow);
	}
	
	private void showDetailsDevice(Table source, Object itemId, Object columnId) {
	    BeanItem<IDevice> item = Generics.convertUnchecked(source.getItem(itemId));
	    IDevice device = item.getBean();
	    showDeviceSpecificWindow(device);
	}
	
	private void handleOperationError(DeviceException ex)
	{
		logger.error("Could not execute operation.", ex);
	}

	@Override
	public void onEvent(org.vaadin.spring.events.Event<IDevice> event)
	{
		switch (event.getTopic())
		{
			case IDevicesService.EVENT_NEW_DEVICE:
				this.getUI().access(() -> this.addNewDevice(event.getPayload()));
				break;
			case IDevicesService.EVENT_DEVICE_DELETED:
				this.getUI().access(() -> this.removeDevice(event.getPayload()));
				break;
		}
	}

	@Override
	public void attach()
	{
		super.attach();
		this.initialListSetup();
		this.getUI().getAppEventBus().subscribe(this);
	}

	@Override
	public void detach()
	{
		this.getUI().getAppEventBus().unsubscribe(this);
		super.detach();
	}

	@Override
	public CI1429UI getUI()
	{
		return (CI1429UI) super.getUI();
	}
	
    public void init(UINewDeviceService uiNewDeviceService, ILogbookService logbookService)
    {
        this.uiNewDeviceService = uiNewDeviceService;
        this.logBookService = logbookService;
    }

}
