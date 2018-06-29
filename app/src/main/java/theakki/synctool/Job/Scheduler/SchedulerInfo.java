package theakki.synctool.Job.Scheduler;

import android.support.annotation.*;

/**
 * This class to store Scheduler information
 * @author theakki
 * @since 0.1
 */
public class SchedulerInfo
{
    private boolean _Active;
    private int _Hour;
    private int _Minute;

    private final int ACTIVE = 10000;


    /**
     * Constructor create a non-active event on 00:00
     */
    public SchedulerInfo()
    {
        _Active = false;
        _Hour = 0;
        _Minute = 0;
    }


    /**
     * Constructor
     * @param active Is Scheduler is active
     * @param hour Hour of Event
     * @param minute Minute of Event;
     */
    public SchedulerInfo(boolean active, @IntRange(from=0,to=23) int hour, @IntRange(from=0,to=59)int minute)
    {
        _Active = active;
        _Hour = hour;
        _Minute = minute;
    }


    /**
     * Constructor from identifier
     * @param identifier Identifier
     */
    public SchedulerInfo(int identifier)
    {
        _Active = isActiveFromIdentifier(identifier);
        _Hour = hourFromIdentifier(identifier);
        _Minute = minuteFromIdentifier(identifier);
    }

    private boolean isActiveFromIdentifier(int id)
    {
        final int value = id / ACTIVE;
        final int active = value % 2;

        return (active > 0);
    }

    private int hourFromIdentifier(int id)
    {
        final int value = id % ACTIVE;
        final int hour = value / 100;
        return hour;
    }

    private int minuteFromIdentifier(int id)
    {
        final int value = id % ACTIVE;
        final int minute = value % 100;
        return minute;
    }


    /**
     * Get Identifier of SchedulerInfo
     * @return Identifier
     */
    public int getIdentifier()
    {
        final int minute = _Minute;
        final int hour = _Hour * 100;
        final int active = (_Active) ? ACTIVE : 0;
        return active + hour + minute;
    }


    /**
     * Return configured Minute
     * @return Minute
     */
    public int Minute(){ return _Minute; }


    /**
     * Set Minute
     * @param minute Minute
     * @return True when valid
     */
    public boolean Minute(@IntRange(from=0,to=59) int minute)
    {
        if(minute < 0 || minute > 59 )
            return false;
        _Minute = minute;
        return true;
    }


    /**
     * Return configured Hour
     * @return Hour
     */
    public int Hour() { return _Hour; }


    /**
     * Set Hour
     * @param hour Hour
     * @return True when valid
     */
    public boolean Hour(@IntRange(from=0,to=23) int hour)
    {
        if(hour < 0 || hour > 23)
            return false;
        _Hour = hour;
        return true;
    }


    /**
     * Get the State
     * @return Is active
     */
    public boolean Active(){ return _Active; }


    /**
     * Set State
     * @param active Set active
     */
    public void Active(boolean active){ _Active = active; }


    /**
     * Compare 2 Scheduler Infos
     * @param schedulerInfo
     * @return True when equal
     */
    public boolean equal(SchedulerInfo schedulerInfo)
    {
        return (getIdentifier() == schedulerInfo.getIdentifier());
    }
}
