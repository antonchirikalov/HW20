package com.toennies.ci1429.app.ui.components;

import java.util.Locale;

import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.model.IDevice.DeviceState;
import com.toennies.ci1574.lib.helper.Generics;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.FontAwesome;


/**
 * This converter is neeeded in order to display the device state in a 'traffic
 * light' way. See {@link DeviceState} and ticket CI1429APP-42
 */
@SuppressWarnings("serial")
public class TrafficLightConverter implements Converter<String, IDevice.DeviceState>
{

	// Because this class has no state, only a single instance is needed.
	public final static TrafficLightConverter INSTANCE = new TrafficLightConverter();

	private static final String COLOR_ICON = FontAwesome.SQUARE.getHtml().replace("font-family:", "color: {};font-family:");

	protected enum ColorMap
	{
		BLUE(IDevice.DeviceState.NOT_INITIALIZED),
		YELLOW(IDevice.DeviceState.INITIALIZED),
		GREEN(IDevice.DeviceState.CONNECTED),
		RED(IDevice.DeviceState.FAULTY);
		
		private final IDevice.DeviceState deviceState;
		
		private ColorMap(IDevice.DeviceState deviceState)
		{
			this.deviceState = deviceState;
		}
		
		public static final ColorMap getColor(IDevice.DeviceState deviceState)
		{
			for (ColorMap c : ColorMap.values())
				if (c.deviceState == deviceState)
					return c;
			return BLUE;
		}
	}

	
	@Override
	public Class<IDevice.DeviceState> getModelType()
	{
		return Generics.convertUnchecked(IDevice.DeviceState.class);
	}

	@Override
	public Class<String> getPresentationType()
	{
		return String.class;
	}

	@Override
	public IDevice.DeviceState convertToModel(String value, Class<? extends IDevice.DeviceState> targetType, Locale locale) throws ConversionException
	{
		return null;
	}

	@Override
	public String convertToPresentation(IDevice.DeviceState value, Class<? extends String> targetType, Locale locale) throws ConversionException
	{
		return COLOR_ICON.replace("{}", ColorMap.getColor(value).toString());
	}


	private TrafficLightConverter()
	{
		//no instance
	}

}
