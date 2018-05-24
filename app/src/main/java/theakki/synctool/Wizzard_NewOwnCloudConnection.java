package theakki.synctool;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.owncloud.android.lib.common.OwnCloudAccount;
import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientManagerFactory;
import com.owncloud.android.lib.common.authentication.OwnCloudCredentials;
import com.owncloud.android.lib.common.authentication.OwnCloudCredentialsFactory;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.resources.users.GetRemoteUserInfoOperation;

import theakki.synctool.FromOwnCloud.GetServerInfoOperation;
import theakki.synctool.Helper.Permissions;
import theakki.synctool.Helper.PreferencesHelper;
import theakki.synctool.Job.ConnectionTypes.ConnectionTypes;
import theakki.synctool.Job.ConnectionTypes.OwnCloud;
import theakki.synctool.Job.NamedConnectionHandler;

import android.R.*;

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
        _btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickOk(view);
            }
        });
        setEnableOkButton(false);

        // Url Text
        _txtUrl = findViewById(R.id.txt_Url);

        // Button Scan
        _btnScan = findViewById(R.id.btn_Scan);
        _btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickScan();
            }
        });

        // Name Text
        _txtName = findViewById(R.id.txt_Name);

        // Status Text
        _txtConnStatus = findViewById(R.id.txt_ConnectionStatus);

        // Username Text
        _txtUser = findViewById(R.id.txt_username);

        // Password Text
        _txtPassword = findViewById(R.id.txt_Password);

        // Checkbox Ask Password
        _cbAskPassword = findViewById(R.id.cb_AskPassword);

        Permissions.requestForPermissionInternet(this);
    }

    private boolean _bConnectionValid = false;
    private void clickScan()
    {
        final String strUri = _txtUrl.getText().toString().trim();
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    RemoteOperationResult res = testConnection( OwnCloudCredentialsFactory.getAnonymousCredentials(), strUri );
                    _bConnectionValid = res.isSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

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


    private RemoteOperationResult testConnection(OwnCloudCredentials cred, String uriServer)
    {
        try {
            Uri uri = Uri.parse(uriServer);

            OwnCloudAccount acc = new OwnCloudAccount(uri, cred);
            OwnCloudClient client = OwnCloudClientManagerFactory.getDefaultSingleton().getClientFor(acc, this);

            GetServerInfoOperation serverInfo = new GetServerInfoOperation(uriServer, this);
            return serverInfo.execute(client);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return new RemoteOperationResult(RemoteOperationResult.ResultCode.UNKNOWN_ERROR);
    }


    private void clickBack()
    {
        this.onBackPressed();
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
                try {
                    final String username = _txtUser.getText().toString().trim();
                    final String password = _txtPassword.getText().toString();
                    final String strUri = _txtUrl.getText().toString().trim();

                    RemoteOperationResult res = checkCredentials(OwnCloudCredentialsFactory.newBasicCredentials(username, password), strUri);
                    _bUserPermissionOk = res.isSuccess();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

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
        boolean storePassword = !_cbAskPassword.isChecked();

        NamedConnectionHandler.getInstance().add(strName, connection, storePassword);
        PreferencesHelper.getInstance().saveData(this, NamedConnectionHandler.getInstance());

        final Intent intent = new Intent();
        intent.putExtra(Wizzard_New2.Extra_ConnectionName, strName);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private RemoteOperationResult checkCredentials(OwnCloudCredentials cred, String uriServer)
    {
        try
        {
            Uri uri = Uri.parse(uriServer);

            OwnCloudAccount acc = new OwnCloudAccount(uri, cred);
            OwnCloudClient client = OwnCloudClientManagerFactory.getDefaultSingleton().getClientFor(acc, this);
            client.setBaseUri(uri);

            GetRemoteUserInfoOperation op = new GetRemoteUserInfoOperation();
            return op.execute(client);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return new RemoteOperationResult(RemoteOperationResult.ResultCode.UNKNOWN_ERROR);
        }

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
