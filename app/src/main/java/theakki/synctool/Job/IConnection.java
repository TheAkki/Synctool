package theakki.synctool.Job;

import android.app.Activity;
import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;

import theakki.synctool.Data.StringTree;

/**
 * Interface to describe basic method for a connection.
 * @author theakki
 * @since 0.1
 */
public interface IConnection
{
    /**
     * Get the type of Connection
     * @return String of Connection type
     */
    String Type();

    /**
     * Request all necessary permissions for this Connection type
     * @param context Context of Application
     */
    void RequestPermissions(Context context);

    /**
     * Connect, login and prepare Connection
     * @param context Context of Application
     */
    void Connect(Context context);

    /**
     * Disconnect
     */
    void Disconnect();

    /**
     * Get list of files
     * @return List of files
     */
    ArrayList<FileItem> getFileList();

    /**
     * Get Settings to store them remanently
     * @param doc XML Document as base for create nodes
     * @return Node with settings
     */
    Element getJobSettings(Document doc);

    /**
     * Move/Rename a file
     * @param SourceFile Source filename
     * @param TargetFile Target filename
     * @return True when success
     */
    boolean Move(String SourceFile, String TargetFile);

    /**
     * Delete a file
     * @param File Filename
     * @return True when success
     */
    boolean Delete(String File);

    /**
     * Read a file
     * @param SourceFile Filename which is to read
     * @param TempFile Target to store data
     * @return True when success
     */
    boolean Read(String SourceFile, File TempFile);

    /**
     * Write a file
     * @param SourceFile Source data
     * @param TargetFile Target file information
     * @return True when success
     */
    boolean Write(File SourceFile, FileItem TargetFile);


    /**
     * Return a tree with folder names with the given local path as base
     * @return Tree
     */
    StringTree Tree();
}
