package com.toennies.ci1429.app.network.test;

public interface IScriptRunner
{
	void push(byte[] cmd);
	byte[] pop();

}
