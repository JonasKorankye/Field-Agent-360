package express.field.agent.security;

import android.app.Activity;
import android.os.Build;

import express.field.agent.Helpers.ContextProvider;
import express.field.agent.Helpers.ResourceProvider;
import express.field.agent.Utils.FunUtils;


public class EmulatorCheckService {

    private static EmulatorCheckService mInstance;

    private Activity mActivity;


    private EmulatorCheckService(Activity activity) {
        this.mActivity = activity;
    }

    public static EmulatorCheckService getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new EmulatorCheckService(activity);
        }

        return mInstance;
    }

    /**
     * Runs check if the device is an emulator. Notifies the user and exits the application if the checks passes
     */
    public boolean isEmulator() {
        boolean isEmulator = isProbablyEmulator();
        if (isEmulator) {
            FunUtils.showMessage(ContextProvider.getInstance().getContext(), ResourceProvider.getString("security_emulator_dialog_message") );

        }
        return isEmulator;
    }

    private boolean isProbablyEmulator() {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
            || Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.HARDWARE.contains("goldfish")
            || Build.HARDWARE.contains("ranchu")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.PRODUCT.contains("sdk_google")
            || Build.PRODUCT.contains("google_sdk")
            || Build.PRODUCT.contains("sdk")
            || Build.PRODUCT.contains("sdk_x86")
            || Build.PRODUCT.contains("sdk_gphone64_arm64")
            || Build.PRODUCT.contains("vbox86p")
            || Build.PRODUCT.contains("emulator")
            || Build.PRODUCT.contains("simulator")
            || "QC_Reference_Phone".equals(Build.BOARD) && !"Xiaomi".equalsIgnoreCase(
            Build.MANUFACTURER)
            || ((Build.FINGERPRINT.startsWith("google/sdk_gphone_")
            && Build.FINGERPRINT.endsWith(":user/release-keys")
            && Build.MANUFACTURER.equals("Google") && Build.PRODUCT.startsWith("sdk_gphone_") && Build.BRAND.equals("google")
            && Build.MODEL.startsWith("sdk_gphone_")));
    }
}