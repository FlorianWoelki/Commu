package com.florianwoelki.commu.client.network;

import com.florianwoelki.commu.client.CommuClient;
import com.florianwoelki.commu.protocol.Packet;
import com.florianwoelki.commu.protocol.PacketDictionary;
import com.florianwoelki.commu.protocol.PacketType;
import com.florianwoelki.commu.protocol.packet.ChatPacket;
import com.florianwoelki.commu.protocol.packet.ConnectPacket;
import com.florianwoelki.commu.protocol.packet.DisconnectPacket;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Florian Woelki on 24.02.17.
 *
 * This class is the NetworkClient.
 * It handles all the networking logic for the client.
 */
public class NetworkClient {

    private Socket socket;
    private String ipAddress;
    private int port;

    private String username;

    private Map<String, Socket> connectedClients = new HashMap<>();

    /**
     * This constructor creates a instance of a NetworkClient with the given
     * ip address and port.
     *
     * @param ipAddress String contains a IP4Address
     * @param port      Integer contains given port, which is not already used
     */
    public NetworkClient(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    /**
     * This method connects the client to the server.
     * It creates the socket and starts handling the incoming packets
     * from the server.
     * In addition, it sends a packet to the server for knowing the given username.
     */
    public void connectToServer() {
        // Creates the connection to the server
        try {
            socket = new Socket(ipAddress, port);
        } catch(IOException e) {
            e.printStackTrace();
            return;
        }

        // Input for the username
        username = JOptionPane.showInputDialog(null, "Enter a username: ", "Input Prompt", JOptionPane.QUESTION_MESSAGE);
        try {
            sendPacket(new ConnectPacket(username));
        } catch(IOException e) {
            e.printStackTrace();
        }

        startPacketReadingThread();
    }

    /**
     * When this method gets called, it starts listening for incoming
     * packets from the server.
     */
    private void startPacketReadingThread() {
        new Thread(() -> {
            while(true) {
                try {
                    // Construct the packet
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    String rawData = input.readUTF();
                    String[] data = rawData.trim().split(";");
                    PacketType packetType = PacketType.valueOf(data[0]);
                    Packet packet = PacketDictionary.translatePacketType(packetType, data);

                    // Read the incoming packet information
                    readPacket(packet);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * This method reads a given packet and executes the logic
     * of the packet.
     *
     * @param packet Packet with given information
     */
    private void readPacket(Packet packet) {
        if(packet instanceof ConnectPacket) { // ConnectPacket
            ConnectPacket connectPacket = (ConnectPacket) packet;
            connectedClients.put(connectPacket.i_username, socket);
            CommuClient.getInstance().updateView();
        } else if(packet instanceof DisconnectPacket) { // DisconnectPacket
            DisconnectPacket disconnectPacket = (DisconnectPacket) packet;
            connectedClients.remove(disconnectPacket.i_username);
            CommuClient.getInstance().updateView();
        } else if(packet instanceof ChatPacket) { // ChatPacket
            ChatPacket chatPacket = (ChatPacket) packet;
            CommuClient.getInstance().appendText(chatPacket.i_username, chatPacket.i_message);
        }
    }

    /**
     * Send a given packet to the server.
     *
     * @param packet Packet with given information which will be sent
     * @throws IOException If writing does not work
     */
    public void sendPacket(Packet packet) throws IOException {
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        output.writeUTF(packet.getOutgoingData());
    }

    /**
     * Returns the given username from the connected client.
     *
     * @return Username as String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the connected clients to the server.
     *
     * @return ConnectedClients as Map
     */
    public Map<String, Socket> getConnectedClients() {
        return connectedClients;
    }
}
