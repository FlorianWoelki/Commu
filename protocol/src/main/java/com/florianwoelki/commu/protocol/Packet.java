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
 */
public abstract class Packet {

    private Encryption encryption = new Encryption();
    private Decryption decryption = new Decryption();

    // username;time;message
    private List<String> dataList = new ArrayList<>();

    private PacketType packetType;

    // Read the incoming data from the same packet
    public Packet(String[] rawData) {
        for(String segment : rawData) {
            dataList.add(segment);
        }
    }

    // Outgoing packet constructor
    public Packet(PacketType packetType) {
        this.packetType = packetType;
    }

    protected String getData(int index) {
        String data = dataList.get(index);
        try {
            return decryption.decrypt(data);
        } catch(NoSuchPaddingException | NoSuchAlgorithmException | IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Outgoing packet methods
    protected void addData(String data) {
        addData(data, 0);
    }

    protected void addData(String data, int index) {
        try {
            String encryptedData = encryption.encrypt(data);
            dataList.add(index, encryptedData);
        } catch(NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    protected abstract void indexOutgoingData();

    public String getOutgoingData() {
        // Request the child packets to create the raw data list to be sent to the client
        dataList.clear();
        indexOutgoingData();
        return compileOutgoingData();
    }

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
