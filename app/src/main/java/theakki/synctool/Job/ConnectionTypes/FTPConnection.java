package theakki.synctool.Job.ConnectionTypes;

import android.app.Activity;

import theakki.synctool.Job.FileItem;
import theakki.synctool.Job.IConnection;

import org.apache.commons.net.ftp.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by theakki on 27.03.18.
 */

public class FTPConnection extends ExternalBase implements IConnection

{
    private FTPClient _Connection = new FTPClient();
    final private String TAG_NAME = "FTP";


    public FTPConnection(Element Node)
    {
        final String name = Node.getNodeName();
        if(name.compareToIgnoreCase(TAG_NAME) != 0)
            throw new IllegalArgumentException("Unexpected Node name '" + name + "'");

        // Defaultwert für FTP
        _Port = 21;

        NodeList childs = Node.getChildNodes();
        for(int i = 0; i < childs.getLength(); ++i)
        {
            // work with locale Nodes

            // base nodes
            setValue( (Element)childs.item(i) );
        }
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
        return false;
    }


    @Override
    public boolean Read(String SourceFile, File TempFile)
    {
        return false;
    }


    @Override
    public boolean Write(File SourceFile, String TargetFile)
    {
        return false;
    }


    static public String getType()
    {
        return "FTP";
    }


    @Override
    public String Type()
    {
        return getType();
    }


    @Override
    public boolean IsAvailable()
    {
        return false;
    }


    @Override
    public void Connect(Activity context)
    {
        Boolean isConnected = false;
        try{
            _Connection.connect(_URL, _Port);
            isConnected = FTPReply.isPositiveCompletion(_Connection.getReplyCode());
        }
        catch(Exception e)
        {
            //;//throw e;
        }
        if(isConnected == false)
            throw new IllegalStateException("No connection possible");
    }


    @Override
    public void Disconnect()
    {

    }


    @Override
    public ArrayList<FileItem> getFileList() {
        return null;
    }


    @Override
    public void RequestPermissions(Activity context)
    {
    }
}
