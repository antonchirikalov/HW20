/**
 * 
 */
package com.toennies.ci1429.app.ui.panels.listdevices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.network.event.IEventHandler;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.ui.panels.listdevices.ParameterPropertyDescriptor.ParameterProperty;
import com.toennies.ci1429.app.ui.panels.listdevices.SocketPropertyDescriptor.SocketProperty;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.UI;

/**
 * @author renkenh
 *
 */
class DeviceItem extends BeanItem<IDevice> implements Item, IEventHandler, Comparable<DeviceItem>
{
	
	
	static final String[] BEAN_PROPERTIES   = { "deviceState", "deviceID", "type", "deviceModel", "vendor" };
	static final String[] PARAMS_PROPERTIES = { };


	private final UI ui;


	public DeviceItem(IDevice device, UI ui)
	{
		super(device, BEAN_PROPERTIES);
		for (String property : DeviceItem.PARAMS_PROPERTIES)
			this.addItemProperty(property, new ParameterPropertyDescriptor.ParameterProperty(property, device));
		this.addItemProperty(SocketPropertyDescriptor.NAME, new SocketPropertyDescriptor.SocketProperty(device));
		this.ui = ui;
		device.registerEventHandler(this);
	}
	

	@Override
	public void handleEvent(String eventID, Object source, Object... params)
	{
		if (IDevice.EVENT_STATE_CHANGED.equals(eventID) || IDevice.EVENT_PARAMS_UPDATED.equals(eventID))
			this.ui.access(() -> this.handleEventInUI(eventID));
	}
	
	private void handleEventInUI(String eventID)
	{
		switch (eventID)
		{
			case IDevice.EVENT_STATE_CHANGED:
				Property<?> stateProp = this.getItemProperty(BEAN_PROPERTIES[0]);
				fireValueChangeEvent(stateProp);
				break;
			case IDevice.EVENT_PARAMS_UPDATED:
				this.getItemPropertyIds().stream()
										 .map((id) -> this.getItemProperty(id))
										 .forEach((p) -> fireValueChangeEvent(p));
				break;
		}
	}

	private static final void fireValueChangeEvent(Property<?> property)
	{
		if (property instanceof MethodProperty)
			((MethodProperty<?>) property).fireValueChange();
		else if (property instanceof ParameterProperty)
			((ParameterProperty) property).fireValueChange();
		else if (property instanceof SocketProperty)
			((SocketProperty) property).fireValueChange();
	}

	@Override
	public int compareTo(DeviceItem o)
	{
		return this.getBean().getDeviceID() - o.getBean().getDeviceID();
	}

	public void dispose()
	{
		this.getBean().unregisterEventHandler(this);
	}


	public static final String getIdProperty()
	{
		return BEAN_PROPERTIES[1];
	}

	public static final List<String> getPropertyModel()
	{
		ArrayList<String> model = new ArrayList<>();
		model.addAll(Arrays.asList(BEAN_PROPERTIES));
		model.addAll(Arrays.asList(PARAMS_PROPERTIES));
		model.add(IProtocol.PARAM_SOCKET);
		return model;
	}

	@Override
	public boolean removeItemProperty(Object id) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}
	
	
	private static final long serialVersionUID = 7393934661743335954L;

}
