/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scale.dummy;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import com.toennies.ci1429.app.model.scale.WeightData;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.protocol.scale.HardwareResponse;
import com.toennies.ci1429.app.network.protocol.scale.IHardwareRequest;

/**
 * Contains the actual dummy scale logic. Does contain a tare which is managed and uses a random generator to generate netto weights.
 * @author renkenh
 */
class DummyScalePipe implements IFlexibleConnector<IHardwareRequest, HardwareResponse>
{
	
	private volatile boolean isConnected = false;
	private final BlockingDeque<IHardwareRequest> requests = new LinkedBlockingDeque<>();
	
	private final Random random = new Random(1000);
	private volatile double tare = 0;
	private volatile int lowerBound = 1;
	private volatile int upperBound = 10;
	private final AtomicInteger counter = new AtomicInteger(0); 
	

	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		this.isConnected = true;
		this.lowerBound = config.getIntEntry(DummyScaleProtocol.PARAM_LOWER_BOUND);
		this.upperBound = config.getIntEntry(DummyScaleProtocol.PARAM_UPPER_BOUND);
		if (lowerBound > upperBound)
		{
			int tmp = this.lowerBound;
			this.lowerBound = this.upperBound;
			this.upperBound = tmp;
		}
	}

	@Override
	public boolean isConnected()
	{
		return this.isConnected;
	}

	@Override
	public HardwareResponse poll() throws IOException
	{
		if (this.requests.isEmpty())
			return null;
		return this.processRequest(this.requests.pollFirst());
	}

	@Override
	public HardwareResponse pop() throws IOException, TimeoutException
	{
		if (this.requests.isEmpty())
			throw new TimeoutException("Scale did not respond.");
		return this.processRequest(this.requests.pollFirst());
	}
	
	
	private HardwareResponse processRequest(IHardwareRequest request)
	{
		DummyRequest dr = (DummyRequest) request;
		switch (dr.cmd)
		{
			case CLEAR_TARE:
			case RESET:
				this.tare = 0;
				return HardwareResponse.OK;
			case ITEM_ADDING:
			case ITEM_NOT_ADDING:
				WeightData data = new WeightData();
				data.setNetto(this.generateCurrentWeight());
				data.setTara(this.tare);
				data.setCounter(this.counter.incrementAndGet());
				return new HardwareResponse(data);
			case TARE:
				this.tare += this.generateCurrentWeight();
				return HardwareResponse.OK;
			case TARE_WITH_VALUE:
				DummyTareValueRequest tvr = (DummyTareValueRequest) request;
				this.tare = tvr.value;
				return HardwareResponse.OK;
			case WEIGH:
			case WEIGH_DIRECT:
				data = new WeightData();
				data.setNetto(this.generateCurrentWeight());
				data.setTara(this.tare);
				return new HardwareResponse(data);
			default:
				break;
		}
		return HardwareResponse.CANCELED;
	}
	
	private double generateCurrentWeight()
	{
		return 1000 * (this.lowerBound + (this.upperBound - this.lowerBound) * this.random.nextDouble());
	}

	@Override
	public void push(IHardwareRequest entity) throws IOException
	{
		this.requests.add(entity);
	}

	@Override
	public void disconnect() throws IOException
	{
		this.shutdown();
	}

	@Override
	public void shutdown()
	{
		this.isConnected = false;
	}


}
