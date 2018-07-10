package theakki.synctool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import theakki.synctool.Helper.PreferencesHelper;
import theakki.synctool.Job.JobHandler;
import theakki.synctool.Job.NamedConnectionHandler;
import theakki.synctool.Job.Scheduler.Scheduler;
import theakki.synctool.Job.SyncJob;

import static junit.framework.Assert.*;

/**
 * Class to show the first wizzard page for new job
 * @author theakki
 * @since 0.1
 */
public class Wizzard_New1_General extends AppCompatActivity
{
    private static final String L_TAG = Wizzard_New1_General.class.getSimpleName();

    private EditText _textName;
    private Button _buttonCancel;
    private Button _buttonNext;
    private Switch _switchActive;

    private SyncJob _job = null;

    private Boolean _bNewCreated = false;
    private String _strOldName = "";

    public final static String SETTINGS = "Settings";
    public final static String OLD_JOBNAME = "OldJobName";

    public final int REQUESTCODE_NEXT_PAGE = 100;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_new1);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            String strSettings = extras.getString(Wizzard_New1_General.SETTINGS, "");
            if(strSettings.length() > 0)
                _job = JobHandler.getJob(strSettings);
            _strOldName = _job.Name();
        }
        if(_job == null)
        {
            _job = new SyncJob();
            _bNewCreated = true;
        }
        assertNotNull("Job not created", _job);


        // Name
        _textName = findViewById(R.id.edit_JobName);
        assertNotNull("Textview 'Name' not found", _textName);


        // Cancel
        _buttonCancel = findViewById(R.id.btn_Cancel);
        assertNotNull("Button 'Cancel' not found", _buttonCancel);
        _buttonCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clickBack();
            }
        });


        // Next
        _buttonNext = findViewById(R.id.btn_Next);
        assertNotNull("Button 'Next' not found", _buttonNext);
        _buttonNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onNextClick();
            }
        });

        // Switch Active
        _switchActive = findViewById(R.id.sw_JobActive);
        assertNotNull("Switch 'Active' not found", _switchActive);
    }


    @Override
    protected void onResume() {
        super.onResume();

        restoreDataFromJob();
    }


    private void restoreDataFromJob()
    {
        _textName.setText( _job.Name() );
        _switchActive.setChecked( _job.Active() );
    }


    private void onNextClick()
    {
        String strName = _textName.getText().toString().trim();

        if(strName.length() == 0)
        {
            Toast.makeText(this, R.string.Toast_JobNameIsEmpty, Toast.LENGTH_SHORT).show();
            return;
        }

        boolean bNameExist = JobHandler.getInstance().existJobByName(strName);
        if(bNameExist == true && _bNewCreated)
        {
            String strError = getString(R.string.Toast_JobExist, strName);
            Toast.makeText(this, strError, Toast.LENGTH_SHORT).show();
            Log.d(L_TAG, "Job with name '" + strName + "' already exist");
            return;
        }

        _job.Name(strName);
        _job.Active( _switchActive.isChecked() );

        Intent intentNext = new Intent(Wizzard_New1_General.this, Wizzard_New2_SourceTarget.class);
        final String strSettings = JobHandler.getSettings(_job);
        intentNext.putExtra(SETTINGS, strSettings );
        intentNext.putExtra(OLD_JOBNAME, _strOldName);
        startActivityForResult(intentNext, REQUESTCODE_NEXT_PAGE);
    }


    private void clickBack()
    {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUESTCODE_NEXT_PAGE)
        {
            if(resultCode == RESULT_OK)
            {
                // Save Connections
                PreferencesHelper.getInstance().saveData(this, NamedConnectionHandler.getInstance());

                Scheduler.getInstance().update( JobHandler.getInstance().getSchedulers(true) );

                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
