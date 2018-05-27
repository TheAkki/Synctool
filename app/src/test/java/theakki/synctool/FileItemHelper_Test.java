package theakki.synctool;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

import theakki.synctool.Helper.FileItemHelper;
import theakki.synctool.Job.FileItem;
import theakki.synctool.Job.Merger.FileMergeResult;

public class FileItemHelper_Test
{
    @Test
    public void concat_PathWithFileWith () throws Exception
    {
        final String Path = "/Path/";
        final String File = "/file.png";
        final String expected = "/Path/file.png";
        final String actual = FileItemHelper.concatPath(Path, File);

        assertEquals(expected, actual);
    }

    @Test
    public void concat_PathWithFileWithout () throws Exception
    {
        final String Path = "/Path/";
        final String File = "file.png";
        final String expected = "/Path/file.png";
        final String actual = FileItemHelper.concatPath(Path, File);

        assertEquals(expected, actual);
    }

    @Test
    public void concat_PathWithoutFileWith () throws Exception
    {
        final String Path = "/Path";
        final String File = "/file.png";
        final String expected = "/Path/file.png";
        final String actual = FileItemHelper.concatPath(Path, File);

        assertEquals(expected, actual);
    }

    @Test
    public void concat_PathWithoutFileWithout () throws Exception
    {
        final String Path = "/Path";
        final String File = "file.png";
        final String expected = "/Path/file.png";
        final String actual = FileItemHelper.concatPath(Path, File);

        assertEquals(expected, actual);
    }

    public static boolean isEqualWithoutFlag(ArrayList<FileMergeResult> expected, ArrayList<FileMergeResult> actual)
    {
        if(expected.size() != actual.size())
            return false;

        for(int i = 0; i < expected.size(); ++i)
        {
            if(expected.get(i).isEqual(actual.get(i), true) == false)
                return false;
        }

        return true;
    }

    public static void resetFlagMergeResult(ArrayList<FileMergeResult> list)
    {
        for(FileMergeResult item : list)
        {
            if(item.FileA != null)
                item.FileA.Flag = FileItem.FLAG_UNKNOWN;
            if(item.FileB != null)
                item.FileB.Flag = FileItem.FLAG_UNKNOWN;
        }
    }

    public static void resetFlagFileItem(ArrayList<FileItem> list)
    {
        for(FileItem file : list)
        {
            file.Flag = FileItem.FLAG_UNKNOWN;
        }
    }

}
