package com.florianwoelki.commu.protocol.packet;

import com.florianwoelki.commu.protocol.Packet;
import com.florianwoelki.commu.protocol.PacketType;

/**
 * Created by Florian Woelki on 24.02.17.
 *
 * This class represents a disconnect packet.
 * This packet gets called whenever a client
 * disconnect from the server.
 */
public class DisconnectPacket extends Packet {

    public String i_username;

    private String o_username;

    /**
     * See {@link Packet}
     * It sets the incoming username.
     *
     * @param rawData String[] with the given raw data
     */
    public DisconnectPacket(String[] rawData) {
        super(rawData);

        i_username = getData(1);
    }

    /**
     * See {@link Packet}
     * It sets the outgoing username.
     *
     * @param username String with the given username
     */
    public DisconnectPacket(String username) {
        super(PacketType.DISCONNECT);

        this.o_username = username;
    }

    /**
     * See {@link Packet#indexOutgoingData()}
     */
    @Override
    protected void indexOutgoingData() {
        addData(o_username);
    }

}
