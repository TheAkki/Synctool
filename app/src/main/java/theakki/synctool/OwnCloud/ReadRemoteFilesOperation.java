package theakki.synctool.OwnCloud;

import java.util.ArrayList;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.client.methods.PropFindMethod;

import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.network.WebdavEntry;
import com.owncloud.android.lib.common.network.WebdavUtils;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.common.utils.Log_OC;

import theakki.synctool.Helper.WebdavHelper;
import theakki.synctool.Job.FileItem;


/**
 * Remote operation to read files from owncloud server. Inspired by Owncloud - Android library.
 *
 * @author theakki
 */

public class ReadRemoteFilesOperation extends RemoteOperation
{
    public enum SearchObjects
    {
        OnlyFiles,
        OnlyDirs
    }

    private static final String TAG = ReadRemoteFilesOperation.class.getSimpleName();
    private static final int SYNC_READ_TIMEOUT = 40000;
    private static final int SYNC_CONNECTION_TIMEOUT = 5000;

    private String  _strRemotePath;
    private SearchObjects _eSearch;
    private int _iMaxDeep = DavConstants.DEPTH_INFINITY;



    /**
     * Constructor
     *
     * @param remotePath Remote path of the file.
     */
    public ReadRemoteFilesOperation(String remotePath, SearchObjects search)
    {
        _strRemotePath = remotePath;
        _eSearch = search;
    }

    public void Deep(int deep){ _iMaxDeep = deep;   }
    public int Deep(){  return _iMaxDeep;   }


    /**
     * Performs the read operation.
     *
     * @param client Client object to communicate with the remote ownCloud server.
     */
    @Override
    protected RemoteOperationResult run(OwnCloudClient client) {
        PropFindMethod propfind = null;
        RemoteOperationResult result;

        /// take the duty of check the server for the current state of the file there
        try
        {
            // remote request
            propfind = new PropFindMethod(client.getWebdavUri() + WebdavUtils.encodePath(_strRemotePath),
                WebdavUtils.getFilePropSet(),    // PropFind Properties
                _iMaxDeep);
            final int status = client.executeMethod(propfind, SYNC_READ_TIMEOUT, SYNC_CONNECTION_TIMEOUT);

            final boolean isSuccess = (status == HttpStatus.SC_MULTI_STATUS || status == HttpStatus.SC_OK);
            if (isSuccess)
            {
                ArrayList<Object> files = new ArrayList<>();

                // Parse response
                MultiStatus resp = propfind.getResponseBodyAsMultiStatus();
                MultiStatusResponse[] arMultiStatusResponse = resp.getResponses();
                for(MultiStatusResponse oMultiStatusResponse : arMultiStatusResponse)
                {
                    WebdavEntry we = new WebdavEntry(oMultiStatusResponse, client.getWebdavUri().getPath());
                    // Workaround: Because WebdavEntry.isDirectory() did not work
                    switch(_eSearch)
                    {
                        case OnlyDirs:
                            if(we.path().endsWith("/") == false)
                                continue;
                            break;
                        case OnlyFiles:
                            if(we.path().endsWith("/") == true)
                                continue;
                            break;
                    }

                    FileItem remoteFile = WebdavHelper.getFileItem(we);
                    files.add(remoteFile);
                }

                // Result of the operation
                result = new RemoteOperationResult(true, propfind);
                result.setData(files);
            }
            else
            {
                result = new RemoteOperationResult(false, propfind);
                client.exhaustResponse(propfind.getResponseBodyAsStream());
            }

        } catch (Exception e)
        {
            result = new RemoteOperationResult(e);
            e.printStackTrace();
            Log_OC.e(TAG, "Synchronizing  file " + _strRemotePath + ": " + result.getLogMessage(),
                result.getException());
        }
        finally
        {
            if (propfind != null)
                propfind.releaseConnection();
        }
        return result;
    }

}
