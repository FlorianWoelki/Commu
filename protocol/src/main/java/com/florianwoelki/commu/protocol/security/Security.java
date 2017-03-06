package com.florianwoelki.commu.protocol.security;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * Created by Florian Woelki on 06.03.17.
 */
public class Security {

    protected final String ALGORITHM = "AES";
    protected final byte[] KEY_VALUE = new byte[]{'T', 'h', 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y'};

    protected Key generateKey() {
        return new SecretKeySpec(KEY_VALUE, ALGORITHM);
    }

}
