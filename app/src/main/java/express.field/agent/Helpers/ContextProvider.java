package express.field.agent.Helpers;

import android.content.Context;

public class ContextProvider {

    private Context mContext;
    private static ContextProvider mInstance = null;

    private ContextProvider(Context context) {
        mContext = context;
    }

    public static void initContextService(final Context context) {
        if(mInstance == null) {
            synchronized (ContextProvider.class) {
                mInstance = new ContextProvider(context);
            }
        }
    }

    public static ContextProvider getInstance() {
        return mInstance;
    }

    public Context getContext() {
        return mContext;
    }
}
