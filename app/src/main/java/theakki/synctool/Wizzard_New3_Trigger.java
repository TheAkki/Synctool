package theakki.synctool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import android.widget.Button;

import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import theakki.synctool.Helper.PreferencesHelper;

import theakki.synctool.Job.JobHandler;
import theakki.synctool.Job.Scheduler.SchedulerInfo;
import theakki.synctool.Job.SyncJob;

/**
 * Class to show the second wizzard page for new job
 * @author theakki
 * @since 0.1
 */
public class Wizzard_New3_Trigger extends AppCompatActivity
{
    private final static String L_TAG = Wizzard_New3_Trigger.class.getSimpleName();

    private SyncJob _job;
    private String _strOldJobName;

    private Switch _switchActive;
    private TimePicker _TimePicker;
    private TextView _txtTime;

    private Button _buttonBack;
    private Button _buttonNext;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_new3);

        Bundle extras = getIntent().getExtras();
        final String strSettings = extras.getString(Wizzard_New1_General.SETTINGS);
        _job = JobHandler.getJob(strSettings);
        _strOldJobName = extras.getString(Wizzard_New1_General.OLD_JOBNAME, "");


        // Scheduler active
        _switchActive = findViewById(R.id.sw_SchedulerActive);
        assert _switchActive != null : "Switch 'Scheduler active' not found";
        _switchActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onActiveChanged( isChecked );
            }
        });


        // Scheduler time picker
        _TimePicker = findViewById(R.id.time_Scheduler);
        assert _TimePicker != null : "Timepicker not found";
        _TimePicker.setIs24HourView(android.text.format.DateFormat.is24HourFormat(this));
        _TimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                onSetTime(hourOfDay, minute);
            }
        });

        // Scheduler time view
        _txtTime = findViewById(R.id.txt_Time);
        assert _txtTime != null : "Text 'Time' not found";
        _txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTimeEdit();
            }
        });


        // Back
        _buttonBack = findViewById(R.id.btn_Back);
        assert _buttonBack != null : "Button 'Back' not found";
        _buttonBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clickBack();
            }
        });

        // Next
        _buttonNext = findViewById(R.id.btn_Next);
        assert _buttonNext != null : "Button 'Next' not found";
        _buttonNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onNextClick();
            }
        });

        restoreDataFromJob();
    }


    private void onActiveChanged(boolean active)
    {
        _TimePicker.setEnabled(active);

        if(active)
        {
            //_TimePicker.setVisibility(View.VISIBLE);
        }
        else
        {
            //_TimePicker.setVisibility(View.INVISIBLE);
        }
    }


    private void onTimeEdit()
    {
        //_TimePicker.setVisibility(View.VISIBLE);
    }


    private String getTimeString(int hour, int min)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);

        if(_TimePicker.is24HourView())
        {
            return android.text.format.DateFormat.format("HH:mm", calendar).toString();
        }
        else
        {
            // Maybe: See https://stackoverflow.com/a/44537609/5561864
            return android.text.format.DateFormat.format("HH:mm a", calendar).toString();
        }
    }


    private void onSetTime(int hour, int minute)
    {
        //_TimePicker.setVisibility(View.GONE);
        //_txtTime.setText( getTimeString(hour, minute) );
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private void restoreDataFromJob()
    {
        SchedulerInfo oSchedInfo = _job.Scheduler();

        final boolean isActive = oSchedInfo.Active();
        _switchActive.setChecked( isActive );
        onActiveChanged(isActive);
        _TimePicker.setCurrentHour(_job.Scheduler().Hour());
        _TimePicker.setCurrentMinute( _job.Scheduler().Minute() );

        //_txtTime.setText( getTimeString(oSchedInfo.Hour(), oSchedInfo.Minute()) );

    }


    private void storeDataIntoJob()
    {
        _job.Scheduler().Active( _switchActive.isChecked() );
        _job.Scheduler().Hour( _TimePicker.getCurrentHour() );
        _job.Scheduler().Minute( _TimePicker.getCurrentMinute() );
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void clickBack()
    {
        Intent intentBack = new Intent();
        intentBack.putExtra(Wizzard_New1_General.SETTINGS, JobHandler.getSettings(_job) );
        setResult(Activity.RESULT_CANCELED, intentBack);
        finish();
    }


    private void onNextClick()
    {
        // validation

        storeDataIntoJob();

        // Creation is finished => Store
        storeJob();

        Intent intentNext = new Intent();
        intentNext.putExtra(Wizzard_New1_General.SETTINGS, JobHandler.getSettings(_job) );
        setResult(Activity.RESULT_OK, intentNext);
        finish();
    }


    private void storeJob()
    {
        if(_strOldJobName.length() > 0)
        {
            Log.d(L_TAG, "Update job with name '" + _job.Name() + "' (old '" + _strOldJobName + "')");
            JobHandler.getInstance().updateJob(_strOldJobName, _job);
        }
        else
        {
            Log.d(L_TAG, "Insert new job with name '" + _job.Name() + "'");
            JobHandler.getInstance().addJob(_job);
        }

        PreferencesHelper.getInstance().saveData(this, JobHandler.getInstance());
    }
}
