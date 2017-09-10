package com.florianwoelki.commu.protocol;

import java.net.Socket;

/**
 * Created by Florian Woelki on 24.02.17.
 *
 * This class represents a listener for all received
 * and sent packets.
 */
public interface PacketListener {

    /**
     * Send a packet to a specific socket.
     *
     * @param packet Packet with all information
     * @param client Socket, who send the packet
     */
    void packetSent(Packet packet, Socket client);

    /**
     * Checks if a socket has received a packet.
     *
     * @param packet Packet which will be received
     * @param client Socket, who received the packet
     */
    void packetReceived(Packet packet, Socket client);

}
