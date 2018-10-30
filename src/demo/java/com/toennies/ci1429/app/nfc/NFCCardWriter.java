package com.toennies.ci1429.app.nfc;

import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.TerminalFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Kadirs implementation for writing nfc tags.
 * 
 * How to use this class for example:
 * 
 * <pre>
 * NFCCardWriter writer = new NFCCardWriter();
 * StringBuffer burmann = new StringBuffer("---BEGIN PASS---");
 * burmann.append("cQUFL7EF87jJ1knaCSjbFykX/CpwiiJMZH6SKxLLh9RQ79V71H3EFk8YsXqogut5vgmiKQN9xcL5");
 * burmann.append("eONFk2qlxCx7T530XiM85xU286MrkDMK6ho4yjRkYTu3D+6rUub/6K6TeF4NBECa1pj0AGQ+iUAb");
 * burmann.append("p+D/ZNROaD3S5YtrarIRoD/yEpqulPpYryIcLY/MOCLHlJNoXbAF+mzwE1tChYa7ozKM5Xw69SKF");
 * burmann.append("/blnx/AOJkWF3VRYTiYEJO9qyC+B8C9h63zYvtZb76MOxOizQw4Uf3VRqqlqroLlTcRkV9YDz00z");
 * burmann.append("VdqFVR4Ue4Mf0gHAjJf1sdGFnNur0gqw34tlHw==");
 * writer.write(burmann.toString());
 * </pre>
 */
@SuppressWarnings("restriction")
public class NFCCardWriter {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final int BLOCK_LENGTH = 4;
	private static final int FIRST_DATA_BLOCK_NUMBER = 4;
	private static final int LAST_DATA_BLOCK_NUMBER = 200;

	/**
	 * Wrapper method for {@link NFCCardWriter#write(String)}. Catches
	 * Exceptions and returns true if writing was successfull.
	 * 
	 */
	public boolean writeOnNFC(String data) {
		try {
			write(data);
			return true;
		} catch (Exception e) {
			LOGGER.error("Error while writing data 2 nfc");
			return false;
		}
	}

	/**
	 * writes a String into the Card via card channel
	 * 
	 * @param data
	 * @throws Exception
	 */
	private void write(String data) throws Exception {

		CardChannel channel = findCardChannel();
		write(channel, data);
		channel.getCard().disconnect(false);
	}

	/**
	 * looks for the card-device (for read/write) that is connected to
	 * USB-Interface
	 * 
	 * @return
	 * @throws CardException
	 */
	private CardChannel findCardChannel() throws CardException {
		TerminalFactory factory = TerminalFactory.getDefault();
		List<CardTerminal> terminals = factory.terminals().list();
		/**
		 * connect to first device ACS ACR1252 1S CL Reader PICC 0 that supports
		 * the NFC-Card
		 */
		CardTerminal terminal = terminals.get(0);
		Card card = terminal.connect("*");
		CardChannel channel = card.getBasicChannel();
		return channel;
	}

	private byte[] concatenateByteArrays(byte[] a, byte[] b) {
		byte[] result = new byte[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}

	private CommandAPDU createWriteBlockAPDU(int block, byte[] blockData) {
		byte[] byteBlock = blockData;
		byte[] nullbyteArray = { (byte) 0x00 };
		while (byteBlock.length < BLOCK_LENGTH) {
			byteBlock = concatenateByteArrays(byteBlock, nullbyteArray);
		}
		byte msb = (byte) ((block & 0xff00) >> 8);
		byte lsb = (byte) (block & 0xff);
		byte[] writeBlockAPDU = { (byte) 0xFF, (byte) 0xD6, msb, lsb, (byte) 0x04 };
		writeBlockAPDU = concatenateByteArrays(writeBlockAPDU, byteBlock);
		return new CommandAPDU(writeBlockAPDU);
	}

	/**
	 * String is written in 4 Bytes-blocks
	 * 
	 * @param channel
	 * @param data
	 * @throws Exception
	 */
	private void write(CardChannel channel, String data) throws Exception {
		String untransmitted = data;
		int block = 0;
		while (untransmitted.length() > 0) {
			String curBlockData = null;
			if (untransmitted.length() > BLOCK_LENGTH) {
				curBlockData = untransmitted.substring(0, BLOCK_LENGTH);
				untransmitted = untransmitted.substring(BLOCK_LENGTH);
			} else {
				curBlockData = untransmitted;
				untransmitted = "";
			}
			int curBlocknumber = block + FIRST_DATA_BLOCK_NUMBER;

			/**
			 * max 200 Blocknumber because of capacity of Card
			 */
			if (curBlocknumber > LAST_DATA_BLOCK_NUMBER)
				throw new Exception("invalidNFCdata");
			writeBlock(channel, curBlocknumber, curBlockData.getBytes());
			block++;
		}

	}

	private void writeBlock(CardChannel channel, int block, byte[] curBlockData) throws Exception {
		CommandAPDU writeBlockAPDU = createWriteBlockAPDU(block, curBlockData);
		channel.transmit(writeBlockAPDU);
	}

}
