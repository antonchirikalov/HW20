package com.toennies.ci1429.app.hw10.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.toennies.ci1429.app.hw10.util.ClientInfoUtil.ClientInfoType;

public class ClientInfoUtilTest
{
	private static final String SAMPLE_PID = "PID006920";
	private static final String SAMPLE_EXE = "EXE00HWServer";
	private static final String SAMPLE_PFD = "PFD00\\\\LXRHE19\\clients\\exe";

	@Test
	public void getTypeTest()
	{
		assertTrue(ClientInfoType.PID.equals(ClientInfoUtil.getInfoType(SAMPLE_PID)));
		assertTrue(ClientInfoType.PFD.equals(ClientInfoUtil.getInfoType(SAMPLE_PFD)));
		assertTrue(ClientInfoType.EXE.equals(ClientInfoUtil.getInfoType(SAMPLE_EXE)));
	}

}
