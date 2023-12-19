package express.field.agent;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import express.field.agent.Request.exception.NetworkException;
import express.field.agent.Request.net.BaseCall;
import express.field.agent.Request.net.NetworkCall;
import express.field.agent.Utils.FunUtils;

public class SplashScreenActivity extends AppCompatActivity implements NetworkCall.OnNetworkCallResultListener {

    private ImageView mSplashLogo;
    private NetworkCall mNetworkCall = NetworkCall.getInstance();


    @Override
    protected void onResume() {
        mNetworkCall.registerListener(this);
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mSplashLogo = findViewById(R.id.splash_logo);

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{ContextCompat.getColor(this, R.color.splash_color_1),
                        ContextCompat.getColor(this, R.color.splash_color_2),
                        ContextCompat.getColor(this, R.color.splash_color_3),
                        ContextCompat.getColor(this, R.color.splash_color_4)});

        if(findViewById(R.id.splash_screen) != null)
            findViewById(R.id.splash_screen).setBackground(gradientDrawable);

        splashScreenActions();
        FunUtils.openActivity(this, Login.class);
    }

    private void splashScreenActions() {
        //Load animation of splash screen logo
        Animation splashLogoAnim = AnimationUtils.loadAnimation(this, R.anim.splash_logo_animation);
        mSplashLogo.startAnimation(splashLogoAnim);
    }



    @Override
    public void onNetworkCallResult(BaseCall call) throws GeneralSecurityException, IOException {
        Map<String, Object> result = (Map<String, Object>) call.getRequestResult();
        if (result.get("result") != null) {
            result = (Map<String, Object>) result.get("result");
        }


    }




    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    public void onNetworkCallException(NetworkException e) {

    }

    @Override
    public String getNetworkCallResultDeliveryTag() {
        return getClass().getName();
    }

    @Override
    protected void onDestroy() {
        mNetworkCall.unregisterListener(this);
        super.onDestroy();
    }


}
