package com.toennies.ci1429.app.network.isotcp;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.toennies.ci1429.app.network.connector.DataTransformer;
import com.toennies.ci1429.app.network.connector.LoggingConnector;
import com.toennies.ci1429.app.network.message.IMessage;
import com.toennies.ci1429.app.network.parameter.AConfigContainer;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.protocol.IProtocol;
import com.toennies.ci1429.app.network.protocol.isotcp.ISOonTCPConnector;
import com.toennies.ci1429.app.network.protocol.isotcp.MSGTPDUFilter;
import com.toennies.ci1429.app.network.protocol.isotcp.NSDUTransformer;
import com.toennies.ci1429.app.network.protocol.isotcp.TPDUTransformer;
import com.toennies.ci1429.app.network.protocol.isotcp.tpdu.TPDU;
import com.toennies.ci1429.app.network.protocol.sps.bohrer.BohrerInfo;
import com.toennies.ci1429.app.network.protocol.sps.bohrer.InfoCollector;
import com.toennies.ci1429.app.network.protocol.sps.telegram.Telegram;
import com.toennies.ci1429.app.network.protocol.sps.telegram.TelegramDeSerializer;
import com.toennies.ci1429.app.network.socket.ATCPSocket;
import com.toennies.ci1429.app.network.socket.TCPSocket;
import com.toennies.ci1429.app.util.binary.pojo.BPojoReader;

public class ISOonTCPTest
{

	public static void main(String[] args) throws Exception, TimeoutException
	{
		BPojoReader<BohrerInfo> reader = new BPojoReader<>(BohrerInfo.class);
		
		TCPSocket socket = new TCPSocket();
		LoggingConnector<byte[]> logging = new LoggingConnector<>(socket);
		DataTransformer transformer = new DataTransformer(new TPDUTransformer(), logging);
		MSGTPDUFilter converter = new MSGTPDUFilter(transformer);
		NSDUTransformer nsdu = new NSDUTransformer(converter);
		ISOonTCPConnector performer = new ISOonTCPConnector(nsdu);
		
		List<Class<? extends Telegram>> mappings = Arrays.asList(BohrerInfo.class);
		
		TelegramDeSerializer serializer = new TelegramDeSerializer(performer, mappings);
		InfoCollector collector = new InfoCollector(serializer);
		
		performer.connect(descriptionTCP());
		Runnable r2 = () ->
		{
			while (true)
			{
				try
				{
					IMessage msg = performer.pop();
					if (msg != null && !(msg instanceof TPDU))
					{
						System.out.println(msg);
						BohrerInfo info = reader.parse(msg.words().get(0));
						System.out.println("Id:     " + info.getId());
						System.out.println("Dialog: " + info.getDialog());
						System.out.println("Number: " + info.getNumber());
						System.out.println("Förderer:   " + info.getFoerdererID());
						System.out.println("Kettentakt: " + info.getTakt());
						System.out.println("Stich:      " + info.getStich());
						System.out.println("Puls:       " + info.getPuls());
						System.out.println("Transponder:" + info.getTransponder());
						System.out.println("Status:     " + info.getAddInfo());
						System.out.println("###############################");
					}
				}
				catch (IOException | TimeoutException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		new Thread(r2).start();
		System.err.println("Finished");
//		while (true)
//		{
//			IMessage m = messages.poll(60, TimeUnit.SECONDS);
//			System.out.println(ASCII.formatHuman(m.words().get(0)));
//			Telegram t = new Telegram(m.words().get(0));
//			System.out.println(ReflectionToStringBuilder.toString(t));
//		}
	}

	static final IConfigContainer descriptionTCP()
	{
		return new AConfigContainer()
		{
			private final Map<String, String> map = parametersTCP();
			
			@Override
			protected Map<String, String> _config()
			{
				return map;
			}
		};
	}
	
	static final Map<String, String> parametersTCP()
	{
		HashMap<String, String> map = new HashMap<>();
		map.put(IProtocol.PARAM_SOCKET, TCPSocket.class.getName());
		map.put(TCPSocket.PARAM_HOST, "10.234.73.106");
		map.put(TCPSocket.PARAM_PORT, "102");
		map.put(TCPSocket.PARAM_TIMEOUT, "30000");
		map.put(ATCPSocket.PARAM_PING, "true");
		map.put(ISOonTCPConnector.PARAM_SOURCEID, "SL0PC31");
		map.put(ISOonTCPConnector.PARAM_DESTID, "SL0SPS31");
		return map;
	}

}
