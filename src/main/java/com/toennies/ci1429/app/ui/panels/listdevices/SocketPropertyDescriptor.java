/**
 * 
 */
package com.toennies.ci1429.app.ui.panels.listdevices;

import javax.validation.constraints.NotNull;

import com.toennies.ci1429.app.model.IDevice;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.network.socket.CupsSocket;
import com.toennies.ci1429.app.network.socket.FileSocket;
import com.toennies.ci1429.app.network.socket.LPTSocket;
import com.toennies.ci1429.app.network.socket.RS232Socket;
import com.toennies.ci1429.app.network.socket.TCPSocket;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.VaadinPropertyDescriptor;

/**
 * @author renkenh
 *
 */
class SocketPropertyDescriptor implements VaadinPropertyDescriptor<IDevice>
{

	public static final String NAME = IProtocol.PARAM_SOCKET;


	public static class SocketProperty extends AbstractProperty<String> implements Property<String>
	{
		
		private final IDevice bean;
		
		
		public SocketProperty(IDevice device)
		{
			this.bean = device;
			super.setReadOnly(false);
		}
	
		
		@Override
		public @NotNull String getValue()
		{
			String socketClassname = this.bean.returnParameterValueByKey(IProtocol.PARAM_SOCKET);
			if (socketClassname == null)
				return "Not Defined";

			if (TCPSocket.class.getName().equals(socketClassname))
				return "[TCP] "+this.getParameter(TCPSocket.PARAM_HOST) + ":" + this.getParameter(TCPSocket.PARAM_PORT);

			if (RS232Socket.class.getName().equals(socketClassname))
				return "[RS232] "+this.getParameter(RS232Socket.PARAM_PORT);

			if (CupsSocket.class.getName().equals(socketClassname))
			{
				return "[CUPS] "+this.getParameter(CupsSocket.PARAM_CUPS_SERVER)+":"+
								 this.getParameter(CupsSocket.PARAM_PRINTER_NAME);
			}

			if (FileSocket.class.getName().equals(socketClassname))
				return "[FILE] "+this.getParameter(FileSocket.PARAM_FILEPATH);

			if (LPTSocket.class.getName().equals(socketClassname))
				return "[LPT] "+this.getParameter(LPTSocket.PARAM_PORT);
			
			return socketClassname;
		}
		
		private final @NotNull String getParameter(Object key)
		{
			String value = this.bean.getConfiguration().get(key);
			return value != null ? value : "";
		}

	    @Override
	    public void fireValueChange()
	    {
	        super.fireValueChange();
	    }

	    @Override
		public void setValue(String newValue) throws com.vaadin.data.Property.ReadOnlyException
		{
			throw new ReadOnlyException();
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


	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public Class<?> getPropertyType()
	{
		return String.class;
	}

	@Override
	public Property<?> createProperty(IDevice bean)
	{
		return new SocketProperty(bean);
	}

	private static final long serialVersionUID = 1L;

}
