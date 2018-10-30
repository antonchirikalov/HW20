package com.toennies.ci1429.app.network.test;

import com.toennies.ci1429.app.util.ASCII;

public class Token
{
	private CommandType commandType;
	private String command;

	public CommandType getCommandType()
	{
		return commandType;
	}

	public void setCommandType(CommandType commandType)
	{
		this.commandType = commandType;
	}


	public String getCommand()
	{
		return command;
	}

	public void setCommand(String command)
	{
		this.command = command;
	}

	public Token(CommandType commandType,  String command)
	{
		this.commandType = commandType;
		this.command = command;
	}

	public Token(CommandType commandType)
	{
		this.commandType = commandType;
	}

	@Override
	public String toString()
	{
		String commandHumanString = command!=null ? ASCII.formatHuman(command.getBytes()): "NULL";
		return "Token{" +
				"commandType=" + commandType +
				", command='" + commandHumanString + '\'' +
				'}';
	}
}
