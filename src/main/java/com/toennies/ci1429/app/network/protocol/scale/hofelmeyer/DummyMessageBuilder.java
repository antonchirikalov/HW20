package com.toennies.ci1429.app.network.protocol.scale.hofelmeyer;

import com.toennies.ci1429.app.util.CRC16;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

public class DummyMessageBuilder{

    char[] SPACES = new char[54];
    StringBuilder message = new StringBuilder();

    private static final int DATE_VALUE_POSITION = 4;
    private static final int TIME_VALUE_POSITION = 12;

    private static final int NETTO_VALUE_POSITION = 38;
    private static final int GROSS_VALUE_POSITION = 22;
    private static final int TARE_VALUE_POSITION = 30;
    private static final int UNIT_VALUE_POSITION = 46;
    private static final int DOUBLE_VALUE_LENGTH = 8;

    private static final int CRC_VALUE_POSITION = 54;
    private static final int CRC_VALUE_LENGTH = 8;

    private static final int WEIGHT_UNIT_VALUE_POSITION = 45;

    private List<String> unitList = new ArrayList<>(4);

    {
        unitList.add("kg");
        unitList.add("g");
        unitList.add("t");
        unitList.add("lb");
    }

    public DummyMessageBuilder(){
        Arrays.fill(SPACES,' ');
        message.append(SPACES);  //now message initiolized with 62 spaces
        message.replace(0,4, "0000");
    }


    public DummyMessageBuilder setDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
        message.replace(DATE_VALUE_POSITION, DATE_VALUE_POSITION + DOUBLE_VALUE_LENGTH, sdf.format(new Date()));
        return this;
    }

    public DummyMessageBuilder setTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        message.replace(TIME_VALUE_POSITION, TIME_VALUE_POSITION + 5,sdf.format((new Date())));
        return this;
    }

    public DummyMessageBuilder setNetWeight(){
        String randomWeight = String.format("%.02f", new Random().nextFloat() * 1000);
        randomWeight = StringUtils.leftPad(randomWeight, DOUBLE_VALUE_LENGTH);
        message.replace(NETTO_VALUE_POSITION, NETTO_VALUE_POSITION + DOUBLE_VALUE_LENGTH, randomWeight);
        return this;
    }

    public DummyMessageBuilder setGrossWeight(){
        String grossWeight = String.format("%.02f", new Random().nextFloat() * 1000);
        grossWeight = StringUtils.leftPad(grossWeight, DOUBLE_VALUE_LENGTH);
        message.replace(GROSS_VALUE_POSITION, GROSS_VALUE_POSITION + DOUBLE_VALUE_LENGTH, grossWeight);
        return this;
    }

    public DummyMessageBuilder setTareWeight(){
        String tareWeight = String.format("%.02f", new Random().nextFloat() * 100);
        tareWeight = StringUtils.leftPad(tareWeight, DOUBLE_VALUE_LENGTH);
        message.replace(TARE_VALUE_POSITION, TARE_VALUE_POSITION + DOUBLE_VALUE_LENGTH, tareWeight);
        return this;
    }

    public DummyMessageBuilder setUnit(){
        message.replace(UNIT_VALUE_POSITION, UNIT_VALUE_POSITION + 2, getRandomUnit());
        return this;
    }


    private DummyMessageBuilder setControlCharacters(){
        message.insert(0, "<");
        message.append(">\r\n");
        return this;
    }


    public byte[] getMessageWithCRC(){
        String crc = String.valueOf(CRC16.crc16(message.toString().getBytes()));
        crc = StringUtils.leftPad(crc, 8);
        message.append(crc);
        setControlCharacters();
        return message.toString().getBytes();
    }


    private String getRandomUnit() {
        Collections.shuffle(unitList);
        return StringUtils.leftPad(unitList.get(0), 2);
    }
}