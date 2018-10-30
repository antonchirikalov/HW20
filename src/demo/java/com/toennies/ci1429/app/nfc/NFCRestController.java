package com.toennies.ci1429.app.nfc;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/devices")
@EnableAutoConfiguration
public class NFCRestController {

	private static final Logger LOGGER = LogManager.getLogger(NFCRestController.class);
	/**
	 * algorithm used by toennies and FA.Innomos
	 */
	private final static String ALGO = "AES";

	/**
	 * keyval used by toennies and FA.Innomos
	 */
	private final static byte[] keyValue = new byte[] { 'B', 'e', 's', 'u', 'c', 'h', 'e', 'r', 'Z', 'u', 'g', 'r', 'i',
			'f', 'T', 'O' };	//FIXME WHAT THE FUCK!  REVIEW DID NOT SEE THIS? this should not be here!!!11!!1 This has to go into the config file - encrypted!

	/**
	 * Example invokation:
	 * 
	 * http://localhost:8080/devices/1/nfcwrite
	 * 
	 * Store data in http body.
	 */
	@RequestMapping(value = "/{deviceID}/nfcwrite", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Boolean> postPrintBatch(@PathVariable("deviceID") int deviceID,
			@RequestParam("data") String data) {

		// TODO: doesn't use devicesID. Every device needs an distinct id.

		// Data that was sent to service gets encrypted here
		byte[] encryptedValueAsByte = generateEncryptedValue(data);
		if (encryptedValueAsByte == null) {
			// If encryption fails, return an error status.
			return new ResponseEntity<Boolean>(false, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// Encrypted data is encoded to BASE64 String
		String encryptedValue = Base64.getEncoder().encodeToString(encryptedValueAsByte);

		// BASE64 data is wrapped with further information
		String generateData2Write = generateData2Write(encryptedValue);

		NFCCardWriter writer = new NFCCardWriter();
		boolean writeOnNFC = writer.writeOnNFC(generateData2Write);
		return new ResponseEntity<Boolean>(writeOnNFC, HttpStatus.OK);
	}

	/**
	 * Takes data and tries to encrypts it. May return null if an exception
	 * occurs.
	 */
	private byte[] generateEncryptedValue(String data) {
		Key keyVal = new SecretKeySpec(keyValue, ALGO);
		Cipher cipher = null;
		byte[] encryptedValue = null;
		try {
			cipher = Cipher.getInstance(ALGO);
			cipher.init(Cipher.ENCRYPT_MODE, keyVal);
			encryptedValue = cipher.doFinal(data.getBytes());
			return encryptedValue;
		} catch (Exception e) {
			LOGGER.error("Error while writing nfc", e);
			return null;
		}
	}

	/**
	 * Takes encrypted String and wraps it.
	 */
	private String generateData2Write(String encryptedValue) {
		StringBuilder dataToWrite = new StringBuilder();
		dataToWrite.append("---BEGIN PASS---\n");
		dataToWrite.append(encryptedValue);
		dataToWrite.append("\n---END PASS---");
		return dataToWrite.toString();
	}

}
