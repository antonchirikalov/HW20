/**
 * 
 */
package com.toennies.ci1429.app.network.protocol;

import com.toennies.ci1429.app.network.connector.IConnectorWrapper;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.event.EventNotifierStub;
import com.toennies.ci1429.app.network.event.IEventHandler;
import com.toennies.ci1429.app.network.event.IEventNotifier;

/**
 * @author renkenh
 *
 */
class PipelineNotifierStub extends EventNotifierStub implements IEventHandler
{

	/**
	 * @param source
	 */
	public PipelineNotifierStub(Object source)
	{
		super(source);
	}


	protected void setupPipelineNotifier(IFlexibleConnector<?, ?> connector)
	{
		if (connector instanceof IEventNotifier)
			((IEventNotifier) connector).registerEventHandler(this);
		if (connector instanceof IConnectorWrapper)
			setupPipelineNotifier(((IConnectorWrapper<?, ?, ?>) connector).getWrappedConnector());
	}
	
	protected void setdownPipelineNotifier(IFlexibleConnector<?, ?> connector)
	{
		if (connector instanceof IEventNotifier)
			((IEventNotifier) connector).unregisterEventHandler(this);
		if (connector instanceof IConnectorWrapper)
			setdownPipelineNotifier(((IConnectorWrapper<?, ?, ?>) connector).getWrappedConnector());
	}

	@Override
	public void handleEvent(String eventID, Object source, Object... params)
	{
		this.publishEvent(eventID, params);
	}

}
