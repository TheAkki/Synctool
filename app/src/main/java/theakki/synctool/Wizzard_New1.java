package theakki.synctool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import theakki.synctool.Job.JobHandler;
import theakki.synctool.Job.SyncJob;

/**
 * Created by theakki on 06.04.18.
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

        /*
        _textName.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                {
                    String strName = _textName.getText().toString().trim();
                    if(strName.length() > 0)
                    {
                        boolean bNameExist = JobHandler.getInstance().existJobByName(strName);
                        if(bNameExist == true)
                        {
                            String strError =getString(R.string.Toast_JobExist, strName);
                            Toast.makeText(v.getContext(), strError, Toast.LENGTH_SHORT).show();
                            _bNameIsValid = true;
                        }
                        else
                        {
                            _bNameIsValid = true;
                        }
                    }
                }
            }
        });
        */


        // Cancel
        _buttonCancel = findViewById(R.id.btn_Cancel);
        _buttonCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intentCancel = new Intent(Wizzard_New1.this, MainActivity.class);
                startActivity(intentCancel);
                //clickBack();
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
        startActivity(intentNext);
    }

    private void clickBack()
    {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }
}
