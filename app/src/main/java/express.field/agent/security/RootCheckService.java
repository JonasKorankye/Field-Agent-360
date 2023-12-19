package express.field.agent.security;

import android.app.Activity;

import com.scottyab.rootbeer.RootBeer;

import express.field.agent.Helpers.ContextProvider;
import express.field.agent.Helpers.ResourceProvider;
import express.field.agent.Utils.FunUtils;

/**
 * Root checking service bases on the RootBeer library by Scott(scottyab) and Matthew(stealthcopter)
 * Used checks are as follows
 * Java checks :
 *  checkRootManagementApps
 *  checkPotentiallyDangerousApps
 *  checkRootCloakingApps
 *  checkTestKeys
 *  checkForDangerousProps
 *  checkForBusyBoxBinary
 *  checkForSuBinary
 *  checkSuExists
 *  checkForRWSystem
 * Native checks :
 *  checkForSuBinary
 * For more information - https://github.com/scottyab/rootbeer
 */
public class RootCheckService {

    private static RootCheckService mInstance;

    private Activity mActivity;
    private RootBeer mRootBeer;


    private RootCheckService(Activity activity) {
        this.mActivity = activity;
        mRootBeer = new RootBeer(mActivity);
    }

    public static RootCheckService getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new RootCheckService(activity);
        }

        return mInstance;
    }

    /**
     * Runs check if the device is rooted. Notifies the user and exits the application if the checks passes
     */
    public boolean isRooted() {
        boolean isRooted = isRootedButCheckForDangerousPropsExempt();
        if (isRooted) {
            FunUtils.showMessage(ContextProvider.getInstance().getContext(), ResourceProvider.getString("security_rooted_device"));

        }
        return isRooted;
    }

    public boolean isRootedButCheckForDangerousPropsExempt() {

        return mRootBeer.detectRootManagementApps() || mRootBeer.detectPotentiallyDangerousApps() || mRootBeer.checkForBinary("su")
            || mRootBeer.checkForBinary("busybox") || mRootBeer.checkForRWPaths()
            || mRootBeer.detectTestKeys() || mRootBeer.checkSuExists() || mRootBeer.checkForRootNative() || mRootBeer.checkForMagiskBinary();
    }


}
