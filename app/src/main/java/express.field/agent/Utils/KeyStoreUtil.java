package express.field.agent.Utils;

import static express.field.agent.Utils.Constants.KEYSTORE_ALIAS;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;


import express.field.agent.Helpers.ContextProvider;
import express.field.agent.Pref.GlobalPref;

public class KeyStoreUtil {

    public static KeyStore getAndroidKeyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            if (!KeyStoreUtil.containsAlias(keyStore, KEYSTORE_ALIAS)) {
                KeyStoreUtil.createKeyPair(ContextProvider.getInstance().getContext(), keyStore, KEYSTORE_ALIAS);
            }

            return keyStore;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean containsAlias(KeyStore keyStore, String alias) {
        try {
            return keyStore.containsAlias(alias);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void createKeyPair(Context context, KeyStore keyStore, String alias) {
        try {
            if (keyStore.containsAlias(alias)) {
                throw new IllegalStateException(String.format("Keystore already contains alias %s", alias));
            }

            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 1);
            KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias)
                .setSubject(new X500Principal("CN=DW Mobile, O=Android Authority"))
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .build();
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", keyStore.getProvider());
            generator.initialize(spec);

            KeyPair keyPair = generator.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static KeyPair genKeyPair(Context context, KeyStore keyStore, String alias) {
        KeyPair keyPair = null;
        try {
            if (keyStore.containsAlias(alias)) {
                throw new IllegalStateException(String.format("Keystore already contains alias %s", alias));
            }


            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 1);
            KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias)
                .setSubject(new X500Principal("CN=DW Mobile, O=Android Authority"))
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .build();
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", keyStore.getProvider());
            generator.initialize(spec);

            keyPair = generator.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyPair;
    }


    public static char[] encryptString(String plain, String alias) {
        try {
            if (getAndroidKeyStore() != null && !getAndroidKeyStore().containsAlias(alias)) {
                throw new IllegalStateException(String.format("Keystore does not contain alias %s", alias));
            }

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) getAndroidKeyStore().getEntry(alias, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

            Cipher input = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            input.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, input);
            cipherOutputStream.write(plain.getBytes(StandardCharsets.UTF_8));
            cipherOutputStream.close();

            byte[] vals = outputStream.toByteArray();
            String finalString = Base64.encodeToString(vals, Base64.DEFAULT);
            return StreamUtils.toChars(finalString.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static char[] decryptString(String encrypted, String alias) {
        try {
            if (getAndroidKeyStore() != null && !getAndroidKeyStore().containsAlias(alias)) {
                throw new IllegalStateException(String.format("Keystore does not contain alias %s", alias));
            }

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) getAndroidKeyStore().getEntry(alias, null);

            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

            CipherInputStream cipherInputStream = new CipherInputStream(
                new ByteArrayInputStream(Base64.decode(encrypted, Base64.DEFAULT)), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;

            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte) nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i).byteValue();
            }

            return StreamUtils.toChars(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static char[] encryptStringSym(String plain, String alias) {
        try {
            // Check if key is saved already
            String localKey = GlobalPref.getInstance().getString(Constants.ENCRYPTED_SYM_KEY, null);
            SecretKey secretKey;

            if (localKey == null) {
                secretKey = generateSecretKey();
            } else {
                char[] localKeyArray = decryptString(localKey, KEYSTORE_ALIAS);

                if (localKeyArray != null && localKeyArray.length > 0) {
                    secretKey = convertStringToSecretKey(String.valueOf(localKeyArray));
                } else {
                    secretKey = generateSecretKey();
                }
            }

            Cipher symCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            symCipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // Encrypt sym key
            byte[] plainBytes = symCipher.doFinal(plain.getBytes());
            char[] encryptedSymKey = encryptString(convertSecretKeyToString(secretKey), alias);

            if (encryptedSymKey == null) {
                return null;
            }

            // Save sym. key with preference service
            GlobalPref.getInstance().put(Constants.ENCRYPTED_SYM_KEY, String.valueOf(encryptedSymKey));

            String finalString = Base64.encodeToString(plainBytes, Base64.DEFAULT);
            return StreamUtils.toChars(finalString.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static char[] decryptStringSym(String encrypted, String alias) {
        try {
            // Get sym key locally
            String encryptedSymKey = GlobalPref.getInstance().getString(Constants.ENCRYPTED_SYM_KEY, null);

            if (encryptedSymKey == null) {
                return null;
            }

            // Decrypt sym key
            String decodedSymKey = String.valueOf(decryptString(encryptedSymKey, alias));

            if (decodedSymKey == null) {
                return null;
            }

            Cipher symCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            symCipher.init(Cipher.DECRYPT_MODE, convertStringToSecretKey(decodedSymKey));

            byte[] plainText = symCipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT));
            return StreamUtils.toChars(plainText);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    public static String convertSecretKeyToString(SecretKey secretKey) throws NoSuchAlgorithmException {
        byte[] rawData = secretKey.getEncoded();
        return Base64.encodeToString(rawData, Base64.DEFAULT);
    }

    public static SecretKey convertStringToSecretKey(String encodedKey) {
        byte[] decodedKey = Base64.decode(encodedKey, Base64.DEFAULT);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}