/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.scanner;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Static database about all known EAN128 codes and their definition.
 * Each entry within this DB has its own parser instance.
 * @author renkenh
 */
public enum EAN128DB
{

	DB00("n2+n18"),
	DB01("n2+n14"),
	DB02("n2+n14"),
	DB10("n2+an...20"),
	DB11("n2+n6"),
	DB12("n2+n6"),
	DB13("n2+n6"),
	DB15("n2+n6"),
	DB17("n2+n6"),
	DB20("n2+n2"),
	DB21("n2+an...20"),
	DB240("n3+an...30"),
	DB241("n3+an...30"),
	DB242("n3+n...6"),
	DB250("n3+an...30"),
	DB251("n3+an...30"),
//	DB253("n3+n13+n...17"),
	DB254("n3+n...8"),
	DB30("n4+n6"),
	DB3100("n4+n6"),
	DB3101("n4+n6"),
	DB3102("n4+n6"),
	DB3103("n4+n6"),
	DB3104("n4+n6"),
	DB3105("n4+n6"),
	DB3106("n4+n6"),
	DB3110("n4+n6"),
	DB3111("n4+n6"),
	DB3112("n4+n6"),
	DB3113("n4+n6"),
	DB3114("n4+n6"),
	DB3115("n4+n6"),
	DB3116("n4+n6"),
	DB3120("n4+n6"),
	DB3121("n4+n6"),
	DB3122("n4+n6"),
	DB3123("n4+n6"),
	DB3124("n4+n6"),
	DB3125("n4+n6"),
	DB3126("n4+n6"),
	DB3130("n4+n6"),
	DB3131("n4+n6"),
	DB3132("n4+n6"),
	DB3133("n4+n6"),
	DB3134("n4+n6"),
	DB3135("n4+n6"),
	DB3136("n4+n6"),
	DB3140("n4+n6"),
	DB3141("n4+n6"),
	DB3142("n4+n6"),
	DB3143("n4+n6"),
	DB3144("n4+n6"),
	DB3145("n4+n6"),
	DB3146("n4+n6"),
	DB3150("n4+n6"),
	DB3151("n4+n6"),
	DB3152("n4+n6"),
	DB3153("n4+n6"),
	DB3154("n4+n6"),
	DB3155("n4+n6"),
	DB3156("n4+n6"),
	DB3160("n4+n6"),
	DB3161("n4+n6"),
	DB3162("n4+n6"),
	DB3163("n4+n6"),
	DB3164("n4+n6"),
	DB3165("n4+n6"),
	DB3166("n4+n6"),
	DB3300("n4+n6"),
	DB3301("n4+n6"),
	DB3302("n4+n6"),
	DB3303("n4+n6"),
	DB3304("n4+n6"),
	DB3305("n4+n6"),
	DB3306("n4+n6"),
	DB3310("n4+n6"),
	DB3311("n4+n6"),
	DB3312("n4+n6"),
	DB3313("n4+n6"),
	DB3314("n4+n6"),
	DB3315("n4+n6"),
	DB3316("n4+n6"),
	DB3320("n4+n6"),
	DB3321("n4+n6"),
	DB3322("n4+n6"),
	DB3323("n4+n6"),
	DB3324("n4+n6"),
	DB3325("n4+n6"),
	DB3326("n4+n6"),
	DB3330("n4+n6"),
	DB3331("n4+n6"),
	DB3332("n4+n6"),
	DB3333("n4+n6"),
	DB3334("n4+n6"),
	DB3335("n4+n6"),
	DB3336("n4+n6"),
	DB3370("n4+n6"),
	DB3371("n4+n6"),
	DB3372("n4+n6"),
	DB3373("n4+n6"),
	DB3374("n4+n6"),
	DB3375("n4+n6"),
	DB3376("n4+n6"),
	DB37("n4+n...8"),
	DB3900("n4+n...15"),
	DB3901("n4+n...15"),
	DB3902("n4+n...15"),
	DB3903("n4+n...15"),
	DB3904("n4+n...15"),
	DB3905("n4+n...15"),
	DB3906("n4+n...15"),
	DB3910("n4+n...15"),
	DB3911("n4+n...15"),
	DB3912("n4+n...15"),
	DB3913("n4+n...15"),
	DB3914("n4+n...15"),
	DB3915("n4+n...15"),
	DB3916("n4+n...15"),
	DB3920("n4+n...15"),
	DB3921("n4+n...15"),
	DB3922("n4+n...15"),
	DB3923("n4+n...15"),
	DB3924("n4+n...15"),
	DB3925("n4+n...15"),
	DB3926("n4+n...15"),
	DB3930("n4+n...15"),
	DB3931("n4+n3+n...15"),
	DB3932("n4+n3+n...15"),
	DB3933("n4+n3+n...15"),
	DB3934("n4+n3+n...15"),
	DB3935("n4+n3+n...15"),
	DB3936("n4+n3+n...15"),
	DB401("n3+an...30"),
	DB402("n3+an...30"),
	DB403("n3+n17"),
	DB410("n3+an...30"),
	DB411("n3+n13"),
	DB412("n3+n13"),
	DB413("n3+n13"),
	DB414("n3+n13"),
	DB415("n3+n13"),
	DB420("n3+an...9"),
	DB421("n3+n3+an...9"),
	DB422("n3+n3"),
	DB423("n3+n3+n...12"),
	DB424("n3+n3"),
	DB425("n3+n3"),
	DB426("n3+n3"),
	DB7001("n4+n13"),
	DB7002("n4+an...30"),
	DB7003("n4+n10"),
	DB7030("n4+n3+an...27"),
	DB7031("n4+n3+an...27"),
	DB7032("n4+n3+an...27"),
	DB7033("n4+n3+an...27"),
	DB7034("n4+n3+an...27"),
	DB7035("n4+n3+an...27"),
	DB7036("n4+n3+an...27"),
	DB7037("n4+n3+an...27"),
	DB7038("n4+n3+an...27"),
	DB7039("n4+n3+an...27"),
	DB8001("n4+n14"),
//	DB8003("n4+n14+an...16"),
	DB8004("n4+an...30"),
	DB8005("n4+n6"),
//	DB8006("n4+n14+n2+n2"),
	DB8007("n4+an...30"),
	DB8008("n4+n12"),
	DB8018("n4+n18"),
	DB8020("n4+an...25"),
	DB90("n2+an...30"),
	DB91("n2+an...30"),
	DB92("n2+an...30"),
	DB93("n2+an...30"),
	DB94("n2+an...30"),
	DB95("n2+an...30"),
	DB96("n2+an...30"),
	DB97("n2+an...30"),
	DB98("n2+an...30"),
	DB99("n2+an...30");
	
	
	private static final SortedMap<Integer, EAN128DB> EAN_BY_DB = new TreeMap<Integer, EAN128DB>();
	static
	{
		for (EAN128DB ean : EAN128DB.values())
			EAN_BY_DB.put(Integer.valueOf(ean.getDB()), ean);
		//check if one DB is the prefix of another one - and terminate if this is the case.
		//reimplement EAN128.get when this happens.
		EAN128DB[] values = EAN128DB.values();
		for (int i = 0; i < values.length; i++)
			for (int k = 0; k < values.length; k++)
				if (k != i && values[i].name().startsWith(values[k].name()))
					throw new RuntimeException("This is an assertion that is raised. The EAN128.get() method will not work when this assertion triggers. Please update EAN128.get().");
	}


	private final IEAN128Parser ean128;
	private final int db;

	
	private EAN128DB(String def)
	{
		this.ean128 = findParser(def);
		this.db = db(this.name());
		
	}
	
	
	/**
	 * @return The int based ID of this entry.
	 */
	public int getDB()
	{
		return this.db;
	}
	
	/**
	 * @return The (stateless) parser that is able to parse a record of this type.
	 */
	public IEAN128Parser parser()
	{
		return this.ean128;
	}
	
	/**
	 * Convenient method to parse a given byte buffer at the current position of the byte buffer.
	 * @return An array with the result. 
	 * @throws ParseException If the record could not be parsed.
	 */
	public String[] parseData(ByteBuffer bb, byte fnc1) throws ParseException
	{
		return this.parser().parse(bb, fnc1);
	}
	

	private static final IEAN128Parser findParser(String def)
	{
		Matcher matcher = EAN128FixedParser.PATTERN.matcher(def);
		if (matcher.matches())
			return new EAN128FixedParser(def);
		matcher = EAN128VarParser.PATTERN.matcher(def);
		if (matcher.matches())
			return new EAN128VarParser(def);
		matcher = EAN128ArrayParser.PATTERN.matcher(def);
		if (matcher.matches())
			return new EAN128ArrayParser(def);
		throw new IllegalArgumentException("Could not find parser for given definition " + def);
	}

	private static final int db(String name)
	{
		Matcher matcher = Pattern.compile("^DB(\\d{2,4})$").matcher(name);
		if (!matcher.matches())
			throw new RuntimeException("Should not happen. Item: " + name);
		String db = matcher.group(1);
		if (db == null)
			throw new RuntimeException("Should not happen. Item: " + name);
		return Integer.parseInt(db);
	}

	
	/**
	 * Method that parses the byte buffer at the current position to determine the id of the next record.
	 * The appropriate db entry is for this record returned.
	 * @param bb The byte buffer.
	 * @return The db entry with the parser for the next record.
	 */
	static final EAN128DB get(ByteBuffer bb)
	{
		int size = 2;
		int db = char2Num(bb.get()) * 10 + char2Num(bb.get());
		EAN128DB found = get(db);
		while (found == null && size <= 4)
		{
			db = db * 10 + char2Num(bb.get());
			found = get(db);
			size++;
		}
		return found;
	}

	private static final int char2Num(byte char_)
	{
		return char_ - 48;
	}
	
	/**
	 * Returns the db entry for the given db id. <code>null</code> if the id is unknown.
	 * @param db The id of the db entry.
	 * @return The db entry if known. Otherwise <code>null</code>.
	 */
	public static final EAN128DB get(int db)
	{
		return EAN_BY_DB.get(Integer.valueOf(db));
	}

}
