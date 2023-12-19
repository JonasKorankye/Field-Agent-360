package express.field.agent.security;

import static android.content.Context.ACTIVITY_SERVICE;


import static express.field.agent.AgentApplication.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.scottyab.rootbeer.Const;

import java.io.File;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

import express.field.agent.Helpers.ContextProvider;
import express.field.agent.Helpers.ResourceProvider;
import express.field.agent.Utils.Constants;
import express.field.agent.Utils.FunUtils;

public class TamperCheckService {

    private static TamperCheckService mInstance;
    private Activity mActivity;

    private static final int SIGNATURE_VALID = 0;
    private static final int SIGNATURE_INVALID = 1;


    private TamperCheckService(Activity activity) {
        this.mActivity = activity;
    }

    public static TamperCheckService getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new TamperCheckService(activity);
        }

        return mInstance;
    }

    /**
     * Runs check if the device is tampered. Notifies the user and exits the application if the checks passes
     */
    public void isAppTampered() {
        boolean tampered = isProbablyTampered();
        if (tampered) {
            FunUtils.showMessage(ContextProvider.getInstance().getContext(), ResourceProvider.getString("security_tamper_dialog_message"));

        }
    }

    private boolean isProbablyTampered() {
        return checkDebuggable(mActivity.getApplicationContext()) ||
            checkAppSignature(mActivity.getApplicationContext()) == SIGNATURE_INVALID ||
            searchForMagisk() ||
            hasFridaRunningProcesses() ||
            isPackageInstalled("com.thirdparty.superuser") ||
            isPackageInstalled("eu.chainfire.supersu") ||
            isPackageInstalled("com.noshufou.android.su") ||
            isPackageInstalled("com.koushikdutta.superuser") ||
            isPackageInstalled("com.zachspong.temprootremovejb") ||
            isPackageInstalled("com.ramdroid.appquarantine") ||
            isPackageInstalled("com.topjohnwu.magisk") ||
            isTestKeyBuild();
    }

    public static int checkAppSignature(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final byte[] currentSignature = Base64.encode(md.digest(), Base64.NO_WRAP);
                Log.e(TAG, "currentSignature: " + Arrays.toString(currentSignature));



                if (Arrays.equals(Constants.APP_SIGNATURE, currentSignature)) {
                    return SIGNATURE_VALID;
                }
            }

        } catch (Exception e) {
            Log.e("signature check", "signature calculation failed", e);
            return SIGNATURE_INVALID;
        }
        return SIGNATURE_INVALID;
    }

    public static boolean checkDebuggable(Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    private boolean searchForMagisk() {
        PackageManager pm = mActivity.getPackageManager();
        @SuppressLint("QueryPermissionsNeeded") List<PackageInfo> installedPackages = pm.getInstalledPackages(0);

        for (int i = 0; i < installedPackages.size(); i++) {
            PackageInfo info = installedPackages.get(i);
            ApplicationInfo appInfo = info.applicationInfo;

            String nativeLibraryDir = appInfo.nativeLibraryDir;
            String packageName = appInfo.packageName;

            Log.i("Magisk Detection", "Checking App: " + nativeLibraryDir);

            File f = new File(nativeLibraryDir + "/libstub.so");
            if (f.exists()) {
                return true;
            }
        }

        return false;
    }

    public boolean hasFridaRunningProcesses() {
        boolean returnValue = false;
        ActivityManager manager = (ActivityManager)
            mActivity.getSystemService(ACTIVITY_SERVICE);

        // Get currently running application processes
        List<ActivityManager.RunningServiceInfo> list = manager.getRunningServices(300);

        if (list != null) {
            String tempName;
            for (int i = 0; i < list.size(); ++i) {
                tempName = list.get(i).process;

                if (tempName.contains("fridaserver") || tempName.contains("supersu") || tempName.contains("superuser")) {
                    returnValue = true;
                }
            }
        }
        return returnValue;
    }

    private boolean isPackageInstalled(String packageName) {
        try {
            mActivity.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private boolean isTestKeyBuild() {
        String str = Build.TAGS;
        return ((str != null) && (str.contains("test-keys")));
    }

}

