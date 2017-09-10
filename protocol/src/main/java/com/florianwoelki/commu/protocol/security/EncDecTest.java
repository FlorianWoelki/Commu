package com.florianwoelki.commu.protocol.security;

import com.florianwoelki.commu.protocol.security.decryption.Decryption;
import com.florianwoelki.commu.protocol.security.encryption.Encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Florian Woelki on 06.03.17.
 *
 * This is a basic encryption and decryption test.
 */
public class EncDecTest {

    public static void main(String[] args) {
        try {
            // Encrypt and decrypt the string
            String testString = "Hallo123";
            String stringEnc = new Encryption().encrypt(testString);
            String stringDec = new Decryption().decrypt(stringEnc);

            System.out.println("Plain text: " + testString);
            System.out.println("Encrypted text: " + stringEnc);
            System.out.println("Decrypted text: " + stringDec);
        } catch(NoSuchPaddingException | NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException | IOException e) {
            e.printStackTrace();
        }
    }

}
