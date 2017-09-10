package com.florianwoelki.commu.protocol.packet;

import com.florianwoelki.commu.protocol.Packet;
import com.florianwoelki.commu.protocol.PacketType;

/**
 * Created by Florian Woelki on 26.02.17.
 */
public class ChatPacket extends Packet {

    public String i_username;
    public String i_message;

    private String o_username;
    private String o_message;

    /**
     * See {@link Packet}
     * It sets the incoming username and message.
     *
     * @param rawData String[] with the given raw data
     */
    public ChatPacket(String[] rawData) {
        super(rawData);

        i_username = getData(1);
        i_message = getData(2);
    }

    /**
     * See {@link Packet}
     * It sets the outgoing username and message.
     *
     * @param username String with the given username
     * @param message  String with the given message
     */
    public ChatPacket(String username, String message) {
        super(PacketType.CHAT);

        this.o_username = username;
        this.o_message = message;
    }

    /**
     * See {@link Packet#indexOutgoingData()}
     */
    @Override
    protected void indexOutgoingData() {
        addData(o_username);
        addData(o_message);
    }

}
