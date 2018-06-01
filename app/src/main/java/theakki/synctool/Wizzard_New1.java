package theakki.synctool;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import theakki.synctool.Helper.PreferencesHelper;
import theakki.synctool.Job.JobHandler;
import theakki.synctool.Job.NamedConnectionHandler;
import theakki.synctool.Job.SyncJob;

/**
 * Class to show the first wizzard page for new job
 * @author theakki
 * @since 0.1
 */
public class Wizzard_New1 extends AppCompatActivity
{
    private EditText _textName;
    private Button _buttonCancel;
    private Button _buttonNext;
    private Switch _switchActive;

    private SyncJob _job;

    private Boolean _bNewCreated = false;

    public final static String SETTINGS = "Settings";

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
            String strSettings = extras.getString(Wizzard_New1.SETTINGS, "");
            if(strSettings.length() > 0)
                _job = JobHandler.getJob(strSettings);
        }
        if(_job == null)
        {
            _job = new SyncJob();
            _bNewCreated = true;
        }


        // Name
        _textName = findViewById(R.id.edit_JobName);


        // Cancel
        _buttonCancel = findViewById(R.id.btn_Cancel);
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
        _buttonNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onNextClick();
            }
        });

        // Switch Active
        _switchActive = findViewById(R.id.sw_Active);

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
            return;
        }

        _job.Name(strName);
        _job.Active( _switchActive.isChecked() );

        Intent intentNext = new Intent(Wizzard_New1.this, Wizzard_New2.class);
        final String strSettings = JobHandler.getSettings(_job);
        intentNext.putExtra(SETTINGS, strSettings );
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

                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
}
