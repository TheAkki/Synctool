package theakki.synctool;

import android.support.annotation.NonNull;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

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

    @Test
    public void extract_RemoteWithoutSeperatorAtEnd () throws Exception
    {
        final String RemotePath = "/Sonstwo";
        final String FileName = "Filename.jpg";
        final String RelativePath = "/Relativer/Path/";

        final String TestPath = FileItemHelper.concatPath(FileItemHelper.concatPath(RemotePath, RelativePath), FileName );

        final String actual = FileItemHelper.getRelativePath(TestPath, FileName, RemotePath);
        assertEquals(RelativePath, actual);
    }

    @Test
    public void extract_RemoteWithSeperatorAtEnd () throws Exception
    {
        final String RemotePath = "/Sonstwo/";
        final String FileName = "Filename.jpg";
        final String RelativePath = "/Relativer/Path/";

        final String TestPath = FileItemHelper.concatPath(FileItemHelper.concatPath(RemotePath, RelativePath), FileName );

        final String actual = FileItemHelper.getRelativePath(TestPath, FileName, RemotePath);
        assertEquals(RelativePath, actual);
    }


    @Test
    public void convertFromObjectArray_CallWithNull () throws Exception
    {
        final ArrayList<FileItem> actual = FileItemHelper.convertFromObjectArray(null);
        final ArrayList<FileItem> expected = new ArrayList<>();

        assertEquals(expected, actual);
    }


    @Test
    public void convertFromObjectArray_CallEmpty() throws Exception
    {
        final ArrayList<FileItem> actual = FileItemHelper.convertFromObjectArray(new ArrayList<Object>());
        final ArrayList<FileItem> expected = new ArrayList<>();

        assertEquals(expected, actual);
    }


    @Test
    public void convertFromObjectArray_CallValid() throws Exception
    {
        final FileItem item1 = new FileItem("Item1", "Folder1", 123, 567);
        final FileItem item2 = new FileItem("Item2", "Folder2", 1234, 5678);

        final Object[] input = {item1, item2};
        final ArrayList<Object> inputlist = new ArrayList<>(Arrays.asList(input));
        final ArrayList<FileItem> actual = FileItemHelper.convertFromObjectArray(inputlist);


        final FileItem[] expectedArray = {item1, item2};
        final ArrayList<FileItem> expected = new ArrayList<>(Arrays.asList(expectedArray));

        assertEquals(expected, actual);
    }

    @Test
    public void clone_CallNull() throws Exception
    {
        final ArrayList<FileItem> actual = FileItemHelper.clone(null);
        final ArrayList<FileItemHelper> expected = new ArrayList<>();

        assertEquals(expected, actual);
    }


    @Test
    public void clone_CallEmpty() throws Exception
    {
        final ArrayList<FileItem> inputList = new ArrayList<>();
        final ArrayList<FileItem> actual = FileItemHelper.clone(inputList);

        assertNotSame(inputList, actual);
        assertEquals(inputList, actual);
        assertTrue(isEqual(inputList, actual));
    }


    @Test
    public void clone_CallValid() throws Exception
    {
        final FileItem item1 = new FileItem("Item1", "Folder1", 123, 567);
        final FileItem item2 = new FileItem("Item2", "Folder2", 1234, 5678);

        final FileItem[] input = {item1, item2};
        final ArrayList<FileItem> inputList = new ArrayList<>(Arrays.asList(input));

        final ArrayList<FileItem> actual = FileItemHelper.clone(inputList);

        assertNotSame(inputList, actual);
        assertEquals(inputList, actual);
        assertTrue(isEqual(inputList, actual));
    }



    public boolean isEqual(@NonNull ArrayList<FileItem> list1, @NonNull ArrayList<FileItem> list2)
    {
        if(list1.size() != list2.size())
            return false;

        for(int i = 0; i < list1.size(); ++i)
        {
            final FileItem item1 = list1.get(i);
            final FileItem item2 = list2.get(i);

            if(item1.isEqual(item2, false, false) == false)
                return false;
        }

        return true;
    }


    @Test
    public void concatPath_CallNull() throws Exception
    {
        final String actual = FileItemHelper.concatPath(null);
        final String expected = "";

        assertEquals(expected, actual);
    }


    @Test
    public void concatPath_CallEmpty() throws Exception
    {
        final String[] input = {};
        final String actual = FileItemHelper.concatPath(input);
        final String expected = "/";

        assertEquals(expected, actual);
    }


    @Test
    public void concatPath_CallValid() throws Exception
    {
        final String folder1 = "folder1";
        final String folder2 = "folder2";

        final String[] input = {folder1, folder2};
        final String actual = FileItemHelper.concatPath(input);
        final String expected = "/" + folder1 + "/" + folder2;

        assertEquals(expected, actual);
    }


    @Test
    public void splitPath_CallNull() throws Exception
    {
        final String[] actual = FileItemHelper.splittPath(null);
        final String[] expected = {};

        assertArrayEquals(expected, actual);
    }


    @Test
    public void splitPath_CallEmpty() throws Exception
    {
        final String[] actual = FileItemHelper.splittPath("");
        final String[] expected = {""};

        assertArrayEquals(expected, actual);
    }


    @Test
    public void splitPath_CallValid() throws Exception
    {
        final String folder1 = "f1";
        final String folder2 = "f2";
        final String[] expected = {folder1, folder2};

        final String input1 = "/" + folder1 + "/" + folder2;
        final String[] actual1 = FileItemHelper.splittPath(input1);
        final String input2 = "/" + folder1 + "/" + folder2 + "/";
        final String[] actual2 = FileItemHelper.splittPath(input2);



        assertArrayEquals(expected, actual1);
        assertArrayEquals(expected, actual2);
    }


}
