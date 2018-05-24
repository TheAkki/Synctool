package theakki.synctool.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import theakki.synctool.Job.JobHandler;
import theakki.synctool.Job.NamedConnectionHandler;

public class PreferencesHelper {
    private static final PreferencesHelper ourInstance = new PreferencesHelper();

    private final String MyPREFERENCES = "MyPrefs" ;
    private final String PREF_Connections = "Connections";
    private final String PREF_Jobs = "Jobs";

    public static PreferencesHelper getInstance() {
        return ourInstance;
    }


    private PreferencesHelper()
    {

    }

    public void loadData(Context context,  NamedConnectionHandler ConHandler)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final String strConnections = sharedpreferences.getString(PREF_Connections, "");

        ConHandler.setup(strConnections, true);
    }

    public void saveData(Context context, NamedConnectionHandler ConHandler)
    {
        final String strConnections = ConHandler.getData();

        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PREF_Connections, strConnections);
        editor.commit();
    }

    public void loadData(Context context,  JobHandler JobHandler, String strDefault)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final String strJobs = sharedpreferences.getString(PREF_Jobs, strDefault);

        JobHandler.setup(strJobs, true);
    }

    public void saveData(Context context, JobHandler JobHandler)
    {
        final String strJobs = JobHandler.getData();

        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PREF_Jobs, strJobs);
        editor.commit();
    }

}
