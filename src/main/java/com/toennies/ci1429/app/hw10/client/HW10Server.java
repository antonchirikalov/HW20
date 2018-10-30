package com.toennies.ci1429.app.hw10.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.toennies.ci1429.app.hw10.processing.HW10CommandDispatcher;
import com.toennies.ci1429.app.hw10.processing.HW10CommandDispatcher.HW10EventType;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.util.Utils;

/**
 * Implementation of socket communications.
 * 
 * @author renkenh
 */
@Component
public class HW10Server
{
	
	private static final Logger logger = LogManager.getLogger();

	/**
	 * An {@link AtomicInteger} is used to atomically increment counters when
	 * the client connection is accepted
	 * {@link HW10Server #generateNextClientId()}
	 */
	private static final AtomicInteger GLOBAL_ID = new AtomicInteger(0);

	/**
	 * This sets client ID prefix for each client connected
	 * <p>
	 * It is invoked during each connected client using the prefix {@value #CID}
	 * and the client ID {@value #generateNextClientId()}}
	 */
	private static final String CID = "CID";

	@Value("${hw10.port}")
	private int port;

	@Autowired
	private HW10CommandDispatcher dispatcher;

	private final Runnable socketListener = () ->
	{
		logger.info("Connecting with the Port: {} and Server Socket is bound: {} ", this.serverSocket.getLocalPort(),
				this.serverSocket.isBound());

		while (HW10Server.this.isRunning && !this.serverSocket.isClosed())
		{
			/**
			 * It implements client sockets as an endpoint for communication
			 * between client and server.
			 */
			Socket clientSocket = null;
			try
			{
				/**
				 * Listens for a connection to be made to this socket
				 * {@link #Socket} and accepts it. The method blocks until a
				 * connection is made.
				 */
				clientSocket = this.serverSocket.accept();
				logger.info(" Client connected in : {} ", clientSocket.getRemoteSocketAddress());

				/** Initialize new client connection {@link #HW10Client} */
				HW10Client client = new HW10Client(clientSocket);
				client.init();
				DeviceResponse response = this.dispatcher.offer(HW10EventType.NEW_CLIENT, client);
				if (response != DeviceResponse.OK)
				{
					logger.error("HW10Server could not offer new client : ", response);
					/** Disconnect from the remote system */
					client.shutdown();
				}
			}
			catch (IOException ex)
			{
				logger.error("HW10Server got an IOException while trying to accept new client : ", ex);
				Utils.close(clientSocket);
			}
		}
		/**
		 * This close method is invoked to release resources that the
		 * serverSocket is holding {@link Utils}
		 */
		Utils.close(this.serverSocket);
	};

	/**
	 * A server socket waits for requests to come in over the network
	 * {@link ServerSocket}.
	 * <p>
	 * It performs operation based on that request, and then possibly returns a
	 * result to the requester.
	 */
	private ServerSocket serverSocket;

	/** This method must be invoked before the thread is started. */
	private final Thread thread = new Thread(this.socketListener);
	{
		this.thread.setDaemon(true);
	}

	/** A volatile boolean is used to update flags. */
	private volatile boolean isRunning = true;

	/**
	 * Create a server-socket {@link ServerSocket #bind(java.net.SocketAddress)}
	 * object with the port number on which the server program is going to
	 * listen for client communications.
	 */
	public synchronized void start()
	{
		if (this.serverSocket != null && (this.serverSocket.isBound() || this.serverSocket.isClosed()))
		{
			logger.error("ServerSocket is already running!");
			return;
		}

		try
		{
			this.serverSocket = new ServerSocket();
			this.serverSocket.bind(new InetSocketAddress(InetAddress.getLocalHost().getHostName(), port));
			this.thread.start();
		}
		catch (IOException e)
		{
			this.serverSocket = null;
			return;
		}
	}

	/** Disconnects {@link ServerSocket} and interrupts the running thread. */
	public synchronized void stop()
	{
		this.isRunning = false;
		this.thread.interrupt();
	}

	/** Increments a client ID {@link #CID} on each connected client. */
	protected static String generateNextClientId()
	{
		return CID.concat(String.valueOf(GLOBAL_ID.incrementAndGet()));
	}
}