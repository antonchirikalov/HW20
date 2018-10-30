package com.toennies.ci1429.app.hw10.processing.devices;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.toennies.ci1429.app.hw10.client.HW10Client;
import com.toennies.ci1429.app.hw10.client.HW10Server;
import com.toennies.ci1429.app.hw10.processing.HW10CommandDispatcher;
import com.toennies.ci1429.app.hw10.processing.HW10CommandDispatcher.HW10EventType;
import com.toennies.ci1429.app.hw10.processing.devices.scanner.functions.HW10ScanRegistry;
import com.toennies.ci1429.app.hw10.processing.events.HW10ClientEventProcessor;
import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.util.IExecutors;

/**
 * 
 * @author renkenh
 */
public class HW10ClientConsumer
{

	private static final Logger logger = LogManager.getLogger();

	/**
	 * The {@link CommandListener} of the {@link HW10Client}, permanently tries
	 * to retrieve commands from the command buffer of
	 * {@link HW10Connector #pop()}.
	 */
	private final class CommandListener implements Runnable
	{

		@Override
		public void run()
		{
			if (!HW10ClientConsumer.this.client.isConnected())
			{
				this.notifyShutdown();
				return;
			}

			try
			{
				String command = this.retreiveCommand();
				if (command != null)
					this.processCommand(command);

				this.start();
			}
			catch (IOException | TimeoutException e)
			{
				logger.error("HW10Client Pipeline got an IOException or TimeoutException: ", e);

				this.notifyShutdown();
				return;
			}
		}

		/**
		 * This method creates an executor that uses a single worker thread
		 * operating off an unbounded queue
		 * {@link ExecutorService #execute(Runnable)}
		 */
		public synchronized void start()
		{
			HW10ClientConsumer.this.executor.execute(this);
		}

		/**
		 * This method retrieves a command(s) from the {@link HW10Connector}
		 * 
		 * @return the commands performed from client(s)
		 *         {@link HW10Client #pipeline}
		 */
		private String retreiveCommand() throws IOException
		{
			return HW10ClientConsumer.this.client.take();
		}

		/**
		 * This method offers the {@link HW10CommandDispatcher} an event
		 * containing the retrieved command.
		 * <p>
		 * After being processed, it stores the command in the SendBuffer Queue
		 * of the {@link HW10Connector}.
		 */
		private void processCommand(String command) throws IOException, TimeoutException
		{
			DeviceResponse response = dispatcher.offer(HW10EventType.NEW_COMMAND, HW10ClientConsumer.this.client, command);
			if (response != null)
				HW10ClientConsumer.this.client.push(response);
		}

		/**
		 * This method notifies the {@link HW10CommandDispatcher} for shutdown
		 * event.
		 * <p>
		 * After being processed, {@link HW10ClientEventProcessor} requests for
		 * removing scanner listener {@link HW10ScanRegistry} and finally
		 * invokes shutdown in {@link HW10Server}.
		 */
		private void notifyShutdown()
		{
			if (!HW10ClientConsumer.this.client.isConnected())
			{
				DeviceResponse response = HW10ClientConsumer.this.dispatcher.offer(HW10EventType.CLIENT_SHUTDOWN, HW10ClientConsumer.this.client);
				if (response != DeviceResponse.OK)
					logger.debug("HW10Client notifyShutdown got a bad response: {} ", response);
			}
		}
	};

	private final CommandListener commandListener = new CommandListener();
	private final ExecutorService executor = Executors.newSingleThreadExecutor(IExecutors.NETWORK_FACTORY);
	private final HW10Client client;
	private final HW10CommandDispatcher dispatcher;

	public HW10ClientConsumer(HW10Client client, HW10CommandDispatcher dispatcher)
	{
		this.client = client;
		this.dispatcher = dispatcher;
	}

	/**
	 * This method {@link CommandListener#start()} starts listening for
	 * commands.
	 */
	public void start()
	{
		this.commandListener.start();
	}

	/**
	 * This method shuts down {@link ExecutorService#shutdown()} all actively
	 * executing tasks.
	 */
	public void shutdown()
	{
		this.executor.shutdownNow();
		this.client.shutdown();
	}

}