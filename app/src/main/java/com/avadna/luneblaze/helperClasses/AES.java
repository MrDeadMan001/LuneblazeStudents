package com.avadna.luneblaze.helperClasses;

import android.util.Base64;

import com.coremedia.iso.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    private static SecretKeySpec secretKey;
    private static byte[] key;

    public static void setKey(String myKey) {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            // key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            String newKey = new String(key, "UTF-8");
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //all Appkeys are encrypted using BASE64.DEFAULT

    public static String encrypt(String strToEncrypt, String secret) {
        try {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            //  String result=  Hex.encodeHex(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));

            String result = android.util.Base64.encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")),
                    Base64.URL_SAFE);
            return result;
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decrypt(String strToDecrypt, String secret) {
        try {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            String result = new String(cipher.doFinal(android.util.Base64.decode(strToDecrypt,
                    Base64.URL_SAFE)));
            return result;
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }

        try {
            setKey(secret);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            String result = new String(cipher.doFinal(android.util.Base64.decode(strToDecrypt,
                    Base64.DEFAULT)));
            return result;
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }

        return null;
    }
}
