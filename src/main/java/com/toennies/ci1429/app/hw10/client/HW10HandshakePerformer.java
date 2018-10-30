package com.toennies.ci1429.app.hw10.client;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.hw10.util.ClientInfoUtil;
import com.toennies.ci1429.app.hw10.util.ClientInfoUtil.ClientInfoType;
import com.toennies.ci1429.app.network.connector.AWrappedConnector;
import com.toennies.ci1429.app.network.connector.IConnector;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.util.ASCII;

/**
 * This class is used to establish the negotiation process with remote client.
 * <p>
 * The parameters are set in {@link #setClientInfo(String)} in order to
 * establish the connection with client machine.
 * 
 * @author renkenh
 */
public class HW10HandshakePerformer extends AWrappedConnector<String>
{
	private static final Logger logger = LogManager.getLogger();

	private final String cid;
	private String pid;
	private String exe;
	private String pfd;

	private ClientType clientType;

	/**
	 * Constructor.
	 * 
	 * @param connector
	 *            this connector is inherited from {@link IConnector} in order
	 *            to send (OUT) and receive (IN) remote data.
	 */
	public HW10HandshakePerformer(IConnector<String> connector)
	{
		super(connector);
		this.cid = HW10Server.generateNextClientId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void connect(IConfigContainer config) throws IOException
	{
		super.connect(config);
		this.performHandshake();
	}

	/**
	 * This method is used to initiate and negotiate socket connection.
	 * <p>
	 * The negotiation process starts by sending {@link #getCid()} and .
	 */
	private void performHandshake() throws IOException
	{
		/* On initiated connection, the {@link #cid} is sent to client. */
		this.push(this.getCid());

		try
		{
			for (int i = 0; i < 3; i++)
			{
				String clientInfo = this.pop();
				this.setClientInfo(clientInfo);
				this.push(String.valueOf(ASCII.ACK.c));
			}
			this.clientType = ClientType.getMatchingClientType(this.exe);
		}
		catch (TimeoutException e)
		{
			logger.error("Error during Handshake : ", e);
			throw new IOException(e);
		}
	}

	/**
	 * This method sets the handshake (negotiation) rules with clients
	 * {@link #ClientInfoUtil}.
	 */
	private void setClientInfo(String clientInfo)
	{
		ClientInfoType type = ClientInfoUtil.getInfoType(clientInfo);
		switch (type)
		{
			case EXE:
				this.exe = clientInfo;
				break;
			case PFD:
				this.pfd = clientInfo;
				break;
			case PID:
				this.pid = clientInfo;
				break;
		}
	}

	public String getPid()
	{
		return this.pid;
	}

	public String getExe()
	{
		return this.exe;
	}

	public String getPath()
	{
		return this.pfd;
	}

	public String getCid()
	{
		return this.cid;
	}

	public ClientType getClientType()
	{
		return this.clientType;
	}
}