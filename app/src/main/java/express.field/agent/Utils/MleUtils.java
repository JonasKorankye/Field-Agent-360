package express.field.agent.Utils;

import android.content.Context;
import android.util.Base64;

import java.nio.charset.StandardCharsets;

import express.field.agent.Pref.GlobalPref;


public class MleUtils {
    private static final String TAG = MleUtils.class.getSimpleName();

    private static final String KEY_DIS_MLSK = "disMlsk";
    private static final String KEY_DIS_MLEK = "disMlek";
    private static final String KEY_MLEK_ENCRYPTED = "mlek_encrypted";
    private static final String KEY_PUBLIC_MLEK = "public_mlek";
    private static final String KEY_PUBLIC_MLSK = "public_mlsk";
    private static final String KEY_PRIVATE_MLSK = "private_mlsk";
    private static final String KEY_EC_JWK = "ec_jwk";
    private static final String KEY_SSASC_ID = "ssascId";
    private static final String KEY_SCK_NO_BIO = "sckNoBio";

    private static GlobalPref mGlobalPref;



    private static MleUtils mInstance;
    private Context mContext;

    public static MleUtils getInstance() {
        if(mInstance == null) {
            mGlobalPref = GlobalPref.getInstance();
            mInstance = new MleUtils();
        }

        return mInstance;
    }

    public void setContext(Context context) {
        mContext = context;

    }

    public void saveDisMlek(String mlek) {
        mGlobalPref.getInstance().put(KEY_DIS_MLEK, mlek);
    }

    public void saveDisMlsk(String mlsk) {
        mGlobalPref.getInstance().put(KEY_DIS_MLSK, mlsk);
    }

    public String getKeyDisMlekStr() {
        return getStringForKey(KEY_DIS_MLEK);
    }

    public String getKeyDisMlskStr() {
        return getStringForKey(KEY_DIS_MLSK);
    }

    public void saveMlekEncrypted(String mlek) {
        mGlobalPref.getInstance().put(KEY_MLEK_ENCRYPTED, mlek);
    }

    public String getMlekEncrypted() {
        return getStringForKey(KEY_MLEK_ENCRYPTED);
    }

    public void savePublicMlek(String publicMlek) {
        String str = Base64.encodeToString(publicMlek.getBytes(), Base64.NO_WRAP);
        mGlobalPref.getInstance().put(KEY_PUBLIC_MLEK, str);
    }

    public String getPublicMlek() {
        String base64 = getStringForKey(KEY_PUBLIC_MLEK);
        byte[] bytes = Base64.decode(base64, Base64.NO_WRAP);
        return new String(bytes, 0, bytes.length, StandardCharsets.UTF_8);
    }

    public void savePublicMlsk(String publicMlsk) {
        String str = Base64.encodeToString(publicMlsk.getBytes(), Base64.NO_WRAP);
        mGlobalPref.getInstance().put(KEY_PUBLIC_MLSK, str);
    }

    public String getPublicMlsk() {
        String base64 = getStringForKey(KEY_PUBLIC_MLSK);
        byte[] bytes = Base64.decode(base64, Base64.NO_WRAP);
        return new String(bytes, 0, bytes.length, StandardCharsets.UTF_8);
    }

    public void saveECKey(String ECKey) {
        mGlobalPref.getInstance().put(KEY_EC_JWK, ECKey);
    }

    public String getECKey() {
        return getStringForKey(KEY_EC_JWK);
    }

    public void saveSsascId(String ssascId) {
        mGlobalPref.getInstance().put(KEY_SSASC_ID, ssascId);
    }

    public String getSsascId() {
        return getStringForKey(KEY_SSASC_ID);
    }

    public void setSckNoBio() {
        mGlobalPref.getInstance().put(KEY_SCK_NO_BIO, "no_bio");
    }

    public String getSckNoBio() {
        return getStringForKey(KEY_SCK_NO_BIO);
    }

    private String getStringForKey(String key) {
      return  mGlobalPref.getInstance().getString(key, "");

    }


}
