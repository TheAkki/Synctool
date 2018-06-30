package theakki.synctool.Helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Util class for handle Dates
 * @author theakki
 * @since 0.1
 */
public class Permissions
{
    final static String L_TAG = Permissions.class.getSimpleName();

    private final static String[] EXTERNAL_PERMS_SD = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final static String[] INTERNET_PERMS_INTERNET = {Manifest.permission.INTERNET};
    private final static String[] BOOT_PERMS_INTERNET = {Manifest.permission.RECEIVE_BOOT_COMPLETED};
    private final static int EXTERNAL_REQUEST = 128;


    /**
     * This Method request permissions to access on a SD-Card
     * @param context Context in which the request is done.
     * @return True when success
     */
    public static boolean requestForPermissionSD(Context context)
    {
        if(!(context instanceof Activity))
        {
            Log.d(L_TAG, "Context is not a instance of Activity");
            return false;
        }

        Activity act = (Activity) context;

        boolean isPermissionOn = true;
        final int version = Build.VERSION.SDK_INT;
        if(version >= 23)
        {
            if(!canAccessExternalSd(act))
            {
                isPermissionOn = false;
                ActivityCompat.requestPermissions(act, EXTERNAL_PERMS_SD , EXTERNAL_REQUEST);
            }
        }
        return isPermissionOn;
    }


    /**
     * This Method request permissions to access Internet
     * @param context Context in which the request is done.
     * @return True when success
     */
    public static boolean requestForPermissionInternet(Context context)
    {
        if(!(context instanceof Activity))
        {
            Log.d(L_TAG, "Context is not a instance of Activity");
            return false;
        }

        Activity act = (Activity) context;

        boolean isPermissionOn = true;
        final int version = Build.VERSION.SDK_INT;
        if(version >= 23)
        {
            if(!canAccessInternet(act))
            {
                isPermissionOn = false;
                ActivityCompat.requestPermissions(act, INTERNET_PERMS_INTERNET , EXTERNAL_REQUEST);
            }
        }
        return isPermissionOn;
    }


    /**
     * This Method check if access to internet is possible
     * @param c Activity which is to check
     * @return True when access is possible
     */
    protected static boolean canAccessInternet(Activity c)
    {
        return (hasPermission(c, Manifest.permission.INTERNET));
    }


    /**
     * This Method check if access to SD-Card is possible
     * @param c Activity which is to check
     * @return True when access is possible
     */
    protected static boolean canAccessExternalSd(Activity c)
    {
        return (hasPermission(c, Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    /**
     * This Method check if is possible to get boot finished message
     * @param c Activity which is to check
     * @return True when access is possible
     */
    public static boolean canAccessBootEndMessage(Activity c)
    {
        return (hasPermission(c, Manifest.permission.RECEIVE_BOOT_COMPLETED));
    }


    /**
     * This Method check if a specified Permission is granted
     * @param context Context of Activity which is to check
     * @param Perm Permission which is need
     * @return TTrue when permission is granted
     */
    protected static boolean hasPermission(Activity context, String Perm)
    {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, Perm));
    }
}
