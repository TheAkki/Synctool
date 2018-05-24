package theakki.synctool.Helper;

import java.io.File;
import java.util.ArrayList;

import theakki.synctool.Job.FileItem;

public class FileItemHelper
{
    public static ArrayList<FileItem> convertFromObjectArray(ArrayList<Object> objects)
    {
        ArrayList<FileItem> fileItems = new ArrayList<>(objects.size());
        for (Object object : objects) {
            fileItems.add((FileItem)object);
        }

        return fileItems;
    }

    public static String concatPath(String first, String last)
    {
        if(first.endsWith(File.pathSeparator))
        {
            if(last.startsWith(File.pathSeparator))
            {
                return first + last.substring(File.pathSeparator.length());
            }
            else
            {
                return first + last;
            }
        }
        else
        {
            if(last.startsWith(File.pathSeparator))
            {
                return first + last;
            }
            else
            {
                return first + File.pathSeparator + last;
            }
        }
    }
}
