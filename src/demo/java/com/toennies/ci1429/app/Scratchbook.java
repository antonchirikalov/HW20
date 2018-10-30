package com.toennies.ci1429.app;

import com.fazecast.jSerialComm.SerialPort;

public class Scratchbook
{

	public static void main(String[] args) throws Exception
	{
		SerialPort p = SerialPort.getCommPort("COM3");
		p.openPort();
		System.out.println(p.isOpen());
	}

}
