package theakki.synctool.Job.ConnectionTypes;

import android.content.Context;

import theakki.synctool.Data.StringTree;
import theakki.synctool.Helper.FileItemHelper;
import theakki.synctool.Job.FileItem;
import theakki.synctool.Job.IConnection;


import org.apache.commons.net.ftp.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * This class handle ftp connections
 * @author theakki
 * @since 0.1
 */
public class FTPConnection extends StoredBase implements IConnection

{
    private FTPClient _Connection = new FTPClient();
    final private String TAG_NAME = "FTP";

    public static final int iDEF_PORT = 21;


    /**
     * Create a ftp connection from XML node
     * @param Node XML Node
     */
    public FTPConnection(Element Node)
    {
        loadJobSettings(Node);
    }


    /**
     * Constructor to create a FTP Connection
     * @param connectionName NamedConnection
     * @param remotePath Path on the Connection
     */
    public FTPConnection(String connectionName, String remotePath)
    {
        _ConnectionName = connectionName;
        _LocalPath = remotePath;
    }


    /**
     * Load Settings from a XML Node
     * @param Node XML Node with Content
     */
    public void loadJobSettings(Element Node)
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


    @Override
    public Element getJobSettings(Document doc)
    {
        Element root = doc.createElement(TAG_NAME);
        appendSettings(doc, root);

        return root;
    }


    @Override
    public boolean Move(String SourceFile, String TargetFile)
    {
        String folder = TargetFile.substring(0, TargetFile.lastIndexOf(File.separator));
        boolean bFoldersCreated = createFolderStructure(folder);
        if(bFoldersCreated == false)
            return false;

        try
        {
            return _Connection.rename(  FileItemHelper.concatPath(_LocalPath, SourceFile),
                                        FileItemHelper.concatPath(_LocalPath, TargetFile));
        }
        catch(Exception e)
        {
            return false;
        }
    }


    @Override
    public boolean Delete(String File)
    {
        try
        {
            return _Connection.deleteFile(FileItemHelper.concatPath(_LocalPath, File));
        }
        catch(Exception e)
        {
            return false;
        }
    }


    @Override
    public boolean Read(String SourceFile, File TempFile)
    {
        final String remoteFile = FileItemHelper.concatPath(_LocalPath, SourceFile);

        try
        {
            OutputStream output = new FileOutputStream( TempFile );
            boolean bDownloaded = _Connection.retrieveFile(remoteFile, output);
            if(bDownloaded == false)
                return false;
        }
        catch(Exception e)
        {
            return false;
        }
        return true;
    }


    @Override
    public boolean Write(File SourceFile, FileItem TargetFile)
    {
        final String relativeFile = FileItemHelper.concatPath(TargetFile.RelativePath, TargetFile.FileName);

        boolean bFoldersCreated = createFolderStructure(TargetFile.RelativePath);
        if(bFoldersCreated == false)
            return false;

        final String remoteFile = FileItemHelper.concatPath(_LocalPath, relativeFile);
        final String modTime = getDateString(TargetFile.Modified);

        try
        {
            InputStream input = new FileInputStream(SourceFile);
            boolean uploaded = _Connection.storeFile(remoteFile, input);
            if(uploaded == false)
                return false;

            boolean timeChanged = _Connection.setModificationTime(remoteFile, modTime);
            if(timeChanged == false)
                return false;
        }
        catch(Exception e)
        {
            return false;
        }

        return true;
    }


    /**
     * This method read all folders on local device. This is done recursive
     * @param parent Parent Tree node
     * @param items List of items in this folder
     */
    private void fillTreeList(StringTree parent, FTPFile[] items)
    {
        for(FTPFile file : items)
        {
            final String folder = file.getName();
            StringTree child = new StringTree( folder );

            try
            {
                _Connection.changeWorkingDirectory(folder);
                FTPFile[] subDirs = _Connection.listDirectories();
                fillTreeList(child, subDirs);
                _Connection.changeToParentDirectory();
                parent.add(child);
            }
            catch(Exception e)
            {
                return;
            }
        }
    }

    @Override
    public StringTree Tree()
    {
        StringTree result = new StringTree("");

        try
        {
            _Connection.changeWorkingDirectory("/");
            fillTreeList(result, _Connection.listDirectories());
            _Connection.changeWorkingDirectory(_LocalPath);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * Method convert a timestamp (in milliseconds) into an time string which is understand by FTP Client
     * @param time Timestamp in milliseconds
     * @return Time string
     */
    public static String getDateString(long time)
    {
        // see MFMT feature of server....
        return new SimpleDateFormat("yyyyMMddHHmmss").format(time);
    }


    /**
     * This method create a folder structure on ftp side
     * @param path Path without filename
     * @return True when success
     */
    protected boolean createFolderStructure(String path)
    {
        return createFolderStructure( FileItemHelper.splittPath(path) );
    }


    /**
     * This method create a folder structure on ftp side
     * @param folders Array of folder names. The folders are created in the given order.
     * @return True when success
     */
    protected boolean createFolderStructure(String[] folders)
    {
        try {

            for(String folder : folders)
            {
                _Connection.makeDirectory(folder);
                _Connection.changeWorkingDirectory(folder);
            }

            for(int i = 0; i < folders.length; ++i) {
                _Connection.changeToParentDirectory();
            }
        }
        catch(Exception e)
        {
            return false;
        }

        return true;
    }


    /**
     * This method return the type of this Connection as String
     * @return "FTP"
     */
    static public String getType()
    {
        return "FTP";
    }


    @Override
    public String Type()
    {
        return getType();
    }


    /**
     * This method check if a FTP server is available
     * @param connection Connection string (IP, url)
     * @param port Port for ftp server
     * @param tls Use tls for connection (actually not used)
     * @return True when available
     */
    public static boolean isAvailable(String connection, int port, boolean tls)
    {
        FTPClient client = new FTPClient();
        try
        {
            client.connect(connection, port);
            boolean bSuccess =  client.isConnected();
            client.isConnected();
            return  bSuccess;
        }
        catch(Exception e)
        {
            return false;
        }
    }


    /**
     * This method check if a FTP server available and the user are valid
     * @param connection Connection string (IP, url)
     * @param port Port for ftp server
     * @param tls Use tls for connection (actually not used)
     * @param username Username
     * @param password Password
     * @return True when available and login is possible
     */
    public static boolean IsAccessible(String connection, int port, boolean tls, String username, String password)
    {
        FTPClient client = new FTPClient();
        try
        {
            client.connect(connection, port);
            boolean bConnected = client.isConnected();
            if(bConnected == false)
            {
                client.disconnect();
                return false;
            }
            boolean bAccess = client.login(username, password);
            if(bAccess)
                client.logout();
            client.disconnect();
            return bAccess;
        }
        catch(Exception e)
        {
            return false;
        }
    }


    @Override
    public void Connect(Context context)
    {
        try{
            _Connection.connect(Url(), Port());
            boolean isConnected = _Connection.isConnected();
            if(isConnected == false)
                throw new IllegalStateException("No connection possible");

            boolean isLogIn = _Connection.login(User(), Password());
            if(isLogIn == false)
                throw new IllegalStateException("LogIn not possible");

            boolean isCdPossible = _Connection.changeWorkingDirectory(_LocalPath);
            if(isCdPossible == false)
                throw new IllegalAccessException("Path not changeable");

            boolean isBinary = _Connection.setFileType(FTP.BINARY_FILE_TYPE);
            if(isBinary == false)
                throw new RuntimeException("Not possible to set binary mode");
        }
        catch(IOException e)
        {
            e.printStackTrace();
            //throw e;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            //throw e;
        }

    }


    @Override
    public void Disconnect()
    {
        try
        {
            _Connection.disconnect();
        }
        catch(Exception e)
        {
        }
    }


    @Override
    public ArrayList<FileItem> getFileList()
    {
        ArrayList<FileItem> result = new ArrayList<>();
        try
        {
            FTPFile[] ftpFiles = _Connection.listFiles();
            fillArrayList(result, ftpFiles, "/");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * This method read all files on ftp server. This is done recursive
     * @param result List of all items
     * @param files List of items in this folder
     * @param relativePath String to store actual path. Information to store relative path in result
     */
    private void fillArrayList(ArrayList<FileItem> result, FTPFile[] files, String relativePath)
    {
        for(FTPFile file : files)
        {
            if(file.isDirectory())
            {
                final String folder = file.getName();
                final String path = FileItemHelper.concatPath(relativePath, folder);
                try
                {
                    _Connection.changeWorkingDirectory( folder );
                    FTPFile[] subFiles = _Connection.listFiles();
                    fillArrayList(result, subFiles, path);
                    _Connection.changeToParentDirectory();
                }
                catch(Exception e)
                {
                    return;
                }
            }
            else if(file.isFile())
            {
                Calendar modTime = file.getTimestamp();
                FileItem item = new FileItem(file.getName(), relativePath, file.getSize(), modTime.getTimeInMillis());
                result.add(item);
            }
        }
    }



    @Override
    public void RequestPermissions(Context context)
    {
    }
}
