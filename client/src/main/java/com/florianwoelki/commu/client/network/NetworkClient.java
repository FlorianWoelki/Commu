package com.florianwoelki.commu.client.network;

import com.florianwoelki.commu.client.CommuClient;
import com.florianwoelki.commu.protocol.Packet;
import com.florianwoelki.commu.protocol.PacketType;
import com.florianwoelki.commu.protocol.packet.ConnectPacket;
import com.florianwoelki.commu.protocol.packet.DisconnectPacket;
import com.florianwoelki.commu.protocol.PacketDictionary;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Florian Woelki on 24.02.17.
 */
public class NetworkClient {

    private Socket socket;
    private String ipAddress;
    private int port;

    private String username;

    private Map<String, Socket> connectedClients = new HashMap<>();

    public NetworkClient(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public void connectToServer() {
        try {
            socket = new Socket(ipAddress, port);
        } catch(IOException e) {
            e.printStackTrace();
            return;
        }

        username = JOptionPane.showInputDialog(null, "Enter a username: ", "Input Prompt", JOptionPane.QUESTION_MESSAGE);
        try {
            sendPacket(new ConnectPacket(username));
        } catch(IOException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            while(true) {
                try {
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    String rawData = input.readUTF();
                    String[] data = rawData.trim().split(";");
                    PacketType packetType = PacketType.valueOf(data[0]);
                    Packet packet = PacketDictionary.translatePacketType(packetType, data);

                    readPacket(packet);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void readPacket(Packet packet) {
        if(packet instanceof ConnectPacket) {
            ConnectPacket connectPacket = (ConnectPacket) packet;
            connectedClients.put(connectPacket.i_username, socket);
            CommuClient.getInstance().updateView();
        } else if(packet instanceof DisconnectPacket) {
            DisconnectPacket disconnectPacket = (DisconnectPacket) packet;
            connectedClients.remove(disconnectPacket.i_username);
            CommuClient.getInstance().updateView();
        }
    }

    private void sendPacket(Packet packet) throws IOException {
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        output.writeUTF(packet.getOutgoingData());
    }

    public Map<String, Socket> getConnectedClients() {
        return connectedClients;
    }
}
