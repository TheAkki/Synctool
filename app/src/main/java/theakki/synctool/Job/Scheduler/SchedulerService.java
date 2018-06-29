package theakki.synctool.Job.Scheduler;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import theakki.synctool.BroadcastReceiver;
import theakki.synctool.Job.JobHandler;
import theakki.synctool.Job.SyncJob;
import theakki.synctool.R;

/**
 * Implements a Service to do the scheduled jobs in background
 * @author theakki
 * @since 0.1
 */
public class SchedulerService extends IntentService
{
    private static final String L_TAG = SchedulerService.class.getSimpleName();

    private static int _iNotifierId = 1000;

    public SchedulerService()
    {
        super("SchedulerService");
    }


    @Override
    public void onHandleIntent(Intent intent)
    {
        if(intent.hasExtra(BroadcastReceiver.EXTRA_ALARM))
        {
            SchedulerInfo schedulerInfo = new SchedulerInfo( intent.getIntExtra(BroadcastReceiver.EXTRA_ALARM, 0) );
            Log.d(L_TAG, "Get Wakeup with Extra " + schedulerInfo.getIdentifier());

            ArrayList<SyncJob> list = JobHandler.getInstance().getByScheduler(schedulerInfo, true);
            for(SyncJob job : list)
            {
                final int iNotifierID = _iNotifierId++;

                final String strName = job.Name();
                NotifyStart(strName, iNotifierID);

                // Execute and wait till finish with timeout of 100 seconds
                job.execute(this);
                try
                {
                    job.get(100, TimeUnit.SECONDS);
                }
                catch(Exception e)
                {
                    Log.e(L_TAG, e.getStackTrace().toString());
                    e.printStackTrace();
                    NotifyFailed(strName, iNotifierID);
                    continue;
                }
                NotifyFinished(strName, iNotifierID);
            }
        }
    }

    private void NotifyStart(String name, int id)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "")
            .setSmallIcon(R.drawable.ic_stat_notify_msg)
            //.setContentTitle(textTitle)
            .setContentText(getString(R.string.NotificationContent_ScheduledSyncJob_Started, name))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(id, mBuilder.build());
    }


    private void NotifyFinished(String name, int id)
    {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "")
            .setSmallIcon(R.drawable.ic_stat_notify_msg)
            //.setContentTitle(textTitle)
            .setContentText(getString(R.string.NotificationContent_ScheduledSyncJob_Finished, name))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(id, mBuilder.build());
    }


    private void NotifyFailed(String name, int id)
    {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "")
            .setSmallIcon(R.drawable.ic_stat_notify_msg)
            //.setContentTitle(textTitle)
            .setContentText(getString(R.string.NotificationContent_ScheduledSyncJob_Failed, name))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(id, mBuilder.build());
    }
}
