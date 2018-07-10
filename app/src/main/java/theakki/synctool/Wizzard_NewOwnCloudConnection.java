package theakki.synctool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import theakki.synctool.Helper.Permissions;
import theakki.synctool.Helper.PreferencesHelper;
import theakki.synctool.Job.ConnectionTypes.ConnectionTypes;
import theakki.synctool.Job.ConnectionTypes.OwnCloud;
import theakki.synctool.Job.NamedConnectionHandler;

import static junit.framework.Assert.*;

public class Wizzard_NewOwnCloudConnection extends AppCompatActivity
{
    private Button _btnBack;
    private Button _btnOk;
    private EditText _txtUrl;
    private Button _btnScan;
    private EditText _txtName;
    private EditText _txtUser;
    private EditText _txtPassword;
    private CheckBox _cbAskPassword;
    private TextView _txtConnStatus;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_newowncloudconnection);

        // Back Button
        _btnBack = findViewById(R.id.btn_Back);
        assertNotNull("Button 'Back' not found", _btnBack);
        _btnBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                clickBack();
            }
        });

        // Ok Button
        _btnOk = findViewById(R.id.btn_Ok);
        assertNotNull("Button 'Next' not found", _btnOk);
        _btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickOk(view);
            }
        });
        setEnableOkButton(false);

        // Url Text
        _txtUrl = findViewById(R.id.txt_Url);
        assertNotNull("Text 'Url' not found", _txtUrl);

        // Button Scan
        _btnScan = findViewById(R.id.btn_Scan);
        assertNotNull("Button 'Scan' not found", _btnScan);
        _btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickScan();
            }
        });

        // Name Text
        _txtName = findViewById(R.id.txt_Name);
        assertNotNull("Text 'Name' not found", _txtName);

        // Status Text
        _txtConnStatus = findViewById(R.id.txt_ConnectionStatus);
        assertNotNull("Text 'Status' not found", _txtConnStatus);

        // Username Text
        _txtUser = findViewById(R.id.txt_username);
        assertNotNull("Text 'User' not found", _txtUser);

        // Password Text
        _txtPassword = findViewById(R.id.txt_Password);
        assertNotNull("Text 'Password' not found", _txtPassword);

        // Checkbox Ask Password
        _cbAskPassword = findViewById(R.id.cb_AskPassword);
        assertNotNull("Checkbox 'Ask Password' not found", _cbAskPassword);

        Permissions.requestForPermissionInternet(this);
    }

    private Activity getContext() { return this; }

    private boolean _bConnectionValid = false;
    private void clickScan()
    {
        final String strUri = _txtUrl.getText().toString().trim();
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                _bConnectionValid = OwnCloud.isAvailable(strUri, getContext());
            }
        });
        try {
            thread.start();
            thread.join();
        }
        catch(Exception e)
        {}

        if(_bConnectionValid)
        {
            _txtConnStatus.setText(getApplication().getResources().getString(R.string.ConnectionTestStatus_ScannedAvailable));
            setVisibleCredentials(true);
            setEnableOkButton(true);
        }
        else
        {
            _txtConnStatus.setText(getApplication().getResources().getString(R.string.ConnectionTestStatus_ScannedNotAvailable));
            setVisibleCredentials(false);
            setEnableOkButton(false);
        }
    }


    private void clickBack()
    {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }


    private boolean _bUserPermissionOk = false;
    private void clickOk(View v)
    {
        String strName = _txtName.getText().toString().trim();

        if(strName.length() == 0) {
            Toast.makeText(v.getContext(), R.string.Toast_JobNameIsEmpty, Toast.LENGTH_SHORT).show();
            return;
        }

        if(NamedConnectionHandler.getInstance().existConnection(strName) == true)
        {
            String strError = getString(R.string.Toast_ConnectionExist, strName);
            Toast.makeText(v.getContext(), strError, Toast.LENGTH_SHORT).show();
            return;
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final String username = _txtUser.getText().toString().trim();
                final String password = _txtPassword.getText().toString();
                final String strUri = _txtUrl.getText().toString().trim();

                _bUserPermissionOk = OwnCloud.IsAccessible(strUri, getContext(), username, password);
            }
        });
        try {
            thread.start();
            thread.join();
        }
        catch(Exception e)
        {}

        if(_bUserPermissionOk)
        {
            _txtConnStatus.setText(getApplication().getResources().getString(R.string.ConnectionTestStatus_AccessGranted));
        }
        else
        {
            _txtConnStatus.setText(getApplication().getResources().getString(R.string.ConnectionTestStatus_AccessDenied));
            Toast.makeText(v.getContext(), R.string.Toast_InvalidUserPassword, Toast.LENGTH_SHORT).show();
            return;
        }

        NamedConnectionHandler.Connection connection = new NamedConnectionHandler.Connection();
        connection.Url = _txtUrl.getText().toString().trim();
        connection.User = _txtUser.getText().toString().trim();
        connection.Password = _txtPassword.getText().toString();
        connection.Type = ConnectionTypes.OwnCloud;
        connection.Port = OwnCloud.iDEF_PORT;
        boolean storePassword = !_cbAskPassword.isChecked();

        NamedConnectionHandler.getInstance().add(strName, connection, storePassword);
        PreferencesHelper.getInstance().saveData(this, NamedConnectionHandler.getInstance());

        final Intent intent = new Intent();
        intent.putExtra(Wizzard_New2_SourceTarget.Extra_ConnectionName, strName);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    private void setVisibleCredentials(boolean visible)
    {
        final int vis = (visible) ? View.VISIBLE : View.GONE;

        _txtUser.setVisibility(vis);
        _txtPassword.setVisibility(vis);
        _cbAskPassword.setVisibility(vis);
    }


    private void setEnableOkButton(boolean visible)
    {
        _btnOk.setEnabled(visible);
    }
}
