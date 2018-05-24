package theakki.synctool.Helper;

/**
 * Created by theakki on 28.03.18.
 */
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class Permissions
{
    private final static String[] EXTERNAL_PERMS_SD = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final static String[] INTERNET_PERMS_INTERNET = {Manifest.permission.INTERNET};
    private final static int EXTERNAL_REQUEST = 128;

    public static boolean requestForPermissionSD(Activity context)
    {
        boolean isPermissionOn = true;
        final int version = Build.VERSION.SDK_INT;
        if(version >= 23)
        {
            if(!canAccessExternalSd(context))
            {
                isPermissionOn = false;
                ActivityCompat.requestPermissions(context, EXTERNAL_PERMS_SD , EXTERNAL_REQUEST);
            }
        }
        return isPermissionOn;
    }

    public static boolean requestForPermissionInternet(Activity context)
    {
        boolean isPermissionOn = true;
        final int version = Build.VERSION.SDK_INT;
        if(version >= 23)
        {
            if(!canAccessInternet(context))
            {
                isPermissionOn = false;
                ActivityCompat.requestPermissions(context, INTERNET_PERMS_INTERNET , EXTERNAL_REQUEST);
            }
        }
        return isPermissionOn;
    }


    protected static boolean canAccessInternet(Activity c)
    {
        return (hasPermission(c, Manifest.permission.INTERNET));
    }


    protected static boolean canAccessExternalSd(Activity c)
    {
        return (hasPermission(c, Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }


    protected static boolean hasPermission(Activity context, String Perm)
    {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, Perm));
    }
}
