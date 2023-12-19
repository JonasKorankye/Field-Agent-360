package express.field.agent.security;

import static express.field.agent.security.EncryptionConfig.getIv;
import static express.field.agent.security.EncryptionConfig.getPassword;
import static express.field.agent.security.EncryptionConfig.getSalt;

import android.util.Base64;
import android.util.Log;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.*;
import javax.crypto.spec.*;

class Encryption {
    private static final String TAG = Encryption.class.getSimpleName();

    public static byte[] encrypt(byte[] dataToEncrypt) {
        try {
            PBEKeySpec pbKeySpec = new PBEKeySpec(getPassword(), getSalt(), 1324, 256);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] keyBytes = secretKeyFactory.generateSecret(pbKeySpec).getEncoded();
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(getIv());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(dataToEncrypt);
            return encrypted;
        } catch (IllegalBlockSizeException ex) {
            Log.e(TAG, ex.getMessage());
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static byte[] decrypt(String dataToDecrypt) {
        byte[] decrypted = null;
        try {
            byte[] encrypted = Base64.decode(dataToDecrypt, Base64.DEFAULT);
            PBEKeySpec pbKeySpec = new PBEKeySpec(getPassword(), getSalt(), 1324, 256);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] keyBytes = secretKeyFactory.generateSecret(pbKeySpec).getEncoded();
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(getIv());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            decrypted = cipher.doFinal(encrypted);
            return decrypted;
        } catch (IllegalBlockSizeException ex) {
            Log.e(TAG, ex.getMessage());
        } catch (BadPaddingException ex) {
            Log.e(TAG, ex.getMessage());
        } catch (InvalidKeyException ex) {
            Log.e(TAG, ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            Log.e(TAG, ex.getMessage());
        } catch (NoSuchPaddingException ex) {
            Log.e(TAG, ex.getMessage());
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
        return null;
    }

}

