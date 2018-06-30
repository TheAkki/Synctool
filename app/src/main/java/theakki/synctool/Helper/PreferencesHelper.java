package theakki.synctool.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import theakki.synctool.Job.JobHandler;
import theakki.synctool.Job.NamedConnectionHandler;
import theakki.synctool.System.SettingsHandler;

/**
 * Util class for handle Preferences
 * @author theakki
 * @since 0.1
 */
public class PreferencesHelper
{
    private static final PreferencesHelper ourInstance = new PreferencesHelper();

    private final String MyPREFERENCES = "MyPrefs" ;
    private final String PREF_Connections = "Connections";
    private final String PREF_Jobs = "Jobs";
    private final String PREF_Settings = "Settings";


    /**
     * Method to get singleton object
     * @return Object of Helper
     */
    public static PreferencesHelper getInstance() {
        return ourInstance;
    }


    /**
     * Constructor
     */
    private PreferencesHelper()
    {
    }


    /**
     * This Method load Data for a NamedConnectionHandler
     * @param context Context of app
     * @param ConHandler ConnectionHandler which is to fill with stored data
     */
    public void loadData(Context context, NamedConnectionHandler ConHandler)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final String strConnections = sharedpreferences.getString(PREF_Connections, NamedConnectionHandler.DEFAULT_SETTINGS);

        ConHandler.setup(strConnections, true);
    }


    /**
     * This Method store Data from a NamedConnectionHandler
     * @param context Context of app
     * @param ConHandler ConnectionHandler which provide data.
     */
    public void saveData(Context context, NamedConnectionHandler ConHandler)
    {
        final String strConnections = ConHandler.getData();

        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PREF_Connections, strConnections);
        editor.commit();
    }


    /**
     * This Method load data for a job handler.
     * @param context Context of app
     * @param JobHandler JobHandler which is to fill with data
     */
    public void loadData(Context context,  JobHandler JobHandler)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final String strJobs = sharedpreferences.getString(PREF_Jobs, JobHandler.DEFAULT_SETTINGS);

        JobHandler.setup(strJobs, true);
    }


    /**
     * This Method store data from a job handler
     * @param context Context of app
     * @param JobHandler JobHandler which is provide the data
     */
    public void saveData(Context context, JobHandler JobHandler)
    {
        final String strJobs = JobHandler.getData();

        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PREF_Jobs, strJobs);
        editor.commit();
    }


    /**
     * This Method load data for a setting handler.
     * @param context Context of app
     * @param settingsHandler SettingsHandler which is to fill with data
     */
    public void loadData(Context context, SettingsHandler settingsHandler)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final String strSettings = sharedpreferences.getString(PREF_Settings, SettingsHandler.DEFAULT_SETTINGS);

        settingsHandler.setup(strSettings);
    }


    /**
     * This Method store data from a setting handler
     * @param context Context of app
     * @param settingsHandler SettingsHandler which is provide the data
     */
    public void saveData(Context context, SettingsHandler settingsHandler)
    {
        final String strJobs = settingsHandler.getData();

        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PREF_Settings, strJobs);
        editor.commit();
    }
}
