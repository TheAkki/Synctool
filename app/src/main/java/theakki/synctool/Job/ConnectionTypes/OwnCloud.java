package theakki.synctool.Job.ConnectionTypes;

import android.app.Activity;
import android.net.Uri;

import org.apache.jackrabbit.webdav.DavConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;

import theakki.synctool.Helper.FileItemHelper;
import theakki.synctool.Job.FileItem;
import theakki.synctool.Job.IConnection;
import theakki.synctool.OwnCloud.ReadRemoteFilesOperation;

import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientFactory;
import com.owncloud.android.lib.common.authentication.OwnCloudCredentialsFactory;
import com.owncloud.android.lib.common.network.NetworkUtils;
import com.owncloud.android.lib.common.operations.OnRemoteOperationListener;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.resources.files.DownloadRemoteFileOperation;
import com.owncloud.android.lib.resources.files.ReadRemoteFolderOperation;
import com.owncloud.android.lib.resources.files.RemoveRemoteFileOperation;

/**
 * Created by theakki on 29.03.18.
 */

public class OwnCloud extends StoredBase implements OnRemoteOperationListener, IConnection
{
    final private String TAG_NAME = "OwnCloud";
    private OwnCloudClient _Client;


    public OwnCloud(Element Node)
    {
        final String name = Node.getNodeName();
        if(name.compareToIgnoreCase(TAG_NAME) != 0)
            throw new IllegalArgumentException("Unexpected Node name '" + name + "'");

        // Defaultwert für OwnCloud
        _Port = 443;    // https

        NodeList childs = Node.getChildNodes();
        for(int i = 0; i < childs.getLength(); ++i)
        {
            // work with locale Nodes

            // base nodes
            setValue( (Element)childs.item(i) );
        }
    }


    public OwnCloud(String connectionName, String localPath)
    {
        _ConnectionName = connectionName;
        _LocalPath = localPath;
        _Port = 443;
    }


    @Override
    public Element getSettings(Document doc)
    {
        Element root = doc.createElement(TAG_NAME);
        appendSettings(doc, root);

        return root;
    }


    @Override
    public boolean Move(String SourceFile, String TargetFile)
    {
        return false;
    }


    @Override
    public boolean Delete(String File)
    {
        RemoveRemoteFileOperation delete = new RemoveRemoteFileOperation( FileItemHelper.concatPath(_LocalPath, File));
        RemoteOperationResult result = delete.execute(_Client);
        return result.isSuccess();
    }


    @Override
    public boolean Read(String SourceFile, File TempFile) {
        DownloadRemoteFileOperation download = new DownloadRemoteFileOperation(FileItemHelper.concatPath(_LocalPath, SourceFile), TempFile.getAbsolutePath());
        RemoteOperationResult result = download.execute(_Client);
        return result.isSuccess();
    }


    @Override
    public boolean Write(File File, String TargetFile) {
        return false;
    }


    static public final String getType()
    {
        return "OwnCloud";
    }


    @Override
    public String Type()
    {
        return getType();
    }


    @Override
    public void RequestPermissions(Activity context)
    {

    }


    @Override
    public boolean IsAvailable()
    {
        ReadRemoteFilesOperation read = new ReadRemoteFilesOperation("/", ReadRemoteFilesOperation.SearchObjects.OnlyFiles);
        read.Deep(DavConstants.DEPTH_0);
        RemoteOperationResult result = read.execute(_Client);

        return result.isSuccess();
    }


    @Override
    public void Connect(Activity context)
    {
        _Client = new OwnCloudClient(Uri.parse(Url()), NetworkUtils.getMultiThreadedConnManager());
        _Client.setDefaultTimeouts(
                OwnCloudClientFactory.DEFAULT_DATA_TIMEOUT,
                OwnCloudClientFactory.DEFAULT_CONNECTION_TIMEOUT);
        _Client.setCredentials(
                OwnCloudCredentialsFactory.newBasicCredentials(
                        User(),
                        Password()
                )
        );
        _Client.setContext(context);

        ReadRemoteFilesOperation read = new ReadRemoteFilesOperation("/", ReadRemoteFilesOperation.SearchObjects.OnlyFiles);
        RemoteOperationResult res =  read.execute(_Client);

        res.getData();
    }


    @Override
    public void onRemoteOperationFinish(RemoteOperation operation, RemoteOperationResult result) {
        if (!result.isSuccess())
        {
            return ;
        } /*
        else if (operation instanceof ReadRemoteFolderOperation) {
            onSuccessfulRefresh((ReadRemoteFolderOperation)operation, result);

        } /else if (operation instanceof UploadRemoteFileOperation) {
            onSuccessfulUpload((UploadRemoteFileOperation)operation, result);

        } else if (operation instanceof RemoveRemoteFileOperation) {
            onSuccessfulRemoteDeletion((RemoveRemoteFileOperation)operation, result);

        } else if (operation instanceof DownloadRemoteFileOperation) {
            onSuccessfulDownload((DownloadRemoteFileOperation)operation, result);

        } else {
            Toast.makeText(this, R.string.todo_operation_finished_in_success, Toast.LENGTH_SHORT).show();
        }*/
    }


    private void onSuccessfulRefresh(ReadRemoteFolderOperation operation, RemoteOperationResult result)
    {
        boolean b = true;

    }


    @Override
    public void Disconnect()
    {

    }


    @Override
    public ArrayList<FileItem> getFileList()
    {
        ReadRemoteFilesOperation read = new ReadRemoteFilesOperation(_LocalPath, ReadRemoteFilesOperation.SearchObjects.OnlyFiles);
        RemoteOperationResult result = read.execute(_Client);

        return FileItemHelper.convertFromObjectArray(result.getData());
    }
}
