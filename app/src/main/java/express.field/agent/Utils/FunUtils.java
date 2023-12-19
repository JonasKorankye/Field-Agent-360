package express.field.agent.Utils;

import static express.field.agent.Helpers.ResourceProvider.getString;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import android.content.Intent;
import android.view.Gravity;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import express.field.agent.Helpers.ContextProvider;
import express.field.agent.R;

/**
 * Created by jonas korankye  on 17/10/23.
 */
public class FunUtils {

    public static void loadPage(FragmentManager fm, Fragment fragment, boolean shouldStack) {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.layout_frame, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public static void previousPage(FragmentManager fm){
        fm.popBackStack();
    }


    private static String VALID_EMAIL_ADDRESS_REGEX = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";

    public static boolean isValidEmail(String email) {
        return email.matches(VALID_EMAIL_ADDRESS_REGEX);
    }

    public static void showMessage(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }



    public static boolean isApplicationBroughtToBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static void openActivity(Activity activity, Class<?> targetCls) {
        Intent intent = new Intent(activity, targetCls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();

    }



}
