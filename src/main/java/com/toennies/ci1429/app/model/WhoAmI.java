package com.toennies.ci1429.app.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class WhoAmI implements Serializable {

	private String remoteAddr;

	public WhoAmI() {
		//
	}

	public WhoAmI(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	@Override
	public String toString() {
		return "WhoAmI [remoteAddr=" + remoteAddr + "]";
	}

}
