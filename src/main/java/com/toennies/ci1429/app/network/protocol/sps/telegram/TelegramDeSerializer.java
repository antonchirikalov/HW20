/**
 * 
 */
package com.toennies.ci1429.app.network.protocol.sps.telegram;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import com.toennies.ci1429.app.network.connector.AFlexibleWrapperTransformer;
import com.toennies.ci1429.app.network.connector.IConnector;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.message.Message;
import com.toennies.ci1429.app.util.binary.pojo.BPojoReader;
import com.toennies.ci1429.app.util.binary.pojo.BPojoWriter;
import com.toennies.ci1574.lib.helper.Generics;

/**
 * Simple transformer that takes a list of pojos and serializes and deserializes them from to byte[] arrays.
 * @author renkenh
 */
public class TelegramDeSerializer extends AFlexibleWrapperTransformer<Telegram, Telegram, IMessage, IMessage> implements IConnector<Telegram>
{
	
	/**
	 * Mapping information used by this transformer to map a pojo from/to a Tönnies telegram.
	 * @author renkenh
	 */
	public static final class TelegramPojoMapping
	{
		public final byte id;
		public final byte dialog;
		public final Class<? extends Telegram> pojoClass;
		
		public TelegramPojoMapping(int id, int dialog, Class<? extends Telegram> pojoClass)
		{
			this.id = (byte) id;
			this.dialog = (byte) dialog;
			this.pojoClass = pojoClass;
		}
		
		
		/**
		 * Unique identifier. Follows portal standard.
		 * @return The unique identifier based on the Tönnies Telegram specifications.
		 */
		public String key()
		{
			return this.id + "|" + this.dialog;
		}
	}
	
	
	private final HashMap<String, BPojoReader<? extends Telegram>> pojoMapping = new HashMap<>();
	private final BPojoReader<Telegram> telegramReader = new BPojoReader<>(Telegram.class);


	/**
	 * @param connector
	 */
	public TelegramDeSerializer(IConnector<IMessage> connector, Collection<Class<? extends Telegram>> classesToMap)
	{
		super(connector);
		classesToMap.stream().map((c) -> MappingUtil.getMapping(c)).filter((m) -> m != null).forEach((m) -> this.pojoMapping.put(m.key(), new BPojoReader<>(m.pojoClass)));
	}


	@Override
	protected Telegram transformToOut(IMessage entity) throws IOException
	{
		if (entity == null)
			return null;
		Telegram tele = this.telegramReader.parse(entity.words().get(0));
		BPojoReader<? extends Telegram> reader = this.pojoMapping.get(tele.getKey());
		if (reader != null)
			return reader.parse(entity.words().get(0));
		return tele;
	}

	@Override
	protected IMessage transformToConIn(Telegram entity) throws IOException
	{
		BPojoWriter<?> writer = new BPojoWriter<>(entity.getClass());
		return new Message(writer.write(Generics.convertUnchecked(entity)));
	}

}
