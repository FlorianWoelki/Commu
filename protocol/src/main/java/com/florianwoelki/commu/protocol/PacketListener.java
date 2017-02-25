package com.florianwoelki.commu.protocol;

import java.net.Socket;

/**
 * Created by Florian Woelki on 24.02.17.
 */
public interface PacketListener {

    void packetSent(Packet packet, Socket client);

    void packetReceived(Packet packet, Socket client);

}
