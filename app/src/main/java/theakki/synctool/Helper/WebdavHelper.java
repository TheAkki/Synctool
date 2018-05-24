package theakki.synctool.Helper;

import com.owncloud.android.lib.common.network.WebdavEntry;
import theakki.synctool.Job.FileItem;


public class WebdavHelper
{
    public static FileItem getFileItem(WebdavEntry webdavEntry)
    {
        FileItem result = new FileItem();
        result.FileName = webdavEntry.name();
        result.RelativePath = webdavEntry.path();
        result.Modified = webdavEntry.modifiedTimestamp();
        result.FileSize = webdavEntry.size();
        result.Flag = FileItem.FLAG_UNKNOWN;
        result.MimeType = webdavEntry.contentType();

        return result;
    }

}
