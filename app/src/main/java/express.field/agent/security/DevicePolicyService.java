package express.field.agent.security;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


import express.field.agent.Helpers.ContextProvider;
import express.field.agent.R;
import express.field.agent.Utils.FunUtils;

/**
 * Class to enforce Device Administrator policy for app, set password complexity
 * and encrypt device to secure local data.
 * App should not run unless all required steps are completed
 */
public class DevicePolicyService {

    private static DevicePolicyService mInstance;
    private Activity mActivity;
    private ComponentName mDeviceAdminComponent;
    private DevicePolicyManager mDevicePolicyManager;


    private DevicePolicyService(Activity activity) {
        this.mActivity = activity;
        mDeviceAdminComponent = new ComponentName(mActivity, DeviceAdminReceiver.class);
        mDevicePolicyManager = (DevicePolicyManager) mActivity.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    public static DevicePolicyService getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new DevicePolicyService(activity);
        }

        return mInstance;
    }

    /**
     * First requirement is Device Administrator in order to enforce password complexity.
     * Second requirement is to set a lock password with highest complexity level :
     * min 6 length, 1 upper case, 1 lower case, 1 symbol and 1 special char
     * Third requirement is tablet encryption
     */
    public boolean isDeviceEncrypted() {
        if (isDeviceAdmin()) {
            setAdminPolicy();
            if (Build.VERSION.SDK_INT < 29 && mDevicePolicyManager.isActivePasswordSufficient()) {
                if (mDevicePolicyManager.getStorageEncryptionStatus() == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE ||
                    mDevicePolicyManager.getStorageEncryptionStatus() == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE_DEFAULT_KEY ||
                    mDevicePolicyManager.getStorageEncryptionStatus() == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE_PER_USER) {
                    return true;
                } else if (mDevicePolicyManager.getStorageEncryptionStatus() == DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED) {
                    handleEncryptionNotSupported();
                    return true;
                } else {
                    requestDeviceEncryption();
                    return true;
                }
            } else {
                // Setting password not supported anymore if > 28 API level
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    return true;
                } else {
                    requestDevicePassword();
                }

                return true;
            }
        } else {
            requestDeviceAdmin();
        }

        return false;
    }

    private boolean isDeviceAdmin() {
        return mDevicePolicyManager.isAdminActive(mDeviceAdminComponent);
    }

    private void requestDevicePassword() {
        Intent i = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
        mActivity.startActivity(i);
    }

    private void requestDeviceEncryption() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_START_ENCRYPTION);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminComponent);
        mActivity.startActivity(intent);
    }

    private void requestDeviceAdmin() {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminComponent);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, mActivity.getResources().getString(R.string.device_admin_description));

        mActivity.startActivity(intent);
    }

    private void setAdminPolicy() {
        // Throws security exception when invoked on Android 10 - https://developers.google.com/android/work/device-admin-deprecation
        if (Build.VERSION.SDK_INT < 29) {
            mDevicePolicyManager.setPasswordQuality(mDeviceAdminComponent, DevicePolicyManager.PASSWORD_QUALITY_COMPLEX);
        }

        // Will be deprecated as well with Android 11
        if (Build.VERSION.SDK_INT < 30) {
            mDevicePolicyManager.setStorageEncryption(mDeviceAdminComponent, true);
        }
    }

    /**
     * If the device does not support encryption the user is notified and the app finishes
     */
    private void handleEncryptionNotSupported() {
       FunUtils.showMessage(ContextProvider.getInstance().getContext(), "unsupporrted");
    }

}
