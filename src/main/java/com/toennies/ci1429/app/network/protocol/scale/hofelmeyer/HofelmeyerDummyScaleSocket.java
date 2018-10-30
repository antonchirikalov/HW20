package com.toennies.ci1429.app.network.protocol.scale.hofelmeyer;

import com.toennies.ci1429.app.network.connector.BlockingByteBuffer;
import com.toennies.ci1429.app.network.parameter.IConfigContainer;
import com.toennies.ci1429.app.network.socket.AtSocket;
import com.toennies.ci1429.app.network.socket.ISocket;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.util.concurrent.TimeoutException;


@AtSocket("Hofelmeyer Dummy Socket Protocol")
public class HofelmeyerDummyScaleSocket implements ISocket {

    protected static final Logger logger = LogManager.getLogger();
    private static final int MAX_BUFFER_SIZE = 4096;
    public static final String RESPONSE_OK = "<00>\r\n";
    private final BlockingByteBuffer buffer = new BlockingByteBuffer(MAX_BUFFER_SIZE);
    private boolean isConnected = false;


    @Override
    public void connect(IConfigContainer config) throws IOException {
        this.buffer.clear();
        this.isConnected = true;
    }

    @Override
    public boolean isConnected() {
        return true;
    }


    private byte[] generateSimpleResponse() {
        return RESPONSE_OK.getBytes();
    }


    private String removeSpecialCharacters(byte[] request) {
        return StringUtils.removePattern(new String(request), "[<>\\n\\r]");
    }


    private byte[] generateDummyMessage(){
        DummyMessageBuilder builder = new DummyMessageBuilder();
        return builder.setDate().setTime().setGrossWeight().setNetWeight().setTareWeight().setUnit().getMessageWithCRC();
    }

    private byte[] processResponse() {

        byte[] request = buffer.pollData();
        if (request != null) {
            switch (removeSpecialCharacters(request)) {
                case "RN":
                case "RM":
                    return generateDummyMessage();
                default:
                    return generateSimpleResponse();
            }

        } else {
            return null;
        }
    }


    @Override
    public byte[] poll() throws IOException {
        return processResponse();
    }

    @Override
    public byte[] pop() throws IOException, TimeoutException {
        return processResponse();
    }

    @Override
    public void push(byte[] entity) throws IOException {
        try {
            this.buffer.pushData(entity);
        } catch (InterruptedException e) {
            logger.error("Could not connect to device.", e);
        }
    }

    @Override
    public void disconnect() throws IOException {
        this.isConnected = false;
    }

    @Override
    public void shutdown() {
        this.isConnected = false;
    }
}
