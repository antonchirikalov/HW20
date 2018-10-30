package com.toennies.ci1429.app.model.scale;

/**
 * API type that contains all the parameters and commands that can be used for a scale http request.
 * @author renkenh
 */
public class Commands
{

	public static final String PARAM_VALUE = "value";


//	/**
//	 * Parameters that can be used.
//	 * @author renkenh
//	 */
//	public enum Param
//	{
//		
//		/** Response format. Should be of value {@link ResponseFormat}. */
//		RESPONSE("Response"),
//		/** The precision of returned weight data (number of digits after the dot.). */
//		PRECISION("Precision"),
//		/** The unit of the returned weight data. Should be of value {@link Unit}. */ 
//		UNIT("Unit"),
//		/** The value parameter needed for the command {@link Command#TARE_WITH_VALUE}. */
//		VALUE("Value");
//		
//		public final String identifier;
//		
//		private Param(String identifier)
//		{
//			this.identifier = identifier;
//		}
//	}
	
	/**
	 * Command type to differentiate the return of the command.
	 * @author renkenh
	 */
	public enum CommandType
	{
		/** Commands of this type return {@link WeightData}. */
		WEIGHTING,
		/** Commands of this type return no payload. */ 
		COMMAND,
		/** Commands of this type return no payload. In the future there might be special permissions needed to execute these commands. */ 
		SYSTEM
	}
	
	
	/**
	 * All commands that can be executed on scales. However, every scale type may implement only a subset of these commands.
	 * @author renkenh
	 */
	public enum Command
	{

		WEIGH_DIRECT(CommandType.WEIGHTING),
		WEIGH(CommandType.WEIGHTING),
		WEIGH_AUTOMATIC(CommandType.WEIGHTING),
		ITEM_ADDING(CommandType.WEIGHTING),
		ITEM_NOT_ADDING(CommandType.WEIGHTING),
		ZERO(CommandType.COMMAND),
		CLEAR_TARE(CommandType.COMMAND),
		TARE(CommandType.WEIGHTING),
		TARE_WITH_VALUE(CommandType.COMMAND),
		RESET(CommandType.SYSTEM),
		RESTART(CommandType.SYSTEM);


		/** The type of the command. */
		public final CommandType cmdType;


		private Command(CommandType cmd)
		{
			this.cmdType = cmd;
		}

		/**
		 * @return The type of the command.
		 */
		public CommandType commandType()
		{
			return this.cmdType;
		}

	}
	

	private Commands()
	{
		//no instance
	}
	
}
