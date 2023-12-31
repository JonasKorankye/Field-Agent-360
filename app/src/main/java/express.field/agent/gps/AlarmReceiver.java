package express.field.agent.gps;

import android.content.Context;
import android.content.Intent;

import androidx.legacy.content.WakefulBroadcastReceiver;

/**
 * Created by myron echenim  on 9/10/16.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context,LocationService.class));
    }
}
