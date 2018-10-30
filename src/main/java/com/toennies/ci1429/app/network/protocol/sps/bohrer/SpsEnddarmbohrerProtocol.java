package com.toennies.ci1429.app.network.protocol.sps.bohrer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.toennies.ci1429.app.model.DeviceResponse;
import com.toennies.ci1429.app.model.DeviceType;
import com.toennies.ci1429.app.network.connector.DataTransformer;
import com.toennies.ci1429.app.network.connector.FlexibleReConnector;
import com.toennies.ci1429.app.network.connector.IFlexibleConnector;
import com.toennies.ci1429.app.network.connector.LoggingConnector;
import com.toennies.ci1429.app.network.parameter.AtDefaultParameters.Parameter;
import com.toennies.ci1429.app.network.protocol.AtProtocol;
import com.toennies.ci1429.app.network.protocol.isotcp.ISOonTCPConnector;
import com.toennies.ci1429.app.network.protocol.isotcp.MSGTPDUFilter;
import com.toennies.ci1429.app.network.protocol.isotcp.NSDUTransformer;
import com.toennies.ci1429.app.network.protocol.isotcp.TPDUTransformer;
import com.toennies.ci1429.app.network.protocol.sps.ASpsProtocol;
import com.toennies.ci1429.app.network.protocol.sps.SpsCommand;
import com.toennies.ci1429.app.network.protocol.sps.SpsRequest;
import com.toennies.ci1429.app.network.protocol.sps.telegram.Telegram;
import com.toennies.ci1429.app.network.protocol.sps.telegram.TelegramDeSerializer;
import com.toennies.ci1429.app.network.socket.ISocket;
import com.toennies.ci1429.app.network.socket.TCPSocket;

/**
 * These commands can also be found in hw10 implemtantion. Have a look in
 * clsTCPServer.cls#374
 */
@AtProtocol(value = "Sps Enddarmbohrer Test", deviceType = DeviceType.SPS)
@Parameter(name=TCPSocket.PARAM_HOST, value="10.234.73.106")
@Parameter(name=TCPSocket.PARAM_PORT, value="102")
@Parameter(name=ISOonTCPConnector.PARAM_SOURCEID, value="SL0PC31")
@Parameter(name=ISOonTCPConnector.PARAM_DESTID, value="SL0SPS31")
public class SpsEnddarmbohrerProtocol extends ASpsProtocol
{

	public static final SpsCommand GET_INFOS = new SpsCommand("GET_INFOS");
	public static final SpsCommand GET_STATUS = new SpsCommand("GET_STATUS");
	

	@Override
	protected IFlexibleConnector<SpsRequest, DeviceResponse> createPipeline(ISocket socket)
	{
		LoggingConnector<byte[]> logging = new LoggingConnector<>(socket);
		DataTransformer transformer = new DataTransformer(new TPDUTransformer(), logging);
		MSGTPDUFilter converter = new MSGTPDUFilter(transformer);
		NSDUTransformer nsdu = new NSDUTransformer(converter);
		ISOonTCPConnector performer = new ISOonTCPConnector(nsdu);
		
		List<Class<? extends Telegram>> mappings = Arrays.asList(BohrerInfo.class, StatusResponse.class);
		
		TelegramDeSerializer serializer = new TelegramDeSerializer(performer, mappings);
		InfoCollector collector = new InfoCollector(serializer);
		RequestHandler handler = new RequestHandler(collector);
		FlexibleReConnector<SpsRequest, DeviceResponse> reConnector = new FlexibleReConnector<>(handler);
		return reConnector;
	}

	
	@Override
	public List<SpsCommand> getSupportedCommands()
	{
		return Collections.unmodifiableList(Arrays.asList(GET_INFOS, GET_STATUS));
	}

}
