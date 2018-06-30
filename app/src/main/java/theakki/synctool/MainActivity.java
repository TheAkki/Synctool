package theakki.synctool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;


import theakki.synctool.Helper.Permissions;
import theakki.synctool.Helper.PreferencesHelper;
import theakki.synctool.Helper.TestEnvironmentHelper;
import theakki.synctool.Job.JobHandler;
import theakki.synctool.Job.NamedConnectionHandler;
import theakki.synctool.Job.Scheduler.Scheduler;
import theakki.synctool.System.SettingsHandler;

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


        if(DEBUG)
        {
            View.OnClickListener listener = new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // Normaler Testfall
                    //JobHandler.getInstance().Do((Activity) v.getContext(), false);
                    //String loadSettings = JobHandler.getInstance().getSettings();

                    // Owncloud Test
                    //OwnCloud oc = new OwnCloud();
                    //oc.User("OwnCloudConnection");
                    //oc.Password("Test");
                    //oc.Url("https://192.168.178.42/owncloud");

                    //oc.Connect((Activity)v.getContext());


                }
            };

            Button testButton = findViewById(R.id.btnTest);
            if(testButton != null)
            {
                testButton.setOnClickListener(listener);
                testButton.setVisibility(View.VISIBLE);
            }

            View.OnClickListener setupListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Permissions.requestForPermissionSD((Activity) v.getContext());

                    Spinner spn = findViewById(R.id.spn_Setup);
                    if(spn == null)
                        return;

                    int iSelected = spn.getSelectedItemPosition();

                    switch(iSelected)
                    {
                        case 0:
                            TestEnvironmentHelper.createSetup1();
                            break;

                        default:
                            break;
                    }
                }

            };

            Button setupButton = findViewById(R.id.btn_Setup);
            if(setupButton != null)
            {
                setupButton.setOnClickListener(setupListener);
                setupButton.setVisibility(View.VISIBLE);

                Spinner spn = findViewById(R.id.spn_Setup);
                spn.setVisibility(View.VISIBLE);
            }
        }

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
        assert  allJobsButton != null : "Button 'All jobs' not found";
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
        assert newJobButton != null : "Button 'New job' not found";
        newJobButton.setOnClickListener(newJobListener);

        // Settings
        Button settingsButton = findViewById(R.id.btn_Settings);
        //Assert.assertTrue ( "Button 'Settings' not found", settingsButton == null);
        assert settingsButton != null : "Button 'Settings' not found";
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSettingClick();
            }
        });


        // Exit
        Button exitButton = findViewById(R.id.btn_Exit);
        assert exitButton != null : "Button 'Exit' not found";
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

    @Override
    protected void onPause()
    {
        super.onPause();
    }


}
