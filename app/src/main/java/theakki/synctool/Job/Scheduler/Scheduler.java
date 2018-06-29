package theakki.synctool.Job.Scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import java.util.Calendar;

import theakki.synctool.BroadcastReceiver;

import static android.content.Context.ALARM_SERVICE;


/**
 * Handling to activate timer events from Android systems
 * @author theakki
 * @since 0.1
 */
public class Scheduler
{
    private static final Scheduler ourInstance = new Scheduler();
    public static Scheduler getInstance()
    {
        return ourInstance;
    }

    public final static int ONE_PER_DAY_MILLIS = 24 * 60 * 60 * 1000; /* h * min * sec * milli */

    private AlarmManager _alarmManager;
    private HashMap<SchedulerInfo, PendingIntent> _activeAlarms = new HashMap<>();
    private Context _context;

    private Scheduler()
    {
    }


    public void init(Context context)
    {
        _context = context;
        _alarmManager = (AlarmManager) _context.getSystemService(ALARM_SERVICE);
    }


    /**
     * Update Android timer events. Create new or delete unneccessary events
     * @param alarms List with scheduler information
     */
    public void update(ArrayList<SchedulerInfo> alarms)
    {
        Set<SchedulerInfo> neededAlarm = new HashSet<>();
        for(SchedulerInfo alarm : alarms)
        {
            if(alarm.Active())
            {
                neededAlarm.add(alarm);
            }
        }

        // Delete not needed Alarms
        ArrayList<SchedulerInfo> activeAlarmList = new ArrayList<>(_activeAlarms.keySet());
        for(int i = 0; i < activeAlarmList.size(); ++i)
        {
            final SchedulerInfo alarm = activeAlarmList.get(i);
            if(neededAlarm.contains(alarm))
                continue;

            PendingIntent intent = _activeAlarms.get(alarm);
            deleteAlarm(intent);
            _activeAlarms.remove(alarm);
        }

        // Create new Alarms
        for(SchedulerInfo alarm : neededAlarm)
        {
            if(_activeAlarms.containsKey(alarm))
                continue;

            createAlarm(alarm);
        }
    }


    private void createAlarm(SchedulerInfo schedulerInfo)
    {
        Intent content = new Intent(_context, BroadcastReceiver.class);
        final int schedulerIdentifier = schedulerInfo.getIdentifier();
        content.putExtra(BroadcastReceiver.EXTRA_ALARM, schedulerIdentifier);


        // Calculate Next time for wakeup
        Calendar calendar = Calendar.getInstance();
        final long actualTime = System.currentTimeMillis();
        calendar.setTimeInMillis(actualTime);
        calendar.set(Calendar.HOUR_OF_DAY, schedulerInfo.Hour());
        calendar.set(Calendar.MINUTE, schedulerInfo.Minute());
        calendar.set(Calendar.SECOND, 0);
        long nextTime = calendar.getTimeInMillis();
        if(nextTime < actualTime)
            nextTime += ONE_PER_DAY_MILLIS;

        // set wakeup
        PendingIntent pendingIntent = PendingIntent.getBroadcast(_context, schedulerIdentifier, content, 0);
        _alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, nextTime, ONE_PER_DAY_MILLIS, pendingIntent);

        // Store Intent
        _activeAlarms.put(schedulerInfo, pendingIntent);
    }


    private void deleteAlarm(PendingIntent intent)
    {
        _alarmManager.cancel(intent);
    }
}
