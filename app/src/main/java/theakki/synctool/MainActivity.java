package theakki.synctool;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.Spinner;


import theakki.synctool.Helper.Permissions;
import theakki.synctool.Helper.PreferencesHelper;
import theakki.synctool.Helper.TestEnvironmentHelper;
import theakki.synctool.Job.ConnectionTypes.OwnCloud;
import theakki.synctool.Job.JobHandler;
import theakki.synctool.Job.NamedConnectionHandler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        final boolean DEBUG = true;

        initSingletonData();

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
        allJobsButton.setOnClickListener(allJobsListener);

        // New Job
        View.OnClickListener newJobListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intentNewJob = new Intent(MainActivity.this, Wizzard_New1.class);
                startActivity(intentNewJob);
            }
        };
        Button newJobButton = findViewById(R.id.btn_NewJob);
        newJobButton.setOnClickListener(newJobListener);
    }

    private void initSingletonData()
    {
        // Jobs
        final String Settings = "<JobHandler><SyncJob><Name>Test</Name><SideA type = \"LocalPath\" ><LocalPath><Path>/sdcard/SyncTest/A</Path></LocalPath></SideA><SideB type = \"LocalPath\" ><LocalPath><Path>/sdcard/SyncTest/B</Path></LocalPath></SideB></SyncJob></JobHandler>";
        PreferencesHelper.getInstance().loadData(this, JobHandler.getInstance(), Settings);

        // Connections
        PreferencesHelper.getInstance().loadData(this, NamedConnectionHandler.getInstance());
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }
}
