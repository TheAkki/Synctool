package theakki.synctool.Helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import static java.lang.Thread.sleep;

/**
 * Created by theakki on 03.04.18.
 */

public class TestEnvironmentHelper
{

    // Sync A <-> B; Master B
    public static void createSetup1()
    {
        deleteRecursive(BasePath);
        FolderA.mkdirs();
        FolderB.mkdirs();

        // New File
        File NewFile = new File(FolderA.getAbsolutePath() + "/SubFolder1/NewFile.txt");
        createDirs(NewFile);
        fillFile(NewFile, "New File Content");

        // Renamed File
        File SourceRenamed = new File(FolderB.getAbsolutePath() + "/Subfolder2/SourceRenamed.txt");
        createDirs(SourceRenamed);
        fillFile(SourceRenamed, "File which is renamed later");
        File Renamed = new File(FolderA.getAbsolutePath() + "/Subfolder2/Renamed.txt");
        createDirs(Renamed);
        copy(SourceRenamed, Renamed);

        // Moved File
        File SourceMoved = new File(FolderB.getAbsolutePath() + "/MovedSource/Moved.txt");
        createDirs(SourceMoved);
        fillFile(SourceMoved, "File which is moved later");
        File Moved = new File(FolderA.getAbsolutePath() + "/Moved/Moved.txt");
        createDirs(Moved);
        copy(SourceMoved, Moved);

        // Equal File
        File SourceEqual = new File(FolderB.getAbsolutePath() + "/Sub/Equal.txt");
        createDirs(SourceEqual);
        fillFile(SourceEqual, "Fill with equal Content");
        File Equal = new File(FolderA.getAbsolutePath() + "/Sub/Equal.txt");
        createDirs(Equal);
        copy(SourceEqual, Equal);

        // Date Changed A newer
    /*
        File SourceDataA = new File (FolderB.getAbsolutePath() + "/SubFolder3/DateA.txt");
        createDirs(SourceDataA);
        fillFile(SourceDataA, "Content of File which is updated later");
        File DataA = new File(FolderA.getAbsolutePath() + "/SubFolder3/DataA.txt");
        createDirs(DataA);
        fillFile(DataA, "New Content");
        setModifiedDate(DataA, SourceDataA.lastModified() - 50000);
    */
    }


    private static void createDirs(File file)
    {
        String filename = file.getAbsolutePath();
        File path = new File(filename.substring(0, filename.length() - file.getName().length()));
        path.mkdirs();
    }

    private static boolean setModifiedDate(File file, long time)
    {

        // Alte Api

        boolean b = file.setLastModified(time);
        return b;

        // Neue Api: Api 26
        /*
        try
        {
            Files.setLastModifiedTime(file.toPath(), FileTime.fromMillis(time));
        } catch(IOException e)
        {
            e.printStackTrace();
        }
        */
    }

    private static void deleteRecursive(File fileOrDirectory)
    {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    private static void copy(File src, File dst)
    {
        try (InputStream in = new FileInputStream(src))
        {
            try (OutputStream out = new FileOutputStream(dst))
            {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
            }
            in.close();
        }
        catch(Exception e)
        {

        }
    }


    private static  void fillFile(File file, String Content)
    {
        try
        {
            if(file.exists() == false)
            {
                file.createNewFile();
            }

            FileOutputStream fstream = new FileOutputStream(file.toString());
            OutputStreamWriter streamWriter = new OutputStreamWriter(fstream);

            streamWriter.write(Content);
            streamWriter.flush();
            streamWriter.close();
            fstream.flush();
            fstream.close();
        }
        catch(FileNotFoundException e)
        {
            // do nothing
            e.printStackTrace();
        }
        catch(IOException e)
        {
            // do nothing
            e.printStackTrace();
        }
    }

    final static private File BasePath = new File( "/sdcard/SyncTest/");
    final static private File FolderA = new File( BasePath.toString() + "/a/"  );
    final static private File FolderB = new File( BasePath.toString() + "/b/"  );

}
