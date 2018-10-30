/**
 * 
 */
package com.toennies.ci1429.app.network.socket;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.util.Utils;


/**
 * A simple socket that writes to the LPT1 port (on Windows). Uses the file system api for that.
 * @author renkenh
 */
@AtSocket("LPT Socket")
@Parameter(name=LPTSocket.PARAM_PORT, value="LPT1:", isRequired=true, toolTip="The LPT port name. Default=LPT1:")
@Parameter
public class LPTSocket implements ISocket
{

	/** The port to write to. */
	public static final String PARAM_PORT = "port";
	
	private static final Logger logger = LogManager.getLogger();

	private volatile String port;


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		this.port = config.getEntry(PARAM_PORT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected()
	{
		return this.port != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void push(byte[] data) throws IOException
	{
		if (!this.isConnected())
			throw new IOException("LTPSocket not connected.");
		
		//always open port - data is not flushed when port is not closed after sending.
		FileOutputStream parallelPort = new FileOutputStream(this.port);
		logger.debug("Will be written to parallel port {}: '{}'.", this.port, new String(data, "US-ASCII"));
		parallelPort.write(data);
		Utils.close(parallelPort);
	}

	@Override
	public byte[] poll() throws IOException
	{
		return null;	//do not return anything
	}

	@Override
	public byte[] pop() throws IOException
	{
		return null;	//do not return anything
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void disconnect()
	{
		this.port = null;
	}

	@Override
	public void shutdown()
	{
		this.disconnect();
	}
	
}
