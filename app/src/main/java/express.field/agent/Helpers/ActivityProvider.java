package express.field.agent.Helpers;

import android.app.Activity;

public class ActivityProvider {

    private Activity mActivity;
    private static ActivityProvider mInstance = null;

    private ActivityProvider() {
    }

    public static ActivityProvider getInstance() {
        if(mInstance == null) {
            synchronized (ActivityProvider.class) {
                mInstance = new ActivityProvider();
            }
        }

        return mInstance;
    }

    public Activity getCurrentActivity() {
        return mActivity;
    }

    public void registerCurrentActivity(Activity activity) {
        mActivity = activity;
    }

}
