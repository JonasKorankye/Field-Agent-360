package express.field.agent.Pref;

import static express.field.agent.Utils.Constants.PREF_NAME;

import android.content.Context;
import android.content.SharedPreferences;


import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;

import express.field.agent.Helpers.ContextProvider;

public class GlobalPref {

    private static GlobalPref mInstance;

    public static final String AGENT_ID = "express.field.agent.agent.Pref.AGENT_ID";
    public static final String AGENT_NAME = "express.field.agent.agent.Pref.AGENT_NAME";
    public static final String AGENT_PASSWORD = "express.field.agent.agent.Pref.AGENT_PASSWORD";
    public static final String HAS_LOGIN = "express.field.agent.agent.Pref.HAS_LOGIN";
    public static final String CURRENT_BALANCE = "express.field.agent.agent.Pref.CURRENT_BALANCE";
    public static final String AGENT_LAT = "express.field.agent.agent.Pref.AGENT_LAT";
    public static final String AGENT_LNG = "express.field.agent.agent.Pref.AGENT_LNG";


    private GlobalPref() {
    }

    public static GlobalPref getInstance() {
        if (mInstance == null) {
            synchronized (GlobalPref.class) {
                mInstance = new GlobalPref();
            }
        }

        return mInstance;
    }

    private Context getContext() {
        return ContextProvider.getInstance().getContext();
    }



    private MasterKey createMasterKey() {
        MasterKey masterKeyAlias = null;
        try {
            masterKeyAlias = new MasterKey.Builder(getContext())
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        return masterKeyAlias;
    }

    SharedPreferences sharedPreferences;
    {
        try {
            sharedPreferences = EncryptedSharedPreferences.create(
                getContext(),
                PREF_NAME,
                createMasterKey(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    SharedPreferences.Editor editor = sharedPreferences.edit();


    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    public void put(String key, String value) {
        editor.putString(key, value).apply();
    }

    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void put(String key, int value) {
        editor.putInt(key, value).apply();
    }

    public long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    public void put(String key, long value) {
        editor.putLong(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public void put(String key, boolean value) {
        editor.putBoolean(key, value).apply();
    }

    public void putStringSet(String key, HashSet<String> set) {
        editor.putStringSet(key, set).apply();
    }

    public Set<String> getStringSet(String key, HashSet<String> defaultValue) {
        return sharedPreferences.getStringSet(key, defaultValue);
    }


    /**
     * Remove all shared preferences
     */
    public final synchronized boolean clear() {
        return editor.clear().commit();
    }
}
