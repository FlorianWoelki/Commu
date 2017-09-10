package com.florianwoelki.commu.server.network;

import com.florianwoelki.commu.protocol.Packet;
import com.florianwoelki.commu.protocol.PacketDictionary;
import com.florianwoelki.commu.protocol.PacketListener;
import com.florianwoelki.commu.protocol.PacketType;
import com.florianwoelki.commu.protocol.packet.ChatPacket;
import com.florianwoelki.commu.protocol.packet.ConnectPacket;
import com.florianwoelki.commu.protocol.packet.DisconnectPacket;
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
 *
 * This class represents the NetworkServer and the logic
 * for the server sided stuff.
 */
public class NetworkServer implements PacketListener {

    private ServerSocket socket;
    private boolean running = false;
    private int port;

    private List<PacketListener> packetListeners = new ArrayList<>();
    private Map<String, Socket> connectedClientMap = new HashMap<>();

    /**
     * This constructor creates a instance of the NetworkServer.
     * It also adds the packet listener to the server.
     *
     * @param port
     */
    public NetworkServer(int port) {
        this.port = port;

        addPacketListener(this);
    }

    /**
     * Add a packet listener to the server.
     *
     * @param packetListener PacketListener for receiving packets
     */
    private void addPacketListener(PacketListener packetListener) {
        packetListeners.add(packetListener);
    }

    /**
     * This method starts the server and starts listening
     * to incoming packets.
     */
    public void startServer() {
        // Starts the server
        try {
            socket = new ServerSocket(port);
            CommuServer.getInstance().log("Server socket initialized on port " + port);
        } catch(IOException e) {
            e.printStackTrace();
            return;
        }

        startPacketReading();

        running = true;
    }

    /**
     * This method starts reading incoming packets.
     */
    private void startPacketReading() {
        new Thread(() -> {
            CommuServer.getInstance().log("Listening for clients...");

            while(running) {
                try {
                    Socket client = socket.accept();
                    CommuServer.getInstance().log("Client has connected! " + client.getRemoteSocketAddress());

                    // Starting thread for listening to packets for each client
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

                        // Remove client, if it disconnect or a error occurred
                        removeClient(client);
                    }).start();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * This method broadcast a received packet to all packet listener
     *
     * @param packet Packet, which will be broadcasted
     * @param client Socket, who will receive the broadcast
     */
    private void broadcastPacketReceived(Packet packet, Socket client) {
        packetListeners.forEach(packetListener -> {
            packetListener.packetReceived(packet, client);
        });
    }

    /**
     * This method broadcast a sent packet to all packet listener
     *
     * @param packet Packet, which will be sent
     * @param client Socket, who will sent the broadcast
     */
    private void broadcastPacketSent(Packet packet, Socket client) {
        packetListeners.forEach(packetListener -> {
            packetListener.packetSent(packet, client);
        });
    }

    /**
     * This method stops the server.
     */
    public void stopServer() {
        running = false;
    }

    /**
     * See {@link PacketListener#packetSent(Packet, Socket)}
     *
     * @param packet Packet, which will be sent
     * @param client Socket, who receive the packet
     */
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

    /**
     * See {@link PacketListener#packetReceived(Packet, Socket)}
     *
     * @param packet Packet, which will be received
     * @param client Socket, who sent the packet
     */
    @Override
    public void packetReceived(Packet packet, Socket client) {
        if(packet instanceof ConnectPacket) { // ConnectPacket
            connectClient((ConnectPacket) packet, client);
        } else if(packet instanceof ChatPacket) { // ChatPacket
            chatClient((ChatPacket) packet, client);
        }
    }

    /**
     * This method will be called, whenever a client chats.
     *
     * @param packet ChatPacket, for knowing information about e.g. the message
     * @param client Socket, who will sent the ChatPacket
     */
    private void chatClient(ChatPacket packet, Socket client) {
        // Send a ChatPacket to all connected clients, that a new client has connected
        connectedClientMap.values().forEach(socket -> {
            if(!socket.getRemoteSocketAddress().equals(client.getRemoteSocketAddress())) {
                try {
                    sendPacket(new ChatPacket(packet.i_username, packet.i_message), socket);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * This method connects a client and notifies all the other
     * connected clients.
     *
     * @param packet ConnectPacket, for knowing information of the connected client
     * @param client Socket, who will connect to the server
     */
    private void connectClient(ConnectPacket packet, Socket client) {
        if(connectedClientMap.get(packet.i_username) != null) {
            return;
        }

        // Send the ConnectPacket to all connected clients
        connectedClientMap.keySet().forEach(username -> {
            try {
                sendPacket(new ConnectPacket(username), client);
            } catch(IOException e) {
                e.printStackTrace();
            }
        });

        connectedClientMap.put(packet.i_username, client);
        CommuServer.getInstance().updateView();
    }

    /**
     * This method removes a client from the server and the list.
     *
     * @param client Client, who will be removed
     */
    private void removeClient(Socket client) {
        Iterator<Map.Entry<String, Socket>> iter = connectedClientMap.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry<String, Socket> entry = iter.next();
            String username = entry.getKey();
            Socket socket = entry.getValue();
            if(socket.equals(client)) {
                iter.remove();

                // Send a DisconnectPacket to all other connected clients
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

    /**
     * This method sends a packet to a specific client.
     *
     * @param packet Packet, which will be sent to the client
     * @param client Socket, who will receive the packet
     * @throws IOException If something goes wrong
     */
    private void sendPacket(Packet packet, Socket client) throws IOException {
        DataOutputStream output = new DataOutputStream(client.getOutputStream());
        output.writeUTF(packet.getOutgoingData());
    }

    /**
     * This method returns the connected clients to the server.
     *
     * @return ConnectedClients as a Map
     */
    public Map<String, Socket> getConnectedClientMap() {
        return connectedClientMap;
    }

}
