/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.printer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;

import com.toennies.ci1429.app.model.printer.LabelData;
import com.toennies.ci1429.app.model.printer.Preview;
import com.toennies.ci1429.app.network.socket.TCPSocket;

/**
 * Utility that can be used to programmatically access the zebra printer label preview function of the printer webserver.
 * It takes the label data to print and the printer instance (as the protocol) and does all the work internally.
 * @author renkenh
 */
class ZebraPreviewUtil
{
	
	private static final Pattern IMGURL_PATTERN = Pattern.compile("<IMG SRC=\"([^\"]+)\"[^>]*>");
	private static final Logger logger = LogManager.getLogger();


	/**
	 * Tries to create a preview of the given label data using a specific zebra printer (hardware). 
	 * @param data The data to preview.
	 * @param protocol The protocol instance of the zebra printer.
	 * @return A preview or <code>null</code> if something went wrong.
	 */
	public static final Preview getReview(LabelData data, ZebraPrinterProtocol protocol)
	{
		CloseableHttpClient client = HttpClients.createDefault();
		try
		{
			String entityResponse = postZpl(data, protocol, client);
			String imgURL = extractImageURL(entityResponse);
			byte[] imgData = imageData(imgURL, protocol, client);
			return new Preview(MediaType.IMAGE_PNG, imgData);
		}
		catch (IOException ex)
		{
			logger.warn("Could not get label preview.", ex);
			return null;
		}
		finally
		{
			try
			{
				client.close();
			}
			catch (IOException e)
			{
				//do not log - just close.
			}
		}
	}
	
	private static final String postZpl(LabelData data, ZebraPrinterProtocol protocol, CloseableHttpClient client) throws IOException
	{
		String host = protocol.getConfig().get(TCPSocket.PARAM_HOST);
	    HttpPost httpPost = new HttpPost("http://"+host+"/zpl");
	    httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
	    HttpEntity entityPost = new StringEntity(createBody(data, protocol));
	    httpPost.setEntity(entityPost);
	    
	    CloseableHttpResponse response = client.execute(httpPost);
	    
	    OutputStream buffer = new ByteArrayOutputStream();
	    int nRead;
	    byte[] raw = new byte[1024];
	    while ((nRead = response.getEntity().getContent().read(raw, 0, raw.length)) != -1) {
	        buffer.write(raw, 0, nRead);
	    }
	 
	    buffer.flush();
	    byte[] byteArray = ((ByteArrayOutputStream) buffer).toByteArray();
	         
	    String entityBody = new String(byteArray, StandardCharsets.UTF_8);
	    return entityBody;
	}
	
	private static final String createBody(LabelData data, ZebraPrinterProtocol protocol) throws IOException
	{
		String zpl = protocol.convertByTemplate(data);
		StringBuilder sb = new StringBuilder();
		sb.append("data="); sb.append(zpl);
		sb.append("&dev=R");
		sb.append("&oname=UNKNOWN");
		sb.append("&otype=ZPL");
		sb.append("&prev=Etikettenvorschau");
		sb.append("&pw=1234");
		return sb.toString();
	}
	
	private static final String extractImageURL(String entityBody)
	{
	    Matcher m = IMGURL_PATTERN.matcher(entityBody);
	    if (m.find())
	    	return m.group(1);
	    return null;
	}
	
	private static final byte[] imageData(String imgURL, ZebraPrinterProtocol protocol, CloseableHttpClient client) throws IOException
	{
		String host = protocol.getConfig().get(TCPSocket.PARAM_HOST);
		HttpGet httpGet = new HttpGet("http://" + host + "/" + imgURL);
		
		CloseableHttpResponse response = client.execute(httpGet);
		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead = 0;
		byte[] data = new byte[1024];
		while ((nRead = response.getEntity().getContent().read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		
		buffer.close();
		return buffer.toByteArray();
	}
	
	
	private ZebraPreviewUtil()
	{
		//no instance
	}

}
