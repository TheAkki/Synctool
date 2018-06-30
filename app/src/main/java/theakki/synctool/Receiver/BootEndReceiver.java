package theakki.synctool.Receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import theakki.synctool.MainActivity;
import theakki.synctool.R;

public class BootEndReceiver extends AlarmReceiver
{
    private final static String L_TAG = BootEndReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.v(L_TAG, "Receive Boot finished or Application available");
        //NotifyBootEnd(context);

        MainActivity.initSingletonData(context);
    }

    private void NotifyBootEnd(Context context)
    {
        final int iNotificationID = 1234;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "")
            .setSmallIcon(R.drawable.ic_stat_notify_msg)
            //.setContentTitle(textTitle)
            .setContentText(context.getText(R.string.NotificationContent_BootEnd))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(iNotificationID, mBuilder.build());
    }
}
