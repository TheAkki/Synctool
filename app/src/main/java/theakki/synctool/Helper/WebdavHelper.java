package theakki.synctool.Helper;

import com.owncloud.android.lib.common.network.WebdavEntry;
import theakki.synctool.Job.FileItem;

/**
 * Util class for handle WebDav things
 * @author theakki
 * @since 0.1
 */
public class WebdavHelper
{
    /**
     * This Method convert an WebdavEntry into a FileItem
     * @param webdavEntry
     * @return
     */
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
