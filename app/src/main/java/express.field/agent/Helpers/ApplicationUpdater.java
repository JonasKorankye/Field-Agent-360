package express.field.agent.Helpers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Build;
import android.view.View;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import express.field.agent.Request.RetrofitManager;
import express.field.agent.Request.exception.NetworkException;
import express.field.agent.Request.net.BaseCall;
import express.field.agent.Request.net.NetworkCall;
import express.field.agent.Request.net.OnDownloadListener;
import express.field.agent.SplashScreenActivity;
import express.field.agent.Utils.Constants;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ApplicationUpdater implements NetworkCall.OnNetworkCallResultListener {

    private static ApplicationUpdater mInstance = null;
    private static final String path = getDirectory().getAbsolutePath() + File.separator + "FA_360.apk";

    private ApplicationUpdater() {
        NetworkCall.getInstance().registerListener(this);
    }

    public static ApplicationUpdater getInstance() {
        if(mInstance == null) {
            synchronized (ApplicationUpdater.class) {
                mInstance = new ApplicationUpdater();
            }
        }

        return mInstance;
    }

    public void update(URL url) {
//        showLoader();
        Call call = RetrofitManager.getDownloadService(getProgressListener()).downloadNewAppVersion(url.toString());
        NetworkCall.getInstance().makeRestCall(call, getNetworkCallResultDeliveryTag());
    }

    private static File getDirectory() {
        final Context context = ActivityProvider.getInstance().getCurrentActivity().getApplicationContext();
        File dir = new File(context.getExternalCacheDir(), Constants.APK_FOLDER);

        if (!dir.exists()) {
            dir.mkdir();
        }

        return dir;
    }

    private OnDownloadListener getProgressListener() {
        return new OnDownloadListener() {
            @Override
            public void onDownloadedSuccess() {
            }

            @Override
            public void onDownloadedError() {
            }

            @Override
            public void onDownloadedFinished() {
            }

            @Override
            public void ontDownloadUpdate(int percent) {
//                mNetworkLoader.updateText(percent + "%");
            }
        };
    }

    private void installNew() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            installPackage();
        } else {
            Uri apkUri = Uri.fromFile(new File(path));
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(apkUri, "application/vnd.android.package-archive");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ActivityProvider.getInstance().getCurrentActivity().startActivity(i);
        }
    }

    private void installPackage() {
        Context context = ContextProvider.getInstance().getContext();
        PackageInstaller.Session session = null;
        PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        params.setAppPackageName(Constants.PACKAGE_NAME);

        try {
            File file = new File(path);
            FileInputStream inputStream = new FileInputStream(file);

            int sessionId = packageInstaller.createSession(params);
            session = packageInstaller.openSession(sessionId);

            OutputStream packageInSession = session.openWrite("package", 0, -1);
            byte[] buffer = new byte[16384];
            int n;
            while ((n = inputStream.read(buffer)) >= 0) {
                packageInSession.write(buffer, 0, n);
            }
            session.fsync(packageInSession);
            inputStream.close();
            packageInSession.close();

            Intent intent = new Intent(context, SplashScreenActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            IntentSender statusReceiver = pendingIntent.getIntentSender();

            session.commit(statusReceiver);
            session.close();

            ActivityProvider.getInstance().getCurrentActivity().finish();
        } catch (IOException e) {
            e.printStackTrace();
            if (session != null) {
                session.abandon();
            }
        }
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            File apk = new File(path);
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(apk);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onNetworkCallResult(BaseCall call) {
        if (call != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean result = writeResponseBodyToDisk((ResponseBody) call.getRequestResult());
//                    hideLoader();
                    if (result) {
                        installNew();
                    } else {
//                        DialogAlert d = DialogAlert.newInstance(ResourceProvider.getString("update_failed_memory_space"), ResourceProvider.getString("ok"), getDialogResultDeliveryTag(), true);
//                        FragmentOperator.INSTANCE.showDialog(d);
                    }
                }
            }).start();
        }
    }

    @Override
    public void onNetworkCallException(NetworkException e) {
//        hideLoader();
        if (e != null) {
//            DialogAlert d = DialogAlert.newInstance(e.getMessage(), ResourceProvider.getString("ok"), getDialogResultDeliveryTag(), true );
//            FragmentOperator.INSTANCE.showDialog(d);
        }
    }

    @Override
    public String getNetworkCallResultDeliveryTag() {
        return getClass().getName();
    }


//
//    public void showLoader() {
//        if (!mNetworkLoader.isAdded()) {
//            FragmentOperator.INSTANCE.showDialog(mNetworkLoader);
//        }
//    }
//
//    public void hideLoader() {
//        if (mNetworkLoader.isAdded()) {
//            // Allows the commit to be executed after an activity's state is saved
//            // Used for cases where it is okay for the UI state to change unexpectedly to the user.
//            mNetworkLoader.dismissAllowingStateLoss();
//        }
//    }

}
