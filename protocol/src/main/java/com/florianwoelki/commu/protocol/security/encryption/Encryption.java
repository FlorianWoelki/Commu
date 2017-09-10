package com.florianwoelki.commu.protocol.security.encryption;

import com.florianwoelki.commu.protocol.security.Security;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Florian Woelki on 06.03.17.
 *
 * This class represents the encryption of any given data
 * as a string.
 * It inherits from the {@link Security} class.
 */
public class Encryption extends Security {

    /**
     * This method encrypts the given data.
     *
     * @param data String which will be encrypted
     * @return Encrypted string
     * @throws NoSuchPaddingException    If a error occurred
     * @throws NoSuchAlgorithmException  If a error occurred
     * @throws InvalidKeyException       If a error occurred
     * @throws BadPaddingException       If a error occurred
     * @throws IllegalBlockSizeException If a error occurred
     */
    public String encrypt(String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedValue = cipher.doFinal(data.getBytes());
        return new BASE64Encoder().encode(encryptedValue);
    }

}
