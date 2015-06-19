/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ro.ssc.app.client.licensing;

/**
 *
 * @author DauBufu
 */


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;


public class TrialKeyGenerator {

    private static final int ITERATIONS_ENCRYPT = 10000;
    private static final String SECRET_KEY_FACTORY = "PBKDF2WithHmacSHA1";
    private static final String CIPHER = "AES/ECB/PKCS5Padding";
    private static final String ALGORITHM = "AES";
    private static final int KEY_LENGTH = 128;
    private static final String PASS_ENCRYPT = "777DAUBUFU$$$";
    private static final String SALT_ENCRYPT = "RO.SSC.SPT";

    public static String generateKey(String toEncode) {

        String encoded = "";
        try {
            byte[] saltEncrypt = SALT_ENCRYPT.getBytes();
            SecretKeyFactory factoryKeyEncrypt = SecretKeyFactory.getInstance(SECRET_KEY_FACTORY);
            SecretKey tmp = factoryKeyEncrypt.generateSecret(new PBEKeySpec(PASS_ENCRYPT.toCharArray(), saltEncrypt, ITERATIONS_ENCRYPT, KEY_LENGTH));
            SecretKeySpec encryptKey = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
            Cipher aesCipherEncrypt = Cipher.getInstance(CIPHER);
            aesCipherEncrypt.init(Cipher.ENCRYPT_MODE, encryptKey);
            byte[] bytes = StringUtils.getBytesUtf8(toEncode);
            byte[] encryptBytes = aesCipherEncrypt.doFinal(bytes);
            encoded = Base64.encodeBase64URLSafeString(encryptBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encoded;
    }

    public static void main(String args[]) {
        if (args.length > 0) {
            System.out.println(generateKey(args[0]));
        }
        else {
            System.out.println("Usage:\n\tjava TrialKeyGenerator <dateToEncode as dd-MM-yyyy>");
        }
    }
}
