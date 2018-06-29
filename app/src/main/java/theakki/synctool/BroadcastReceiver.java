package theakki.synctool.Job.Scheduler;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Implements a Service to do the scheduled jobs in background
 * @author theakki
 * @since 0.1
 */
public class ScheduleReceiver extends BroadcastReceiver
{
    private final static String L_TAG = ScheduleReceiver.class.getSimpleName();

    public final static String EXTRA_ALARM = "AlarmContent";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.v(L_TAG, "Receive message");

        if(intent.hasExtra(EXTRA_ALARM))
        {
            Log.d(L_TAG, "Receive Alarm message");
            Intent intService = new Intent(context, SchedulerService.class);
            intService.putExtras(intent);
            context.startService(intService);
        }
        else
        {
            Log.v(L_TAG, "Unknown Intent");
        }

        setResultCode(Activity.RESULT_OK);
    }
}
