package com.florianwoelki.commu.protocol;

/**
 * Created by Florian Woelki on 24.02.17.
 *
 * This class represents the packet type.
 * It must be specified for every single packet.
 */
public enum PacketType {

    CONNECT, // If a client connects to the server
    CHAT, // If a client sends a message
    DISCONNECT // If a client disconnects from the server

}
