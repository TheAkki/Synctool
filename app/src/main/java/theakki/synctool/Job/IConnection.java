/**
 * Created by theakki on 25.03.18.
 */

package theakki.synctool.Job;

import android.app.Activity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;

public interface IConnection
{
    String Type();
    void RequestPermissions(Activity context);
    boolean IsAvailable();
    void Connect(Activity context);
    void Disconnect();
    ArrayList<FileItem> getFileList();
    Element getSettings(Document doc);

    boolean Move(String SourceFile, String TargetFile);
    boolean Delete(String File);
    boolean Read(String SourceFile, File TempFile);
    boolean Write(File SourceFile, FileItem TargetFile);
}
