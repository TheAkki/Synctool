package theakki.synctool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import static junit.framework.Assert.*;

import theakki.synctool.Helper.PreferencesHelper;
import theakki.synctool.System.ImportExport;
import theakki.synctool.System.SettingsHandler;

public class Settings extends AppCompatActivity
{
    private static final String L_Tag = Settings.class.getSimpleName();

    private Button _btnOk;
    private Switch _swBootEnd;

    private Button _btnImport;
    private Button _btnExport;


    private static class Request
    {
        public static final int ImportFileSelect = 1;
        public static final int ExportFileSelect = 2;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Button "OK"
        _btnOk = findViewById(R.id.btn_Ok);
        assertNotNull("Button 'OK' not found", _btnOk);
        _btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOkClick();
            }
        });


        // Switch "Boot End"
        _swBootEnd = findViewById(R.id.sw_BootEnd);
        assertNotNull("Switch 'Boot End' not found", _swBootEnd);
        _swBootEnd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onSwitchChanged_BootEnd(isChecked);
            }
        });


        // Button "Import"
        _btnImport = findViewById(R.id.btn_Import);
        assertNotNull("Button 'Import' not found", _btnImport);
        _btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onImportClick();
            }
        });


        // Button "Export"
        _btnExport = findViewById(R.id.btn_Export);
        assertNotNull("Button 'Export' not found", _btnExport);
        _btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onExportClick();
            }
        });
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        loadViewFromData();
    }


    private Context getContext()
    {
        return this;
    }


    private void onOkClick()
    {
        PreferencesHelper.getInstance().saveData(this, SettingsHandler.getInstance());

        Intent intentBack = new Intent();
        setResult(Activity.RESULT_OK, intentBack);
        finish();
    }


    private void onImportClick()
    {
        // Source: https://stackoverflow.com/questions/41193219/how-to-select-file-on-android-using-intent#41195531
        // Source: https://developer.android.com/guide/topics/providers/document-provider

        Intent importFilePickerIntent = new Intent(Intent.ACTION_GET_CONTENT); // Intent.ACTION_OPEN_DOCUMENT // Intent.ACTION_PICK // ACTION_GET_CONTENT
        importFilePickerIntent.setType("*/*");
        importFilePickerIntent.addCategory(Intent.CATEGORY_OPENABLE);

        if (importFilePickerIntent.resolveActivityInfo(getPackageManager(), 0) != null)
        {
            Log.d(L_Tag, "Call external app for selecting import file");
            startActivityForResult(importFilePickerIntent, Request.ExportFileSelect);
        }
        else
        {
            Log.e(L_Tag, "No file explorer app installed");
            Toast.makeText(this, R.string.Toast_NoFileExplorerAppInstalled, Toast.LENGTH_SHORT).show();
        }
    }

    private void importFile(final Uri uri)
    {
        final ProgressDialog progress = ProgressDialog.show(this, getString(R.string.Status_PleaseWait), getString(R.string.Settings_Import), true, false);
        new Thread()
        {
            public void run()
            {
                ImportExport.getInstance().Import(getContext(), uri);
                progress.dismiss();
            }
        }.start();
    }


    private static final String MIME_TYPE = "application/xml";

    private String getDefaultFilename()
    {
        final String date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(System.currentTimeMillis());

        return date + ".synctool";
    }

    private void onExportClick()
    {
        // Source: https://stackoverflow.com/a/26651827/5561864
        Uri applicationPath = Uri.parse( SettingsHandler.getInstance().getApplicationDataPath() );

        Intent exportFilePickerIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        exportFilePickerIntent.addCategory(Intent.CATEGORY_OPENABLE);
        exportFilePickerIntent.setType(MIME_TYPE);
        exportFilePickerIntent.putExtra(Intent.EXTRA_TITLE, getDefaultFilename());
        //exportFilePickerIntent.setDataAndType(applicationPath, MIME_TYPE);

        if (exportFilePickerIntent.resolveActivityInfo(getPackageManager(), 0) != null)
        {
            Log.d(L_Tag, "Call external app for selecting export file");
            startActivityForResult(exportFilePickerIntent, Request.ExportFileSelect);
        }
        else
        {
            Log.e(L_Tag, "No file explorer app installed");
            Toast.makeText(this, R.string.Toast_NoFileExplorerAppInstalled, Toast.LENGTH_SHORT).show();
        }
    }

    private void exportFile(final Uri uri)
    {
        final ProgressDialog progress = ProgressDialog.show(this, getString(R.string.Status_PleaseWait), getString(R.string.Settings_Export), true, false);
        new Thread()
        {
            public void run()
            {
                ImportExport.getInstance().Export(getContext(), uri, false);
                progress.dismiss();
            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode)
        {
            case Request.ImportFileSelect:
                Log.v(L_Tag, "Result from ImportFileSelect");
                if(resultCode == RESULT_OK)
                {
                    Uri uri;
                    if (intent != null) {
                        uri = intent.getData();
                        Log.i(L_Tag, "Uri: " + uri.toString());
                        importFile(uri);
                    }
                }
                break;

            case Request.ExportFileSelect:
                Log.v(L_Tag, "Result from ExportFileSelect");
                if(resultCode == RESULT_OK)
                {
                    Uri uri;
                    if (intent != null) {
                        uri = intent.getData();
                        Log.i(L_Tag, "Uri: " + uri.toString());
                        exportFile(uri);
                    }
                }
                break;

            default:
                Log.e(L_Tag, "RequestCode " + requestCode + " not implemented");
                break;
        }
    }


    private void onSwitchChanged_BootEnd(boolean value)
    {
        SettingsHandler.getInstance().StartWithBootEnd( value );
        //if(value)
        //{
        //    if(Permissions.canAccessBootEndMessage(this) == false)
        //    {
        //        Log.e(L_Tag, "Not possible to get Permission");
        //    }
        //}
    }


    private void loadViewFromData()
    {
        _swBootEnd.setChecked( SettingsHandler.getInstance().StartWithBootEnd() );
    }






}
