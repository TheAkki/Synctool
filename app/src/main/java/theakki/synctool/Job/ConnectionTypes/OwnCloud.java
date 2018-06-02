package theakki.synctool.Job.ConnectionTypes;

import android.app.Activity;
import android.net.Uri;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;

import theakki.synctool.Data.StringTree;
import theakki.synctool.FromOwnCloud.GetServerInfoOperation;
import theakki.synctool.Helper.FileItemHelper;
import theakki.synctool.Job.FileItem;
import theakki.synctool.Job.IConnection;
import theakki.synctool.OwnCloud.ReadRemoteFilesOperation;

import com.owncloud.android.lib.common.OwnCloudAccount;
import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientFactory;
import com.owncloud.android.lib.common.OwnCloudClientManagerFactory;
import com.owncloud.android.lib.common.authentication.OwnCloudCredentials;
import com.owncloud.android.lib.common.authentication.OwnCloudCredentialsFactory;
import com.owncloud.android.lib.common.network.NetworkUtils;
import com.owncloud.android.lib.common.operations.OnRemoteOperationListener;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.resources.files.CreateRemoteFolderOperation;
import com.owncloud.android.lib.resources.files.DownloadRemoteFileOperation;
import com.owncloud.android.lib.resources.files.MoveRemoteFileOperation;
import com.owncloud.android.lib.resources.files.ReadRemoteFolderOperation;
import com.owncloud.android.lib.resources.files.RemoveRemoteFileOperation;
import com.owncloud.android.lib.resources.files.UploadRemoteFileOperation;
import com.owncloud.android.lib.resources.users.GetRemoteUserInfoOperation;

/**
 * This class handle OwnCloud files
 * @author theakki
 * @since 0.1
 */
public class OwnCloud extends StoredBase implements OnRemoteOperationListener, IConnection
{
    final private String TAG_NAME = "OwnCloud";
    private OwnCloudClient _Client;

    public static int iDEF_PORT = 443;

    /**
     * Create a owncloud connection from XML node
     * @param Node XML Node
     */
    public OwnCloud(Element Node)
    {
        final String name = Node.getNodeName();
        if(name.compareToIgnoreCase(TAG_NAME) != 0)
            throw new IllegalArgumentException("Unexpected Node name '" + name + "'");

        NodeList childs = Node.getChildNodes();
        for(int i = 0; i < childs.getLength(); ++i)
        {
            // work with locale Nodes

            // base nodes
            setValue( (Element)childs.item(i) );
        }
    }


    /**
     * Constructor to create a FTP Connection
     * @param connectionName NamedConnection
     * @param remotePath Path on the Connection
     */
    public OwnCloud(String connectionName, String remotePath)
    {
        _ConnectionName = connectionName;
        _LocalPath = remotePath;
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
        final String strFullSource = FileItemHelper.concatPath(_LocalPath, SourceFile);
        final String strFullTarget = FileItemHelper.concatPath(_LocalPath, TargetFile);
        final String strPathTarget = strFullTarget.substring(0, strFullTarget.lastIndexOf(File.separator));

        CreateRemoteFolderOperation mkdir = new CreateRemoteFolderOperation(strPathTarget, true);
        RemoteOperationResult resultMkdir = mkdir.execute(_Client);

        if(resultMkdir.isSuccess() == false)
            return false;

        MoveRemoteFileOperation mv = new MoveRemoteFileOperation(strFullSource, strFullTarget, true);
        RemoteOperationResult result = mv.execute(_Client);

        return result.isSuccess();
    }


    @Override
    public boolean Delete(String File)
    {
        RemoveRemoteFileOperation delete = new RemoveRemoteFileOperation( FileItemHelper.concatPath(_LocalPath, File));
        RemoteOperationResult result = delete.execute(_Client);
        return result.isSuccess();
    }


    @Override
    public boolean Read(String SourceFile, File TempFile)
    {
        DownloadRemoteFileOperation download = new DownloadRemoteFileOperation(FileItemHelper.concatPath(_LocalPath, SourceFile), TempFile.getAbsolutePath());
        RemoteOperationResult result = download.execute(_Client);
        return result.isSuccess();
    }


    @Override
    public boolean Write(File file, FileItem TargetFile)
    {
        final String strFullFolderPath = FileItemHelper.concatPath(_LocalPath, TargetFile.RelativePath);
        CreateRemoteFolderOperation mkdir = new CreateRemoteFolderOperation(strFullFolderPath, true);
        RemoteOperationResult resultMkdir = mkdir.execute(_Client);

        // ToDo: No Error when path already exist
        //if(resultMkdir.isSuccess() == false)
        //    return false;

        final String strRemotePath = FileItemHelper.concatPath(strFullFolderPath, TargetFile.FileName);
        UploadRemoteFileOperation upload = new UploadRemoteFileOperation(   file.getAbsolutePath(),
                                                                            strRemotePath,
                                                                            TargetFile.MimeType,
                                                                            getDateFromTimestamp(TargetFile.Modified));
        RemoteOperationResult result = upload.execute(_Client);
        return  result.isSuccess();
    }


    @Override
    public StringTree Tree() {
        StringTree root = new StringTree("");

        ReadRemoteFilesOperation read = new ReadRemoteFilesOperation("/", ReadRemoteFilesOperation.SearchObjects.OnlyDirs);
        RemoteOperationResult result = read.execute(_Client);

        return convertToTree(result.getData());
    }


    /**
     * Convert a ArrayList of FileItem, which are containing paths to a tree
     * @param objects List of paths
     * @return Tree
     */
    private StringTree convertToTree(ArrayList<Object> objects)
    {
        StringTree root = new StringTree("");
        for(Object o : objects)
        {
            FileItem fi = (FileItem)o;
            String[] parts = FileItemHelper.splittPath(fi.RelativePath);
            root.include(parts);
        }
        return root;
    }


    /**
     * This method return the type of this Connection as String
     * @return "OwnCloud"
     */
    static public String getType()
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


    /**
     * This method check if a FTP server is available
     * @param connection Connection string (IP, url)
     * @param context Context of Application
     * @return True when available
     */
    public static boolean isAvailable(String connection, Activity context)
    {
        try {
            Uri uri = Uri.parse(connection);

            OwnCloudAccount acc = new OwnCloudAccount(uri, OwnCloudCredentialsFactory.getAnonymousCredentials());
            OwnCloudClient client = OwnCloudClientManagerFactory.getDefaultSingleton().getClientFor(acc, context);

            GetServerInfoOperation serverInfo = new GetServerInfoOperation(connection, context);
            RemoteOperationResult res = serverInfo.execute(client);
            return res.isSuccess();
        }
        catch(Exception e)
        {
            return false;
        }
    }


    /**
     * This method check if a FTP server available and the user are valid
     * @param connection Connection string (IP, url)
     * @param context Context of Application
     * @param username Username
     * @param password Password
     * @return True when available and login is possible
     */
    public static boolean IsAccessible(String connection, Activity context, String username, String password)
    {
        try {
            OwnCloudCredentials cred = OwnCloudCredentialsFactory.newBasicCredentials(username, password);
            Uri uri = Uri.parse(connection);
            OwnCloudAccount acc = new OwnCloudAccount(uri, cred);
            OwnCloudClient client = OwnCloudClientManagerFactory.getDefaultSingleton().getClientFor(acc, context);
            client.setBaseUri(uri);

            GetRemoteUserInfoOperation op = new GetRemoteUserInfoOperation();
            RemoteOperationResult result = op.execute(client);
            return result.isSuccess();
        }
        catch(Exception e)
        {
            return false;
        }
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

        //ReadRemoteFilesOperation read = new ReadRemoteFilesOperation("/", ReadRemoteFilesOperation.SearchObjects.OnlyFiles);
        //RemoteOperationResult res =  read.execute(_Client);

        //res.getData();
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


    /**
     * Method convert a timestamp (in milliseconds) into an time string which is understand by OwnCloud Client
     * @param timestamp Timestamp in milliseconds
     * @return Time string
     */
    protected static String getDateFromTimestamp(long timestamp)
    {
        return Long.toString(timestamp / 1000);
    }
}
