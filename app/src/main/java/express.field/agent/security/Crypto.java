package express.field.agent.security;




import static express.field.agent.Utils.Constants.KEYSTORE_ALIAS;
import static express.field.agent.Utils.KeyStoreUtil.genKeyPair;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.security.crypto.MasterKey;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.ECDHEncrypter;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.SignedJWT;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;

import express.field.agent.Helpers.ContextProvider;
import express.field.agent.Request.exception.CryptoException;
import express.field.agent.Request.exception.HttpCallException;
import express.field.agent.Request.net.UtError;
import express.field.agent.Request.net.UtListResponsePrepared;
import express.field.agent.Request.net.UtMapResponsePrepared;
import express.field.agent.Request.net.UtResponseJWE;
import express.field.agent.Request.net.UtResponsePrepared;
import express.field.agent.Request.net.UtResponseSecured;
import express.field.agent.Utils.MleUtils;
import express.field.agent.Utils.ObjectUtils;


public class Crypto {
    private static final String TAG = Crypto.class.getSimpleName();

    private static final String KEY_STORE = "AndroidKeyStore";

    public static final String EC_256 = "EC_256";
    public static final String RSA_2048 = "RSA_2048";
    public static final String AES_256 = "AES_256";
    private static final String RSA_OAEP_256_MLEK = "RSA_OAEP_256_MLEK";
    private static final String EC_P384_MLSK = "EC_P384_MLSK";
    private static final String RSA_SHA256_DOCS = "RSA_SHA256_DOCS"; // Key for exchanging document bundle pdf files

    private static final String BE_KEY_JSON_RPC = "jsonrpc";
    private static final String BE_KEY_ERROR = "error";
    private static final String BE_KEY_RESULT = "result";
    private static final String BE_KEY_PROTECTED = "protected";

    private static final String AES_CIPHER_SPECS = KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_GCM + "/" + KeyProperties.ENCRYPTION_PADDING_NONE;

    private static final String testData = "Some test data! We need more complex data!Some test data!";

    static RSAPublicKey rsaPublicKey;
    static RSAPrivateKey rsaPrivateKey;
    private static boolean isKeystoreAvailable = true;

    public static void listKeyAliases() {
        Enumeration<String> aliases;
        try {
            KeyStore keystore = KeyStore.getInstance(KEY_STORE);
            keystore.load(null);
            aliases = keystore.aliases();

            Log.d(TAG, String.format("%s alias has elements: %s", KEY_STORE, aliases.hasMoreElements()));

            while (aliases.hasMoreElements()) {
                Log.d(TAG, String.format("%s alias: %s", KEY_STORE, aliases.nextElement()));
            }
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            Log.e(TAG, String.format("%s keystore aliases list problem!", KEY_STORE), e);
        }
    }

    public static void generateKey(final String keyAlias) throws CryptoException {
        if (!keyAlias.equals(EC_256) && !keyAlias.equals(RSA_2048)&& !keyAlias.equals(AES_256) && !keyAlias.equals(RSA_OAEP_256_MLEK)
            && !keyAlias.equals(EC_P384_MLSK) && !keyAlias.equals(RSA_SHA256_DOCS)) {
            Log.i(TAG, String.format("%s key alias is not supported!", keyAlias));
            return;
        }

        KeyStore keystore;
        try {
            keystore = KeyStore.getInstance(KEY_STORE);
            keystore.load(null);

            if (!keystore.containsAlias(keyAlias)) {
                switch (keyAlias) {
                    case EC_256: {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(EC_256, KeyProperties.PURPOSE_SIGN);

                            builder.setKeySize(256).setDigests(KeyProperties.DIGEST_SHA256);

                            KeyPairGenerator generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, KEY_STORE);
                            generator.initialize(builder.build());

                            KeyPair keyPair = generator.generateKeyPair();
                            logPublicKey(keyPair, EC_256);
                        } else {
                            KeyPair keyPair = genKeyPair(ContextProvider.getInstance().getContext(), keystore, KEYSTORE_ALIAS);
                            logPublicKey(keyPair, EC_256);
                        }

                        break;
                    }
                    case RSA_2048: {
                        KeyGenParameterSpec.Builder builder = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            builder = new KeyGenParameterSpec.Builder(RSA_2048, KeyProperties.PURPOSE_SIGN);

                            builder.setKeySize(2048).setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1).setDigests(KeyProperties.DIGEST_SHA384);
                            KeyPairGenerator generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEY_STORE);
                            generator.initialize(builder.build());

                            KeyPair keyPair = generator.generateKeyPair();
                            logPublicKey(keyPair, RSA_2048);
                        } else {
                            KeyPair keyPair = genKeyPair(ContextProvider.getInstance().getContext(), keystore, KEYSTORE_ALIAS);
                            logPublicKey(keyPair, RSA_2048);
                        }
                        break;
                    }
                    case AES_256: {
                        KeyGenParameterSpec.Builder builder = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            builder = new KeyGenParameterSpec.Builder(AES_256, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT);

                            builder.setKeySize(256).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE);
                            KeyGenerator generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, KEY_STORE);
                            generator.init(builder.build());
                            Key key = generator.generateKey();
                            Log.d(TAG, String.format("Key generated: %s, %s", AES_256, key.toString()));
                        } else {
                            isKeystoreAvailable = false;
                            Log.d(TAG, String.format("Keystore AES_256 not available for Android Version: %s", Build.VERSION.SDK_INT));

                        }
                        break;
                    }
                    case RSA_OAEP_256_MLEK: {
                        KeyGenParameterSpec.Builder builder = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            builder = new KeyGenParameterSpec.Builder(RSA_OAEP_256_MLEK, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT);

                            builder.setKeySize(2048)
                                .setDigests(KeyProperties.DIGEST_SHA256)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP);
                            KeyPairGenerator generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEY_STORE);
                            generator.initialize(builder.build());

                            KeyPair keyPair = generator.generateKeyPair();
                            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
                            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();

                            MleUtils.getInstance().saveMlekEncrypted(rsaPublicKey.toString());
                            MleUtils.getInstance().savePublicMlek(rsaPublicKey.toString());

                            Log.d(TAG, String.format("rsaPrivateKeyHere:%s", rsaPrivateKey.toString()));
                            Log.d(TAG, String.format("rsaPublicKeyHere:%s", rsaPublicKey));

                            logPublicKey(keyPair, RSA_OAEP_256_MLEK);
                        } else {
                            KeyPair keyPair = genKeyPair(ContextProvider.getInstance().getContext(), keystore, KEYSTORE_ALIAS);

                            rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
                            rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();

                            MleUtils.getInstance().saveMlekEncrypted(rsaPublicKey.toString());
                            MleUtils.getInstance().savePublicMlek(rsaPublicKey.toString());

                            Log.d(TAG, String.format("rsaPrivateKeyElse:%s", rsaPrivateKey.toString()));
                            Log.d(TAG, String.format("rsaPublicKeyElse:%s", rsaPublicKey.toString()));
                            logPublicKey(keyPair, RSA_OAEP_256_MLEK);
                        }
                        break;
                    }
                    case EC_P384_MLSK: {
                        KeyGenParameterSpec.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            builder = new KeyGenParameterSpec.Builder(EC_P384_MLSK, KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY);

                            builder.setAlgorithmParameterSpec(new ECGenParameterSpec("secp384r1"));
                            builder.setDigests(KeyProperties.DIGEST_SHA384);
                            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", KEY_STORE);
                            generator.initialize(builder.build());
                            KeyPair keyPair = generator.generateKeyPair();


                            logPublicKey(keyPair, EC_P384_MLSK);
                        } else {
                            isKeystoreAvailable = false;
                            generateAlternativeMlsk();
                        }
                        break;
                    }
                    case RSA_SHA256_DOCS: {
                        KeyGenParameterSpec.Builder builder = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            builder = new KeyGenParameterSpec.Builder(RSA_SHA256_DOCS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT);

                            builder.setKeySize(2048)
                                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA1);
                            KeyPairGenerator generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEY_STORE);
                            generator.initialize(builder.build());

                            KeyPair keyPair = generator.generateKeyPair();
                            logPublicKey(keyPair, RSA_SHA256_DOCS);
                        } else {
                            KeyPair keyPair = genKeyPair(ContextProvider.getInstance().getContext(), keystore, KEYSTORE_ALIAS);
                            logPublicKey(keyPair, RSA_SHA256_DOCS);
                        }
                        break;
                    }
                }
            } else {
                Log.d(TAG, String.format("%s key alias already exists!", keyAlias));
            }

        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            Log.e(TAG, String.format("%s keystore key generation problem!", KEY_STORE), e);
            throw new CryptoException(String.format("%s keystore key generation problem!", KEY_STORE));
        }
    }


    private static void logPublicKey(KeyPair keyPair, String type) {
        byte[] publicKey = keyPair.getPublic().getEncoded();
        Log.d(TAG, String.format("Key pair generated: %s, Public Key: %s", type, Base64.encodeToString(publicKey, Base64.DEFAULT)));
    }

    public static void testEncryptDecryptAes256Key() {
        testEncryptDecryptAes256Key(testData);
    }

    private static void testEncryptDecryptAes256Key(final String text) {
        KeyStore ks;
        try {
            ks = KeyStore.getInstance(KEY_STORE);
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(AES_256, null);

            if (!(entry instanceof KeyStore.SecretKeyEntry)) {
                Log.w(TAG, "Not an instance of a PrivateKeyEntry");
            } else {
                Log.d(TAG, String.format("Text for encryption: %s", text));

                Cipher cipher = Cipher.getInstance(AES_CIPHER_SPECS);
                cipher.init(Cipher.ENCRYPT_MODE, ((KeyStore.SecretKeyEntry) entry).getSecretKey());

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, cipher);
                cipherOutputStream.write(text.getBytes(StandardCharsets.UTF_8));
                cipherOutputStream.close();
                byte[] encryptedText = outputStream.toByteArray();

                byte[] iv = cipher.getIV();

                // Add Initialization Vector to cipher message
                ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + encryptedText.length);
                byteBuffer.putInt(iv.length);
                byteBuffer.put(iv);
                byteBuffer.put(encryptedText);

                // Purge array data
                Arrays.fill(iv, (byte) 0);
                Arrays.fill(encryptedText, (byte) 0);

                byte[] cipherMessage = byteBuffer.array();

                final String cipherMessageHex = byteArrayToHexString(cipherMessage);

                Log.d(TAG, String.format("Encrypted hex message: %s", cipherMessageHex));

                decryptAes256(cipherMessageHex);
            }
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableEntryException | InvalidKeyException | NoSuchPaddingException | CryptoException e) {
            Log.e(TAG, String.format("Problem with encryption for key alias: %s", AES_256), e);
        }
    }

    private static char[] decryptAes256(final String encryptedHex) throws CryptoException {
        try {
            KeyStore ks = KeyStore.getInstance(KEY_STORE);
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(AES_256, null);

            if (!(entry instanceof KeyStore.SecretKeyEntry)) {
                String mess = "Not an instance of a PrivateKeyEntry";
                Log.e(TAG, mess);
                throw new CryptoException(mess);
            } else {
                Cipher cipher = Cipher.getInstance(AES_CIPHER_SPECS);

                ByteBuffer byteBuffer = ByteBuffer.wrap(hexStringToByteArray(encryptedHex));

                // Check if IV length is compromised.
                int ivLength = byteBuffer.getInt();
                if (ivLength < 12 || ivLength >= 16) { // check input parameter
                    throw new IllegalArgumentException(String.format("%s, Problem with decryption for key alias: %s", TAG, AES_256));
                }

                // Extract encryptedText and initialization vector from cipher message
                byte[] iv = new byte[ivLength];
                byteBuffer.get(iv);
                byte[] cipherText = new byte[byteBuffer.remaining()];
                byteBuffer.get(cipherText);

                cipher.init(Cipher.DECRYPT_MODE, ((KeyStore.SecretKeyEntry) entry).getSecretKey(), new GCMParameterSpec(128, iv));

                CipherInputStream cipherInputStream = new CipherInputStream(new ByteArrayInputStream(cipherText), cipher);

                ArrayList<Byte> values = new ArrayList<>();
                int nextByte;
                while ((nextByte = cipherInputStream.read()) != -1) {
                    values.add((byte) nextByte);
                }

                byte[] text = new byte[values.size()];
                for (int i = 0; i < text.length; i++) {
                    text[i] = values.get(i);
                }

                // Purge array data
                Arrays.fill(iv, (byte) 0);
                Arrays.fill(cipherText, (byte) 0);

                String finalText = new String(text, 0, text.length, StandardCharsets.UTF_8);

                return finalText.toCharArray();
            }
        } catch (KeyStoreException | NoSuchPaddingException | CertificateException | InvalidAlgorithmParameterException | InvalidKeyException | UnrecoverableEntryException | NoSuchAlgorithmException | IOException e) {
            String mess = String.format("Problem with decryption for key alias: %s", AES_256);
            Log.e(TAG, mess, e);
            throw new CryptoException(mess);
        }
    }

    public static String byteArrayToHexString(final byte[] h) {
        Formatter formatter = new Formatter();
        for (byte b : h) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public static byte[] hexStringToByteArray(String s) {
        byte[] bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            bytes[i] = (byte) v;
        }
        return bytes;
    }

    public static String encryptAes256(final char[] text) throws CryptoException {
        KeyStore ks;
        try {
            ks = KeyStore.getInstance(KEY_STORE);
            ks.load(null);
            KeyStore.Entry entry = ks.getEntry(AES_256, null);

            if (!(entry instanceof KeyStore.SecretKeyEntry)) {
                String mess = "Not an instance of a PrivateKeyEntry";
                Log.e(TAG, mess);
                throw new CryptoException(mess);
            }

            Log.d(TAG, String.format("Text for encryption: %s", String.valueOf(text)));

            Cipher cipher = Cipher.getInstance(AES_CIPHER_SPECS);
            cipher.init(Cipher.ENCRYPT_MODE, ((KeyStore.SecretKeyEntry) entry).getSecretKey());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                outputStream, cipher);
            cipherOutputStream.write(String.valueOf(text).getBytes(StandardCharsets.UTF_8));
            cipherOutputStream.close();
            byte[] encryptedText = outputStream.toByteArray();

            byte[] iv = cipher.getIV();

            // Add Initialization Vector to cipher message
            ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + encryptedText.length);
            byteBuffer.putInt(iv.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedText);

            // Purge array data
            Arrays.fill(iv, (byte) 0);
            Arrays.fill(encryptedText, (byte) 0);

            byte[] cipherMessage = byteBuffer.array();

            final String cipherMessageHex = byteArrayToHexString(cipherMessage);

            Log.d(TAG, String.format("Encrypted hex message: %s", cipherMessageHex));


            return cipherMessageHex;
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableEntryException | InvalidKeyException | NoSuchPaddingException e) {
            String mess = String.format("Problem with encryption for key alias: %s", AES_256);
            Log.e(TAG, mess, e);
            throw new CryptoException(mess);
        }
    }

    public static String encryptPwd(final char[] text) throws CryptoException {
        return encryptAes256(text);
    }

    public static char[] decryptPwd(String text) throws CryptoException {
        return decryptAes256(text);
    }


    private static void generateMlek() throws CryptoException {
        // If key is generated under Android Keystore, it can not be used for JWK as there is algorithm incompatibility
//         generateKey(Crypto.RSA_OAEP_256_MLEK);

        if (TextUtils.isEmpty(MleUtils.getInstance().getMlekEncrypted())) {
            RSAKey mlekRsaKey;

            try {
                mlekRsaKey = new RSAKeyGenerator(2048)
                    .keyUse(KeyUse.ENCRYPTION) // indicate the intended use of the key
                    .keyID(UUID.randomUUID().toString()) // give the key a unique ID
                    .algorithm(JWEAlgorithm.RSA_OAEP_256)
                    .generate();

                String mlekStr = mlekRsaKey.toPublicJWK().toJSONString();

                Log.d(TAG, String.format("MLEK RSA KEY: %s", mlekStr));
                MleUtils.getInstance().savePublicMlek(mlekStr);

                if (!isKeystoreAvailable) {
                    MleUtils.getInstance().saveMlekEncrypted(mlekRsaKey.toJSONString());

                } else {
                    String privateEncryptedHex = encryptAes256(mlekRsaKey.toJSONString().toCharArray());
                    MleUtils.getInstance().saveMlekEncrypted(privateEncryptedHex);
                }
            } catch (JOSEException e) {
                throw new CryptoException(e.getMessage());
            }
        } else {
            Log.d(TAG, "MLEK KEY is already generated!");
        }
    }

    public static void generateMlsk() throws CryptoException {
        generateKey(Crypto.EC_P384_MLSK);
    }

    public static void generateAlternativeMlsk() throws CryptoException {
        ECKey jwk;
        // Generate EC key pair in JWK format
        if (TextUtils.isEmpty(MleUtils.getInstance().getPublicMlsk())) {
            try {
                jwk = new ECKeyGenerator(Curve.P_384)
                    .keyUse(KeyUse.SIGNATURE) // indicates the intended use of the key
                    .keyID(UUID.randomUUID().toString()) // give the key a unique ID
                    .generate();

                MleUtils.getInstance().saveECKey(ObjectUtils.toJsonString(jwk.toJSONObject()));

                Log.d(TAG, String.format("MLSK EC KEY: %s", ObjectUtils.toJsonString(jwk.toJSONObject())));

                String mlskPublicKeyStr = jwk.toPublicJWK().toJSONString();

                Log.d(TAG, String.format("MLSK PUBLIC EC KEY: %s", mlskPublicKeyStr));

                MleUtils.getInstance().savePublicMlsk(mlskPublicKeyStr);

            } catch (JOSEException e) {
                throw new CryptoException(e.getMessage());
            }
        } else {
            Log.d(TAG, "MLSK KEY is already generated!");
        }

    }


    private static ECKey parseString(String keyString) {
        try {
            return ECKey.parse(keyString);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "Invalid ECKey String");
            return null;
        }
    }


    public static PrivateKey getMlskPrivateKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE);
            keyStore.load(null);
            KeyStore.Entry entry = keyStore.getEntry(EC_P384_MLSK, null);

            return ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
        } catch (IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PrivateKey getMlskPrivateKeySharedPreference() {
        final String jws = MleUtils.getInstance().getECKey();
        ObjectUtils.fromJsonString(MleUtils.getInstance().getECKey(), UtResponsePrepared.class);

        try {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE);
            keyStore.load(null);
            KeyStore.Entry entry = keyStore.getEntry(EC_P384_MLSK, null);

            return ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
        } catch (IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ECKey getMlskJwk() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE);
            keyStore.load(null);

            PublicKey publicKey = keyStore.getCertificate(EC_P384_MLSK).getPublicKey();
            ECKey jwk = new ECKey.Builder(Curve.P_384, (ECPublicKey) publicKey).build();

            return jwk;
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void generateKeys() {
        try {
            generateKey(AES_256);
            Crypto.generateMlek();
            Crypto.generateMlsk();
        } catch (CryptoException e) {
            Log.e("Err Key Generation!", String.valueOf(e));
        }
    }

    public static String preparePayload(@NonNull final String jsonPayload) throws CryptoException {
        JWSObject jwsObject = null;
        final Map<String, Object> mlsk;

        Log.d(TAG, String.format("method preparePayload, JSON Payload: %s", jsonPayload));

        try {
            jwsObject = getJws(jsonPayload);


            if (!isKeystoreAvailable) {
                mlsk = JSONObjectUtils.parse(MleUtils.getInstance().getPublicMlsk());
            } else {
                mlsk = getMlskJwk().toJSONObject();
            }

            JWEObject jweObject = new JWEObject(
                new JWEHeader.Builder(JWEAlgorithm.ECDH_ES_A256KW, EncryptionMethod.A256CBC_HS512)
                    .contentType("JWS") // required to indicate nested JWT
                    .customParam("mlek", JSONObjectUtils.parse(MleUtils.getInstance().getPublicMlek()))
                    .customParam("mlsk", mlsk)
                    .build(),
                new Payload(jwsObject));

            String disMlek = MleUtils.getInstance().getKeyDisMlekStr();

            JWK disMlekJwk = JWK.parse(disMlek);
            ECKey ecKey = ECKey.parse(disMlekJwk.toJSONString());
            jweObject.encrypt(new ECDHEncrypter(ecKey.toPublicJWK()));

            // Serialise to JWE compact form
            String jweString = jweObject.serialize();
            Log.d(TAG, jweString);

            return jweString;
        } catch (JOSEException | ParseException e) {
            Log.e(TAG, "Error preparing payload json", e);
            throw new CryptoException("Error in signing/encrypting payload!");
        }
    }

    private static JWSObject getJws(@NonNull final String jsonPayload) throws JOSEException {
        JWSSigner signer;
        if (!isKeystoreAvailable) {
            final ECKey ecKey = parseString(MleUtils.getInstance().getECKey());
            signer = new ECDSASigner(ecKey.toECPrivateKey(), Curve.P_384);

        } else {
            signer = new ECDSASigner(Crypto.getMlskPrivateKey(), Curve.P_384);

        }

        JWSObject jwsObject = new JWSObject(
            new JWSHeader.Builder(JWSAlgorithm.ES384).build(),
            new Payload(jsonPayload));
        jwsObject.sign(signer);

        return jwsObject;
    }


    public static UtResponsePrepared parseResponse(final byte[] response) throws HttpCallException {
        Map<String, Object> resMap = ObjectUtils.jsonToMap(response);

        // This is the common case - JWE, JSON RPC
        if (isJsonRpcJweResponse(resMap)) {
            try {
                UtResponseSecured responseObjectJWE = ObjectUtils.jsonToObject(response, UtResponseSecured.class);
                EncryptedJWT encryptedJWT = getEncryptedJWT(responseObjectJWE);

                encryptedJWT.decrypt(new RSADecrypter(getMlekPrivateKey()));
                SignedJWT signedJWT = encryptedJWT.getPayload().toSignedJWT();

                JWK disMlskJwk = JWK.parse(MleUtils.getInstance().getKeyDisMlskStr());
                ECKey ecKey = ECKey.parse(disMlskJwk.toJSONString());
                JWSVerifier verifier = new ECDSAVerifier(ecKey);

                if (signedJWT.verify(verifier)) {
                    Log.d(TAG, "Signature Verified");
                    String payloadStr = String.valueOf(signedJWT.getPayload().toJSONObject());
                    Log.d(TAG, payloadStr);

                    if (responseObjectJWE.hasErrors()) {
                        UtError utError = ObjectUtils.fromJsonString(payloadStr, UtError.class);
                        return new UtResponsePrepared(utError);
                    } else {
                        if (payloadStr.length() == 0 || Objects.equals(payloadStr, "null") || payloadStr.trim().isEmpty()) {
                            final byte[] payloadBytes = signedJWT.getPayload().toBytes();
                            final List<Map<String, Object>> payloadRes = ObjectUtils.jsonBytesToList(payloadBytes);


                            return new UtResponsePrepared(payloadRes);
                        } else {
                            final HashMap<String, Object> stringObjectHashMap = ObjectUtils.objectToMap(signedJWT.getPayload().toJSONObject());
                            System.out.println(stringObjectHashMap);

                            return new UtResponsePrepared(ObjectUtils.objectToMap(signedJWT.getPayload().toJSONObject()));
                        }

                    }
                } else {
                    Log.e(TAG, "NOT Verified");
                    throw new HttpCallException("Response signature verification failed!");
                }
            } catch (ParseException | JOSEException | CryptoException e) {
                Log.e(TAG, "Parsing of response failed", e);
                throw new HttpCallException("Parsing of response failed");
            }
        } else if (resMap.get(BE_KEY_PROTECTED) != null) {
            // This handling is due to BUG in the backend. Should be revised whenever it is resolved.
            try {
                UtResponseJWE responseObjectJWE = ObjectUtils.jsonToObject(response, UtResponseJWE.class);
                EncryptedJWT encryptedJWT = getEncryptedJWT(responseObjectJWE);

                encryptedJWT.decrypt(new RSADecrypter(getMlekPrivateKey()));
                SignedJWT signedJWT = encryptedJWT.getPayload().toSignedJWT();

                JWK disMlskJwk = JWK.parse(MleUtils.getInstance().getKeyDisMlskStr());
                ECKey ecKey = ECKey.parse(disMlskJwk.toJSONString());
                JWSVerifier verifier = new ECDSAVerifier(ecKey);

                if (signedJWT.verify(verifier)) {
                    Log.d(TAG, "Signature Verified");
                    String payloadStr = String.valueOf(signedJWT.getPayload().toJSONObject());
                    Log.d(TAG, payloadStr);
                    UtResponsePrepared responsePrepared = ObjectUtils.fromJsonString(payloadStr, UtResponsePrepared.class);
                    return responsePrepared;
                } else {
                    Log.e(TAG, "NOT Verified");
                    throw new HttpCallException("Response signature verification failed!");
                }
            } catch (ParseException | JOSEException | CryptoException e) {
                Log.e(TAG, "Parsing of response failed", e);
                throw new HttpCallException("Parsing of response failed");
            }
        } else {
            // Handling non-rpc, not encrypted response. Could be List(result[]) or Map(result{})
            if (resMap.get(BE_KEY_RESULT) != null || resMap.get(BE_KEY_ERROR) != null) {
                Log.d(TAG, ObjectUtils.toJsonString(resMap));
                if (resMap.get(BE_KEY_RESULT) instanceof ArrayList) {
                    return ObjectUtils.mapToObject(resMap, UtListResponsePrepared.class);
                }
                return ObjectUtils.mapToObject(resMap, UtMapResponsePrepared.class);
            } else {
                return new UtResponsePrepared<>(resMap);
            }
        }
    }


    private static boolean isJsonRpcJweResponse(Map<String, Object> resMap) {
        return resMap.get(BE_KEY_JSON_RPC) != null && ((resMap.get(BE_KEY_RESULT) != null && ((Map<String, Object>) resMap.get(BE_KEY_RESULT)).get(BE_KEY_PROTECTED) != null) || (resMap.get(BE_KEY_ERROR) != null && ((Map<String, Object>) resMap.get(BE_KEY_ERROR)).get(BE_KEY_PROTECTED) != null));
    }

    private static EncryptedJWT getEncryptedJWT(final UtResponseSecured utResponseSecured) throws ParseException {
        UtResponseJWE jwe;

        if (utResponseSecured.hasErrors()) {
            Log.d(TAG, "Response contains error!");
            jwe = utResponseSecured.getError();
        } else {
            jwe = utResponseSecured.getResult();
        }

        String header = jwe.getProtectedField();
        String iv = jwe.getIv();
        String ciphertext = jwe.getCipherText();
        String tag = jwe.getTag();

        List<UtResponseJWE.Recipient> recipients = jwe.getRecipients();
        String encryptedKey = recipients.get(0).getEncryptedKey();

        return new EncryptedJWT(new Base64URL(header), new Base64URL(encryptedKey), new Base64URL(iv), new Base64URL(ciphertext), new Base64URL(tag));
    }

    private static EncryptedJWT getEncryptedJWT(final UtResponseJWE jwe) throws ParseException {
        String header = jwe.getProtectedField();
        String iv = jwe.getIv();
        String ciphertext = jwe.getCipherText();
        String tag = jwe.getTag();

        List<UtResponseJWE.Recipient> recipients = jwe.getRecipients();
        String encryptedKey = recipients.get(0).getEncryptedKey();

        return new EncryptedJWT(new Base64URL(header), new Base64URL(encryptedKey), new Base64URL(iv), new Base64URL(ciphertext), new Base64URL(tag));
    }


    private static PrivateKey getMlekPrivateKey() throws CryptoException {
        String enc = MleUtils.getInstance().getMlekEncrypted();
        try {
            RSAKey rsaKey;
            if (!isKeystoreAvailable) {
                rsaKey = RSAKey.parse(enc);
            } else {
                rsaKey = RSAKey.parse(String.valueOf(decryptAes256(enc)));

            }
            return rsaKey.toRSAPrivateKey();


        } catch (ParseException | JOSEException e) {
            String mess = "Can not get mlek key!";
            Log.e(TAG, mess);
            throw new CryptoException(mess);
        }
    }

    public static void generateBundleDocSignKeyPair() throws CryptoException {
        generateKey(RSA_SHA256_DOCS);
    }

    public static String getBundleDocSignPublicKey() throws CryptoException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE);
            keyStore.load(null);
            KeyStore.Entry entry = keyStore.getEntry(RSA_SHA256_DOCS, null);
            PublicKey publicKey = keyStore.getCertificate(RSA_SHA256_DOCS).getPublicKey();
            byte[] bytes = publicKey.getEncoded();

            String docSignKeyStr = new String(Base64.encode(bytes, Base64.NO_WRAP));
            Log.d(TAG, String.format("Bundle doc signature public key: %s", docSignKeyStr));

            return String.format("-----BEGIN PUBLIC KEY-----\n%s\n-----END PUBLIC KEY-----", docSignKeyStr);
        } catch (IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e) {
            Log.e(TAG, e.getMessage());
            throw new CryptoException(e.getMessage());
        }
    }

    private static PrivateKey getBundleDocSignPrivateKey() throws CryptoException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE);
            keyStore.load(null);
            KeyStore.Entry entry = keyStore.getEntry(RSA_SHA256_DOCS, null);
            return ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
        } catch (IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException e) {
            Log.e(TAG, e.getMessage());
            throw new CryptoException(e.getMessage());
        }
    }

    public static MasterKey getMainKey(Context context) throws GeneralSecurityException, IOException {
        return new MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build();
    }
}
