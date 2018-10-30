/**
 * 
 */
package com.toennies.ci1429.app.printer;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
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

/**
 * @author renkenh
 *
 */
public class LabelPreviewDemo
{
	
	private static final Pattern IMG_PATTERN = Pattern.compile("<IMG SRC=\"([^\"]+)\"[^>]*>");

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		String host = "http://10.235.49.108";
		String previewURL = host + "/zpl";

		String body = "data="
				+ "^XA" +
				"^XFR:INNCN.ZPL" +
				"^FH_^CI28^FN11^FD29478^FS^CI0"+
				"^FH_^CI28^FN15^FD_E5_86_B7_E5_86_BB_E9_95_BF_E5_88_87_E5_B8_A6_E8_B6_BE_E7_8C_AA_E8_84_9A ^FS^CI0" +
				"^FH_^CI28^FN12^FDFrozen Pork Hind   Feet toes on ^FS^CI0"+
				"^FH_^CI28^FN13^FD-^FS^CI0"+
				"^FH_^CI28^FN16^FD12345678^FS^CI0"+
				"^PQ1"+
				"^XZ"+
				"&dev=R"+
				"&oname=UNKNOWN"+
				"&otype=ZPL"+
				"&prev=Etikettenvorschau"+
				"&pw=1234";
		
		CloseableHttpClient client = HttpClients.createDefault();
	    HttpPost httpPost = new HttpPost(previewURL);
	    httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
	    HttpEntity entityPost = new StringEntity(body);
	    httpPost.setEntity(entityPost);
	    
	    CloseableHttpResponse response = client.execute(httpPost);
	    
	    OutputStream buffer = new ByteArrayOutputStream();
	    int nRead;
	    byte[] data = new byte[1024];
	    while ((nRead = response.getEntity().getContent().read(data, 0, data.length)) != -1) {
	        buffer.write(data, 0, nRead);
	    }
	 
	    buffer.flush();
	    byte[] byteArray = ((ByteArrayOutputStream) buffer).toByteArray();
	         
	    String entityBody = new String(byteArray, StandardCharsets.UTF_8);
	    
	    Matcher m = IMG_PATTERN.matcher(entityBody);
	    if (m.find())
	    {
	    	String imgURL = m.group(1);
	    	if (imgURL != null)
	    	{
	    		System.out.println("Image url: " + imgURL);
	    	    HttpGet httpGet = new HttpGet(host + "/" + imgURL);
	    	    
	    	    response = client.execute(httpGet);
	    	    
	    	    buffer = new FileOutputStream("previewImage.png");

	    	    while ((nRead = response.getEntity().getContent().read(data, 0, data.length)) != -1) {
	    	        buffer.write(data, 0, nRead);
	    	    }
	    	 
	    	    buffer.close();
	    	    client.close();
	    	    System.out.println("File written");
	    	    return;
	    	}
	    }
	    System.out.println("No img url found in " + entityBody);
	    client.close();
	}

}
