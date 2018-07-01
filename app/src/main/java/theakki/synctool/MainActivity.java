package theakki.synctool;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

import theakki.synctool.Helper.PreferencesHelper;
import theakki.synctool.Job.JobHandler;
import theakki.synctool.Job.NamedConnectionHandler;
import theakki.synctool.Job.Scheduler.Scheduler;
import theakki.synctool.System.SettingsHandler;

import static junit.framework.Assert.*;


public class MainActivity extends AppCompatActivity
{
    private final static String L_Tag = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        final boolean DEBUG = false;


        initSingletonData(this);
        prepareFolder();


        // All Jobs
        View.OnClickListener allJobsListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intentAllJobs = new Intent(MainActivity.this, AllJobs.class);
                startActivity(intentAllJobs);
            }
        };
        Button allJobsButton = findViewById(R.id.btn_AllJobs);
        assertNotNull("Button 'All jobs' not found", allJobsButton);
        allJobsButton.setOnClickListener(allJobsListener);

        // New Job
        View.OnClickListener newJobListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intentNewJob = new Intent(MainActivity.this, Wizzard_New1_General.class);
                startActivity(intentNewJob);
            }
        };
        Button newJobButton = findViewById(R.id.btn_NewJob);
        assertNotNull("Button 'New job' not found", newJobButton);
        newJobButton.setOnClickListener(newJobListener);

        // Settings
        Button settingsButton = findViewById(R.id.btn_Settings);
        assertNotNull("Button 'Settings' not found", settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSettingClick();
            }
        });


        // Exit
        Button exitButton = findViewById(R.id.btn_Exit);
        assertNotNull("Button 'Exit' not found", exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExitClick();
            }
        });
    }

    private void onExitClick()
    {
        PreferencesHelper.getInstance().saveData(this, NamedConnectionHandler.getInstance());
        PreferencesHelper.getInstance().saveData(this, JobHandler.getInstance());

        finish();
    }


    private void onSettingClick()
    {
        Intent intentSettings = new Intent(this, Settings.class);
        startActivity(intentSettings);
    }


    public static void initSingletonData(Context context)
    {
        Log.d(L_Tag, "Load SingletonData");

        // Jobs
        PreferencesHelper.getInstance().loadData(context, JobHandler.getInstance());

        // Connections
        PreferencesHelper.getInstance().loadData(context, NamedConnectionHandler.getInstance());

        // Settings
        SettingsHandler.getInstance().init(context);
        PreferencesHelper.getInstance().loadData(context, SettingsHandler.getInstance());

        // Scheduler
        Scheduler.getInstance().init(context);
        Scheduler.getInstance().update(JobHandler.getInstance().getSchedulers(true));
    }


    private void prepareFolder()
    {
        File f = new File(SettingsHandler.getInstance().getApplicationDataPath());
        if( f.isDirectory() )
            return;
        if(f.exists())
        {
            Log.e(L_Tag, "Application Path exist but is no directory");
            return;
        }
        else
        {
            f.mkdirs();
        }
    }


    @Override
    protected void onPause()
    {
        super.onPause();
    }


}
