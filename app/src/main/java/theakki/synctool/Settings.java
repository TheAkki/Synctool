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

import theakki.synctool.Helper.Permissions;
import theakki.synctool.Helper.PreferencesHelper;
import theakki.synctool.System.SettingsHandler;

public class Settings extends AppCompatActivity
{
    private static final String L_Tag = Settings.class.getSimpleName();

    private Button _btnOk;
    private Switch _swBootEnd;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        _btnOk = findViewById(R.id.btn_Ok);
        assert _btnOk != null : "Button 'OK' not found";
        _btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOkClick();
            }
        });

        _swBootEnd = findViewById(R.id.sw_BootEnd);
        assert _swBootEnd != null : "Switch 'Boot End' not found";
        _swBootEnd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onSwitchChanged_BootEnd(isChecked);
            }
        });
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        loadViewFromData();
    }


    private void onOkClick()
    {
        PreferencesHelper.getInstance().saveData(this, SettingsHandler.getInstance());

        Intent intentBack = new Intent();
        setResult(Activity.RESULT_OK, intentBack);
        finish();
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
