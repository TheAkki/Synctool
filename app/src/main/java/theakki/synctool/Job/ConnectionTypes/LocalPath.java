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

import theakki.synctool.Data.StringTree;
import theakki.synctool.Helper.FileItemHelper;
import theakki.synctool.Helper.Permissions;
import theakki.synctool.Job.FileItem;
import theakki.synctool.Job.IConnection;

/**
 * This class handle local files
 * @author theakki
 * @since 0.1
 */
public class LocalPath implements IConnection
{
    final private String TAG_Name = "LocalPath";
    final private String TAG_Path = "Path";


    /**
     * Constructor
     * @param Path Local path as base
     */
    public LocalPath(String Path)
    {
        _Path = Path;
    }


    /**
     * Create a Local path from XML node
     * @param Node XML Node
     */
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

    /**
     * This method read all folders on local device. This is done recursive
     * @param parent Parent Tree node
     * @param items List of items in this folder
     */
    private void fillTreeList(StringTree parent, File[] items)
    {
        if(items == null || items.length == 0)
            return;

        for(int i = 0; i < items.length; ++i)
        {
            File obj = items[i];
            if(obj.isDirectory())
            {
                StringTree folder = new StringTree(obj.getName());
                fillTreeList(folder, obj.listFiles());
                parent.add(folder);
            }
        }
    }


    @Override
    public StringTree Tree()
    {
        StringTree result = new StringTree("");

        File rootFile = new File(_Path);
        fillTreeList(result, rootFile.listFiles());

        return result;
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


    /**
     * Copy one file to an other
     * @param src Source file
     * @param dst Target file
     * @throws IOException
     */
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


    /**
     * This method return the type of this Connection as String
     * @return "LocalPath"
     */
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

        File root = new File(_Path);
        fillArrayList(result, root.listFiles(), _Path);

        return result;
    }


    /**
     * This method read all files on local device. This is done recursive
     * @param result List of all items
     * @param items List of items in this folder
     * @param basePath String to get the base path of the file. Remove that part from full string to get relative path
     */
    private void fillArrayList(ArrayList<FileItem> result, File[] items, String basePath)
    {
        if(items == null)
            return;

        for(int i = 0; i < items.length; ++i)
        {
            File obj = items[i];
            if(obj.isDirectory())
            {
                fillArrayList(result, obj.listFiles(), basePath);
            }
            else
            {
                /* Maybe check here some attributes like link */
                final String Name = obj.getName();
                final String Path = obj.getAbsolutePath();
                final String PathWithoutName = Path.substring(0, Path.length() - Name.length());
                final String PathWithoutNameAndBase = PathWithoutName.substring(basePath.length());

                FileItem temp = new FileItem(Name, PathWithoutNameAndBase, obj.length(), obj.lastModified(), getMimeType(obj));

                result.add(temp);
            }
        }
    }

    private String _Path;
    public String Path() {return _Path;}
    public void Path(String Path){_Path = Path;}
}
