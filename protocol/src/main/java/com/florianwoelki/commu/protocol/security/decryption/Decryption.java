package com.florianwoelki.commu.protocol.security.decryption;

import com.florianwoelki.commu.protocol.security.Security;
import sun.misc.BASE64Decoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Florian Woelki on 06.03.17.
 *
 * This class represents the decryption of any given data
 * as a string.
 * It inherits from the {@link Security} class.
 */
public class Decryption extends Security {

    /**
     * This method decrypts the given data.
     *
     * @param encryptedData String which will be decrypted
     * @return Decrypted string
     * @throws NoSuchPaddingException    If a error occurred
     * @throws NoSuchAlgorithmException  If a error occurred
     * @throws InvalidKeyException       If a error occurred
     * @throws IOException               If a error occurred
     * @throws BadPaddingException       If a error occurred
     * @throws IllegalBlockSizeException If a error occurred
     */
    public String decrypt(String encryptedData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = cipher.doFinal(decryptedValue);
        return new String(decValue);
    }

}
