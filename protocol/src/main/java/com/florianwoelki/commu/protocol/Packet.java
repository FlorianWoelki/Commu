package com.florianwoelki.commu.protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Florian Woelki on 24.02.17.
 */
public abstract class Packet {

    // username;time;message
    private List<String> dataList = new ArrayList<>();

    private PacketType packetType;

    // Read the incoming data from the same packet
    public Packet(String[] rawData) {
        for(String segment : rawData) {
            dataList.add(segment);
        }
    }

    // Outgoing packet constructor
    public Packet(PacketType packetType) {
        this.packetType = packetType;
    }

    protected String getData(int index) {
        return dataList.get(index);
    }

    // Outgoing packet methods
    protected void addData(String data) {
        addData(data, 0);
    }

    protected void addData(String data, int index) {
        dataList.add(index, data);
    }

    protected abstract void indexOutgoingData();

    public String getOutgoingData() {
        // Request the child packets to create the raw data list to be sent to the client
        dataList.clear();
        indexOutgoingData();
        return compileOutgoingData();
    }

    protected String compileOutgoingData() {
        StringBuffer buffer = new StringBuffer(packetType.name()).append(";");
        // LOGIN;
        for(int i = dataList.size() - 1; i >= 0; i--) {
            String data = dataList.get(i);
            buffer.append(data).append(";");
        }
        return buffer.toString();
    }

}
