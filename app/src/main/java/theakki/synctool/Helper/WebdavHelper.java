package theakki.synctool.Helper;

import com.owncloud.android.lib.common.network.WebdavEntry;

import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;

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
        return  new FileItem(webdavEntry.name(), webdavEntry.path(), webdavEntry.size(), webdavEntry.modifiedTimestamp(), webdavEntry.contentType());
    }

    public static DavPropertyNameSet getFolderPropSet()
    {
        DavPropertyNameSet propSet = new DavPropertyNameSet();
        propSet.add(DavPropertyName.DISPLAYNAME);

        propSet.add(DavPropertyName.GETCONTENTTYPE);
        propSet.add(DavPropertyName.RESOURCETYPE);
        propSet.add(DavPropertyName.GETCONTENTLENGTH);
        propSet.add(DavPropertyName.GETLASTMODIFIED);
        propSet.add(DavPropertyName.CREATIONDATE);
        propSet.add(DavPropertyName.GETETAG);
        return propSet;
    }

}
