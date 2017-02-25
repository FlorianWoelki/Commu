package com.florianwoelki.commu.server.network;

import com.florianwoelki.commu.protocol.Packet;
import com.florianwoelki.commu.protocol.PacketListener;
import com.florianwoelki.commu.protocol.PacketType;
import com.florianwoelki.commu.protocol.packet.ConnectPacket;
import com.florianwoelki.commu.protocol.packet.DisconnectPacket;
import com.florianwoelki.commu.protocol.PacketDictionary;
import com.florianwoelki.commu.server.CommuServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by Florian Woelki on 24.02.17.
 */
public class NetworkServer implements PacketListener {

    private ServerSocket socket;
    private boolean running = false;
    private int port;

    private List<PacketListener> packetListeners = new ArrayList<>();
    private Map<String, Socket> connectedClientMap = new HashMap<>();

    public NetworkServer(int port) {
        this.port = port;

        addPacketListener(this);
    }

    private void addPacketListener(PacketListener packetListener) {
        packetListeners.add(packetListener);
    }

    public void startServer() {
        try {
            socket = new ServerSocket(port);
            CommuServer.getInstance().log("Server socket initialized on port " + port);
        } catch(IOException e) {
            e.printStackTrace();
            return;
        }

        new Thread(() -> {
            CommuServer.getInstance().log("Listening for clients...");

            while(running) {
                try {
                    Socket client = socket.accept();
                    CommuServer.getInstance().log("Client has connected! " + client.getRemoteSocketAddress());

                    new Thread(() -> {
                        boolean error = false;
                        while(!error && client.isConnected()) {
                            try {
                                DataInputStream input = new DataInputStream(client.getInputStream());
                                String rawData = input.readUTF();
                                String[] data = rawData.trim().split(";");
                                PacketType packetType = PacketType.valueOf(data[0]);
                                Packet packet = PacketDictionary.translatePacketType(packetType, data);

                                broadcastPacketReceived(packet, client);
                                broadcastPacketSent(packet, client);

                                CommuServer.getInstance().log(client.getRemoteSocketAddress() + " sent packet:\n\t" + packet);
                            } catch(EOFException e) {
                                error = true;
                                CommuServer.getInstance().log("Client has disconnected! " + client.getRemoteSocketAddress());
                            } catch(IOException e) {
                                error = true;
                                e.printStackTrace();
                            }
                        }

                        removeClient(client);
                    }).start();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        running = true;
    }

    private void broadcastPacketReceived(Packet packet, Socket client) {
        packetListeners.forEach(packetListener -> {
            packetListener.packetReceived(packet, client);
        });
    }

    private void broadcastPacketSent(Packet packet, Socket client) {
        packetListeners.forEach(packetListener -> {
            packetListener.packetSent(packet, client);
        });
    }

    public void stopServer() {
        running = false;
    }

    @Override
    public void packetSent(Packet packet, Socket client) {
        if(packet instanceof ConnectPacket) {
            try {
                for(Socket socket : connectedClientMap.values()) {
                    ConnectPacket connectPacket = new ConnectPacket(((ConnectPacket) packet).i_username);
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream());

                    output.writeUTF(connectPacket.getOutgoingData());
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void packetReceived(Packet packet, Socket client) {
        if(packet instanceof ConnectPacket) {
            connectClient((ConnectPacket) packet, client);
        }
    }

    private void connectClient(ConnectPacket packet, Socket client) {
        if(connectedClientMap.get(packet.i_username) != null) {
            return;
        }

        connectedClientMap.put(packet.i_username, client);
        CommuServer.getInstance().updateView();
    }

    private void removeClient(Socket client) {
        Iterator<Map.Entry<String, Socket>> iter = connectedClientMap.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry<String, Socket> entry = iter.next();
            String username = entry.getKey();
            Socket socket = entry.getValue();
            if(socket.equals(client)) {
                iter.remove();
                connectedClientMap.values().forEach(connectedSocket -> {
                    try {
                        sendPacket(new DisconnectPacket(username), connectedSocket);
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        CommuServer.getInstance().updateView();
    }

    private void sendPacket(Packet packet, Socket client) throws IOException {
        DataOutputStream output = new DataOutputStream(client.getOutputStream());
        output.writeUTF(packet.getOutgoingData());
    }

    public Map<String, Socket> getConnectedClientMap() {
        return connectedClientMap;
    }

}
