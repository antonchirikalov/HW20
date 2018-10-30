package com.toennies.ci1429.app.network.message;

import java.util.List;

public class MSGParserDemo
{

	public static final void main(String[] args) throws Exception
	{
		MessageTransformer parser = new MessageTransformer("ab", "cd", "ef");
		log(parser.parseData("ab0000cd0000ef".getBytes()));
		log(parser.parseData("ab1111c".getBytes()));
		log(parser.parseData("d11111ef".getBytes()));
		log(parser.parseData("ab2222222d11111ef".getBytes()));
	}

	
	private static final void log(List<IExtendedMessage> msgs)
	{
		System.out.println("Found Messages: " + msgs.size());
		for (IMessage msg : msgs)
		{
			System.out.print("Found Words: " + msg.words().size() + " ");
			for (byte[] word : msg.words())
				System.out.print(new String(word) + ":");
			System.out.println();
		}
		System.out.println();
	}
}
