package com.toennies.ci1429.app.util.binary.model;


/**
 * Main type to specify a binary field. Contains information about the type (see {@link BType} for supported types) and an id which is used to access the field for
 * reading and writing of values.
 * @author renkenh
 */
public class BField
{
	
	/** Order of the types is essential. Different checks are based on the order. Do not change. */
	public enum BType
	{
		BYTE(1, byte.class),
		UBYTE(1, short.class),
		SHORT(2, short.class),
		USHORT(2, int.class),
		INT(4, int.class),
		UINT(4, long.class),
		LONG(8, long.class),
		FLOAT(4, float.class),
		DOUBLE(8, double.class),
		CHAR(1, char.class),
		STRING_FIXED(-1, String.class),
		STRING_DYNAMIC(-1, String.class),
		RAW_FIXED(-1, byte[].class),
		RAW_DYNAMIC(-1, byte[].class);

		/** The length of the field in bytes. -1 for unknown size. */
		public final int length;
		/** The java type used to represent the value in java (needed especially for unsupported types like unsigned values. */
		public final Class<?> clazz;


		private BType(int length, Class<?> clazz)
		{
			this.length = length;
			this.clazz = clazz;
		}
	}
	

	/** The type of the field. */
	public final BType type;
	/** The id of the field. */
	public final String id;


	/** Constructor. */
	public BField(BType type, String id)
	{
		this.type = type;
		this.id = id;
	}
	
	/**
	 * The length of this field in bytes.
	 * @return The length.
	 */
	public int length()
	{
		return this.type.length;
	}
}
