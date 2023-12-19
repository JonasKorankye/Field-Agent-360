package express.field.agent.security;

import android.text.TextUtils;
import android.util.Base64;
import java.security.SecureRandom;


import express.field.agent.Pref.GlobalPref;

public class EncryptionConfig {
    private static SecureRandom random = new SecureRandom();
    private static final String KEY_IV = "iv";
    private static final String KEY_SALT = "salt";
    private static final String KEY_PASSWORD = "password";
    private static final int SALT_LENGTH = 20;
    private static final int IV_LENGTH = 16;
    private static final int passwordLength = 10;
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static byte[] getSalt() {
        if (TextUtils.isEmpty(GlobalPref.getInstance().getString(KEY_SALT, ""))) {
            return generateSalt();
        } else {
            String base64Iv = GlobalPref.getInstance().getString(KEY_SALT, "");
            byte[] bytes = Base64.decode(base64Iv,Base64.DEFAULT);
            return new String(bytes).getBytes();
        }
    }

    public static char[] getPassword() {
        if (TextUtils.isEmpty(GlobalPref.getInstance().getString(KEY_PASSWORD, ""))) {
            return generatePassword();
        } else {
            String string = GlobalPref.getInstance().getString(KEY_PASSWORD, "");
            System.out.println(string.toCharArray());
            return string.toCharArray();
        }
    }

    public static byte[] getIv() {
        if (TextUtils.isEmpty(GlobalPref.getInstance().getString(KEY_IV, ""))) {
            return generateIv();
        } else {
            String base64Iv = GlobalPref.getInstance().getString(KEY_IV, "");
            byte[] bytes = Base64.decode(base64Iv,Base64.DEFAULT);
            return new String(bytes).getBytes();
        }
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    private static char[] generatePassword() {
        char[] password = new char[passwordLength];
        for (int i = 0; i < passwordLength; i++) {
            password[i] = ALPHABET.charAt(random.nextInt(ALPHABET.length()));
        }
        return password;
    }

    private static byte[] generateIv() {
        byte[] iv = new byte[IV_LENGTH];
        random.nextBytes(iv);
        return iv;
    }
}

