/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.watcher.espa;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.toennies.ci1429.app.network.connector.AFlexibleWrapperTransformer;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;

/**
 * Health Check for the ESPA Protocol.
 * Simply acknowledges incoming messages.
 * @author renkenh
 */
public class ESPAHealthcheck<IN, OUT> extends AFlexibleWrapperTransformer<IN, OUT, IN, OUT>
{
	
	private Duration timeout = Duration.ZERO;
	private Instant lastIncome = Instant.now();
	

	/**
	 * Constructor.
	 */
	public ESPAHealthcheck(IFlexibleConnector<IN, OUT> connector)
	{
		super(connector);
	}
	
	
	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		int timeoutValue = config.getIntEntry(ESPAProtocol.PARAM_HEALTHCHECK);
		this.timeout = Duration.of(timeoutValue, ChronoUnit.MILLIS);
		super.connect(config);
		this.lastIncome = Instant.now();
	}

	
	@Override
	protected IN transformToConIn(IN entity)
	{
		if (entity != null)
			this.lastIncome = Instant.now();
		return entity;
	}

	@Override
	protected OUT transformToOut(OUT entity) throws IOException
	{
		return entity;
	}

	
	@Override
	public boolean isConnected()
	{
		Duration sinceIncome = Duration.between(Instant.now(), this.lastIncome).abs();
		return sinceIncome.compareTo(this.timeout) <= 0 && super.isConnected();
	}

}
