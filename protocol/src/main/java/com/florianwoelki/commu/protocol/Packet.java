package com.florianwoelki.commu.protocol;

import com.florianwoelki.commu.protocol.security.decryption.Decryption;
import com.florianwoelki.commu.protocol.security.encryption.Encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Florian Woelki on 24.02.17.
 *
 * This class represents a Packet which can be sent to the
 * server or the client.
 */
public abstract class Packet {

    private Encryption encryption = new Encryption();
    private Decryption decryption = new Decryption();

    // username;time;message
    private List<String> dataList = new ArrayList<>();

    private PacketType packetType;

    /**
     * This constructor is good for reading the incoming data
     * from the same packet.
     *
     * @param rawData String[] with the given raw data
     */
    public Packet(String[] rawData) {
        for(String segment : rawData) {
            dataList.add(segment);
        }
    }

    /**
     * This constructor is good for outgoing packets.
     *
     * @param packetType PacketType, which type the packet will be
     */
    public Packet(PacketType packetType) {
        this.packetType = packetType;
    }

    /**
     * Get the data from a specific index.
     * It also decrypt the data.
     *
     * @param index Int for the index of the data
     * @return Decrypted data
     */
    protected String getData(int index) {
        String data = dataList.get(index);
        try {
            return decryption.decrypt(data);
        } catch(NoSuchPaddingException | NoSuchAlgorithmException | IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method is good for outgoing packet methods.
     * See also {@link #addData(String, int)}
     *
     * @param data String which will be added to the 0 index
     */
    protected void addData(String data) {
        addData(data, 0);
    }

    /**
     * This method adds data to a specific index.
     *
     * @param data  String which will be added to the specific index
     * @param index Int, where the data will be
     */
    protected void addData(String data, int index) {
        try {
            String encryptedData = encryption.encrypt(data);
            dataList.add(index, encryptedData);
        } catch(NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method handles the outgoing data.
     * It is necessary for every single packet.
     */
    protected abstract void indexOutgoingData();

    /**
     * This method gets the outgoing data from the packet.
     *
     * @return String with the outgoing data
     */
    public String getOutgoingData() {
        // Request the child packets to create the raw data list to be sent to the client
        dataList.clear();
        indexOutgoingData();
        return compileOutgoingData();
    }

    /**
     * This method compiles the outgoing data.
     *
     * @return String with the compiled data
     */
    protected String compileOutgoingData() {
        StringBuilder buffer = new StringBuilder(packetType.name()).append(";");
        // LOGIN;
        for(int i = dataList.size() - 1; i >= 0; i--) {
            String data = dataList.get(i);
            buffer.append(data).append(";");
        }
        return buffer.toString();
    }

}
