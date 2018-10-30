package com.toennies.ci1429.app.ui.panels.listdevices;

import javax.validation.constraints.NotNull;

import com.toennies.ci1429.app.model.IDevice;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.VaadinPropertyDescriptor;

class ParameterPropertyDescriptor implements VaadinPropertyDescriptor<IDevice>
{
	
	public static class ParameterProperty extends AbstractProperty<String> implements Property<String>
	{
		
		private final Object key;
		private final IDevice bean;
	

		public ParameterProperty(Object key, IDevice bean)
		{
			this.key = key;
			this.bean = bean;
			super.setReadOnly(false);
		}
	

		@Override
		public @NotNull String getValue()
		{
			return String.valueOf(this.bean.getConfiguration().get(this.key));
		}

		@Override
		public void setValue(String newValue) throws com.vaadin.data.Property.ReadOnlyException
		{
			throw new ReadOnlyException();
		}

	    @Override
	    public void fireValueChange()
	    {
	        super.fireValueChange();
	    }

	    @Override
	    public void setReadOnly(boolean newStatus)
	    {
	    	//do nothing
	    }

		@Override
		public Class<? extends String> getType()
		{
			return String.class;
		}
		
		private static final long serialVersionUID = 1L;
	}
	

	private final Object key;

	public ParameterPropertyDescriptor(Object key)
	{
		this.key = key;
	}

	@Override
	public String getName()
	{
		return String.valueOf(this.key);
	}

	@Override
	public Class<?> getPropertyType()
	{
		return String.class;
	}

	@Override
	public Property<?> createProperty(IDevice bean)
	{
		return new ParameterProperty(this.key, bean);
	}

	private static final long serialVersionUID = 1L;

}
