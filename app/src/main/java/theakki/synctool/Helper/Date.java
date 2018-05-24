package theakki.synctool.Helper;

import java.text.SimpleDateFormat;

/**
 * Created by theakki on 30.03.18.
 */

public class Date
{
    public static String getDate()
    {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}
