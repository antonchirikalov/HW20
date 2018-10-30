/**
 * 
 */
package com.toennies.ci1429.app.util.binary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.toennies.ci1429.app.util.binary.pojo.BPojoReader;
import com.toennies.ci1429.app.util.binary.pojo.BPojoWriter;

/**
 * @author renkenh
 *
 */
public class BReaderWriterTest
{

	@Test
	public void pojoReadWriteTest() throws IOException
	{
		TestPayload payload = new TestPayload();
		payload.setAbyte((byte) 1);
		payload.setAchar('c');
		payload.setAdouble(1.1d);
		payload.setAfloat(4.4f);
		payload.setAint(10);
		payload.setAlong(10000000000000l);
		payload.setAshort(28000);
		payload.setAstring("A String");
		payload.setRaw(new byte[] { 1, 2, 3, 4, 5});
		payload.setUbyte((short) 224);
		payload.setUint(4294967245l);
		payload.setUshort(65000);
		
		BPojoWriter<TestPayload> writer = new BPojoWriter<>(TestPayload.class);
		byte[] written = writer.write(payload);
		BPojoReader<TestPayload> reader = new BPojoReader<>(TestPayload.class);
		TestPayload ritten = reader.parse(written);
		
		assertEquals(payload.getAbyte(), ritten.getAbyte());
		assertEquals(payload.getAchar(), ritten.getAchar());
		assertEquals(payload.getAdouble(), ritten.getAdouble(), 0.00001d);
		assertEquals(payload.getAfloat(), ritten.getAfloat(),   0.00001f);
		assertEquals(payload.getAint(), ritten.getAint());
		assertEquals(payload.getAlong(), ritten.getAlong());
		assertEquals(payload.getAshort(), ritten.getAshort());
		assertEquals(payload.getAstring(), ritten.getAstring());
		assertTrue(Arrays.equals(payload.getRaw(), ritten.getRaw()));
		assertEquals(payload.getUbyte(), ritten.getUbyte());
		assertEquals(payload.getUint(), ritten.getUint());
		assertEquals(payload.getUshort(), ritten.getUshort());
	}

}
