package theakki.synctool.Job.ConnectionTypes;

import android.app.Activity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import theakki.synctool.Helper.FileItemHelper;
import theakki.synctool.Helper.Permissions;
import theakki.synctool.Job.FileItem;
import theakki.synctool.Job.IConnection;

/**
 * Created by theakki on 25.03.18.
 */

public class LocalPath implements IConnection
{
    final private String TAG_Name = "LocalPath";
    final private String TAG_Path = "Path";

    public LocalPath(String Path)
    {
        _Path = Path;
    }

    public LocalPath(Element Node)
    {
        final String name = Node.getNodeName();
        if(name.compareToIgnoreCase(name) != 0)
            throw new IllegalArgumentException("Unexpected Node '" + name + "'");

        NodeList childs = Node.getChildNodes();
        for(int i = 0; i < childs.getLength(); ++i)
        {
            Element child = (Element) childs.item(i);
            final String childName = child.getNodeName();
            if(childName.compareToIgnoreCase(TAG_Path) == 0)
            {
                String path = child.getTextContent();
                if(path == null)
                    throw new IllegalArgumentException("Path Value is null");
                if(path.isEmpty())
                    throw new IllegalArgumentException("Path Value is empty");
                _Path = path;
            }
            else
            {
                throw new IllegalArgumentException("Unexpected Node '" + childName + "'");
            }
        }
    }


    @Override
    public Element getSettings(Document doc) {
        Element root = doc.createElement(TAG_Name);

        Element path = doc.createElement(TAG_Path);
        path.setTextContent(_Path);
        root.appendChild(path);

        return root;
    }


    @Override
    public boolean Move(String SourceFile, String TargetFile) {
        File source = new File(FileItemHelper.concatPath(_Path, SourceFile));
        File target = new File(FileItemHelper.concatPath(_Path, TargetFile));

        return source.renameTo(target);
    }


    @Override
    public boolean Delete(String File)
    {
        File file = new File(FileItemHelper.concatPath(_Path, File));
        return file.delete();
    }


    @Override
    public boolean Read(String SourceFile, File TempFile)
    {
        File source = new File(FileItemHelper.concatPath(_Path, SourceFile));

        try
        {
            copy(source, TempFile);
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
        final String strTargetFilePath = FileItemHelper.concatPath(TargetFile.RelativePath, TargetFile.FileName);

        File target = new File( FileItemHelper.concatPath(_Path, strTargetFilePath)  );
        target.mkdirs();

        try
        {
            copy(SourceFile, target);
        }
        catch(Exception e)
        {
            return false;
        }
        return false;
    }


    private static String getMimeType(File f)
    {
        // For Api 26
        //return Files.probeContentType(f);

        // For Api < 26
        /*
        try {
            Tika tika = new Tika();
            return tika.detect(f);
        }
        catch(Exception e)
        {
            return "";
        }
        */
        // ToDo: Correct
        return "text/plain";
    }


    private  static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }


    static public String getType()
    {
        return "LocalPath";
    }


    @Override
    public String Type()
    {
        return getType();
    }


    @Override
    public boolean IsAvailable() {
        if(_Path.length() == 0)
            return false;
        File f = new File(_Path);
        if(f.exists() == false)
            return false;
        if(f.isDirectory() == false)
            return false;

        return true;
    }


    @Override
    public void Connect(Activity context)
    {
    }


    @Override
    public void Disconnect()
    {
    }


    @Override
    public void RequestPermissions(Activity context)
    {
        Permissions.requestForPermissionSD(context);
    }


    @Override
    public ArrayList<FileItem> getFileList()
    {
        ArrayList<FileItem> result = new ArrayList<>();
        //_Path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SyncTest/A";

        File root = new File(_Path);
        fillArrayList(result, root.listFiles(), _Path);

        return result;
    }


    private void fillArrayList(ArrayList<FileItem> list, File[] Folder, String BasePath)
    {
        if(Folder == null)
            return;

        for(int i = 0; i < Folder.length; ++i)
        {
            File obj = Folder[i];
            if(obj.isDirectory())
            {
                fillArrayList(list, obj.listFiles(), BasePath);
            }
            else
            {
                /* Maybe check here some attributes like link */
                final String Name = obj.getName();
                final String Path = obj.getAbsolutePath();
                final String PathWithoutName = Path.substring(0, Path.length() - Name.length());
                final String PathWithoutNameAndBase = PathWithoutName.substring(BasePath.length());

                FileItem temp = new FileItem(Name, PathWithoutNameAndBase, obj.length(), obj.lastModified(), getMimeType(obj));

                list.add(temp);
            }
        }
    }

    private String _Path;
    public String Path() {return _Path;}
    public void Path(String Path){_Path = Path;}
}
