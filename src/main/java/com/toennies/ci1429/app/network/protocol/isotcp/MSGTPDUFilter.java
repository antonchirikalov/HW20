/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.isotcp;

import com.toennies.ci1429.app.network.connector.AFlexibleWrapperTransformer;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.message.IExtendedMessage;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.protocol.isotcp.tpdu.TPDU;

/**
 * Simple tpdu filter. It drops all incoming messages that are not of type {@link TPDU}.
 * @author renkenh
 */
public class MSGTPDUFilter extends AFlexibleWrapperTransformer<IMessage, TPDU, IMessage, IExtendedMessage>
{

	/**
	 * Constructor.
	 */
	public MSGTPDUFilter(IFlexibleConnector<IMessage, IExtendedMessage> connector)
	{
		super(connector);
	}


	@Override
	protected TPDU transformToOut(IExtendedMessage entity)
	{
		if (entity instanceof TPDU)
			return (TPDU) entity;
		return null;
	}

	@Override
	protected IMessage transformToConIn(IMessage entity)
	{
		return entity;
	}

}
