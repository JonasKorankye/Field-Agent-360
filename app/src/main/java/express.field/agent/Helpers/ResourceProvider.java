package express.field.agent.Helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import express.field.agent.Utils.Constants;

/**
 * Util class used to inject resources when usual access to Android resources is not easily achieved
 */
public class ResourceProvider {

    private static Context mContext;

    private static void refreshContext() {
        mContext = ContextProvider.getInstance().getContext();
    }

    public static String getString(@NonNull String resName) {
        refreshContext();
        Resources res = mContext.getResources();
        int id = res.getIdentifier(resName, Constants.STRING_RESOURCE, Constants.PACKAGE_NAME);
        return id != 0 ? res.getString(id) : resName;
    }

    public static Drawable getDrawable(@NonNull String resName) {
        refreshContext();
        Resources res = mContext.getResources();
        int id = res.getIdentifier(resName, Constants.DRAWABLE_RESOURCE, Constants.PACKAGE_NAME);
        return res.getDrawable(id);
    }

    public static Drawable getColorDrawable(@NonNull String resName) {
        refreshContext();
        Resources res = mContext.getResources();
        int id = res.getIdentifier(resName, Constants.COLOR_RESOURCE, Constants.PACKAGE_NAME);
        return res.getDrawable(id);
    }

    @ColorInt
    public static int getColorInt(@NonNull String resName) {
        refreshContext();
        Resources res = mContext.getResources();
        return res.getColor(res.getIdentifier(resName, Constants.COLOR_RESOURCE, Constants.PACKAGE_NAME));
    }

    @DrawableRes
    public static int getImageResource(@NonNull String resName) {
        refreshContext();
        Resources res = mContext.getResources();
        return res.getIdentifier(resName, Constants.DRAWABLE_RESOURCE, Constants.PACKAGE_NAME);
    }

    public static int getStyle(@NonNull String styleName) {
        refreshContext();
        Resources res = mContext.getResources();
        return res.getIdentifier(styleName, Constants.STYLE_RESOURCE, Constants.PACKAGE_NAME);
    }

    public static int getStringResource(@NonNull String resName) {
        refreshContext();
        Resources res = mContext.getResources();
        return res.getIdentifier(resName, Constants.STRING_RESOURCE, Constants.PACKAGE_NAME);
    }

}
