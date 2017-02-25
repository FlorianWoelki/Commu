package com.florianwoelki.commu.protocol.packet;

import com.florianwoelki.commu.protocol.Packet;
import com.florianwoelki.commu.protocol.PacketType;

/**
 * Created by Florian Woelki on 24.02.17.
 */
public class ConnectPacket extends Packet {

    public String i_username;

    private String o_username;

    // Incoming constructor
    public ConnectPacket(String[] rawData) {
        super(rawData);

        i_username = getData(1);
    }

    // Outgoing constructor
    public ConnectPacket(String username) {
        super(PacketType.CONNECT);

        this.o_username = username;
    }

    @Override
    protected void indexOutgoingData() {
        addData(o_username);
    }

}
