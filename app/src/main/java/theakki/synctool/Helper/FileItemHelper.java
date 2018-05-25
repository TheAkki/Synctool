package theakki.synctool.Helper;

import java.io.File;
import java.util.ArrayList;

import theakki.synctool.Job.FileItem;

/**
 * Util class for class FileItem
 * @author theakki
 * @since 0.1
 */
public class FileItemHelper
{
    /**
     * This Method convert an array list of objects to array list of FileItem.
     * It will not checked if object is a FileItem.
     * @param objects Array list of objects
     * @return Array list of FileItem
     */
    public static ArrayList<FileItem> convertFromObjectArray(ArrayList<Object> objects)
    {
        ArrayList<FileItem> fileItems = new ArrayList<>(objects.size());
        for (Object object : objects)
        {
            fileItems.add((FileItem)object);
        }

        return fileItems;
    }


    /**
     * Concat two parts of a path. It's also adjust the necessary seperator
     * @param first First part of path
     * @param last  Next part of path
     * @return Concated path
     */
    public static String concatPath(String first, String last)
    {
        if(first.endsWith(File.separator))
        {
            if(last.startsWith(File.separator))
            {
                return first + last.substring(File.separator.length());
            }
            else
            {
                return first + last;
            }
        }
        else
        {
            if(last.startsWith(File.separator))
            {
                return first + last;
            }
            else
            {
                return first + File.separator + last;
            }
        }
    }
}
