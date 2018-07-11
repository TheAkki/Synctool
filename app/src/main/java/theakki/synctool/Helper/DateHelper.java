package theakki.synctool.Helper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Util class for handle Dates
 * @author theakki
 * @since 0.1
 */
public class DateHelper
{
    /**
     * This Method return a string with actual date in format 'yyyy-MMM-dd'.
     * @return Formated actual date
     */
    public static String getDate()
    {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}
