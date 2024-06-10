package com.example.goofyconverter.Backend;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryption {

    public byte[] encryptMessage(String message, String key) {
        try {
            byte[] encodedkey = getHash(key);

            StringBuffer sb = new StringBuffer();

            for (byte b : encodedkey) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }

            SecretKeySpec secretKey = new SecretKeySpec(encodedkey, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] dataToSend = message.getBytes();

            return cipher.doFinal(dataToSend);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed!", e);
        }
    }

    public String decryptMessage(byte[] encodedData, String key) {
        try {
            byte[] encodedKey = getHash(key);

            SecretKeySpec k = new SecretKeySpec(encodedKey, "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, k);

            byte[] originalData = c.doFinal(encodedData);

            String strr = new String(originalData);
            return strr;
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed!", e);
        }
    }

    public byte[] getHash(String key) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        return digest.digest(key.getBytes("UTF-8"));
    }
}
