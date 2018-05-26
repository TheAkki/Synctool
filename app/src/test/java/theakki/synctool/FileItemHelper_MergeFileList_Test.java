package theakki.synctool;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import theakki.synctool.Helper.FileItemHelper;
import theakki.synctool.Job.FileItem;
import theakki.synctool.Job.Merger.FileMergeResult;
import theakki.synctool.Job.Merger.MergeResult;
import theakki.synctool.Job.Settings.OneWayStrategy;
import theakki.synctool.Job.Settings.SyncDirection;

import static org.junit.Assert.*;

public class FileItemHelper_MergeFileList_Test
{
    @Test
    public void merge_MovedSingleA2BStandard() throws Exception
    {
        FileItem file1A = new FileItem("file1.txt", "/path1/", 12345, 987654321);
        FileItem file1B = new FileItem("file1.txt", "/path1/path2/", 12345, 987654321);

        final ArrayList<FileItem> listA = new ArrayList<>(Arrays.asList( file1A ));
        final ArrayList<FileItem> listB = new ArrayList<>(Arrays.asList( file1B ));

        // Da A der Master ist muss A verschoben sein.
        final ArrayList<FileMergeResult> expected = new ArrayList<>(Arrays.asList( new FileMergeResult(MergeResult.Moved_A, file1A.clone(), file1B.clone()) ));
        ArrayList<FileMergeResult> actual = FileItemHelper.mergeFileList(listA, listB, SyncDirection.ToB, OneWayStrategy.Standard);
        FileItemHelper_Test.resetFlagMergeResult(actual);

        assertEquals(expected, actual);
    }

    @Test
    public void merge_MovedSingleB2AStandard() throws Exception
    {
        FileItem file1A = new FileItem("file1.txt", "/path1/", 12345, 987654321);
        FileItem file1B = new FileItem("file1.txt", "/path1/path2/", 12345, 987654321);

        final ArrayList<FileItem> listA = new ArrayList<>(Arrays.asList( file1A ));
        final ArrayList<FileItem> listB = new ArrayList<>(Arrays.asList( file1B ));

        final ArrayList<FileMergeResult> expected = new ArrayList<>(Arrays.asList( new FileMergeResult(MergeResult.Moved_B, file1A.clone(), file1B.clone()) ));
        ArrayList<FileMergeResult> actual = FileItemHelper.mergeFileList(listA, listB, SyncDirection.ToA, OneWayStrategy.Standard);
        FileItemHelper_Test.resetFlagMergeResult(actual);

        assertEquals(expected, actual);
    }

    @Test
    public void merge_EqualSingleA2BStandard() throws Exception
    {
        FileItem file1A = new FileItem("file1.txt", "/path1/", 12345, 987654321);
        FileItem file1B = new FileItem("file1.txt", "/path1/", 12345, 987654321);

        final ArrayList<FileItem> listA = new ArrayList<>(Arrays.asList( file1A ));
        final ArrayList<FileItem> listB = new ArrayList<>(Arrays.asList( file1B ));

        // Nothing to do when equal
        final ArrayList<FileMergeResult> expected = new ArrayList<>();
        ArrayList<FileMergeResult> actual = FileItemHelper.mergeFileList(listA, listB, SyncDirection.ToB, OneWayStrategy.Standard);
        FileItemHelper_Test.resetFlagMergeResult(actual);

        assertEquals(expected, actual);
    }

    @Test
    public void merge_EqualSingleB2AStandard() throws Exception
    {
        FileItem file1A = new FileItem("file1.txt", "/path1/", 12345, 987654321);
        FileItem file1B = new FileItem("file1.txt", "/path1/", 12345, 987654321);

        final ArrayList<FileItem> listA = new ArrayList<>(Arrays.asList( file1A ));
        final ArrayList<FileItem> listB = new ArrayList<>(Arrays.asList( file1B ));

        // Nothing to do when equal
        final ArrayList<FileMergeResult> expected = new ArrayList<>();
        ArrayList<FileMergeResult> actual = FileItemHelper.mergeFileList(listA, listB, SyncDirection.ToA, OneWayStrategy.Standard);
        FileItemHelper_Test.resetFlagMergeResult(actual);

        assertEquals(expected, actual);
    }

    @Test
    public void merge_AlreadySyncA2B_NewDateFolder() throws Exception
    {
        FileItem file1A = new FileItem("file1.txt", "/path1/", 12345, 987654321);
        FileItem file1B = new FileItem("file1.txt", "/path1/Date1/", 12345, 987654321);

        final ArrayList<FileItem> listA = new ArrayList<>(Arrays.asList( file1A ));
        final ArrayList<FileItem> listB = new ArrayList<>(Arrays.asList( file1B ));

        // File allready synced. So nothing to do
        final ArrayList<FileMergeResult> expected = new ArrayList<>();
        ArrayList<FileMergeResult> actual = FileItemHelper.mergeFileList(listA, listB, SyncDirection.ToB, OneWayStrategy.NewFilesInDateFolder);
        FileItemHelper_Test.resetFlagMergeResult(actual);

        assertEquals(expected, actual);
    }

    @Test
    public void merge_NotSyncSyncA2B_NewDateFolder() throws Exception
    {
        FileItem file1A = new FileItem("file1.txt", "/path1/", 12345, 987654321);
        FileItem file1B = new FileItem("file11.txt", "/path1/Date1/", 12345, 987654321);

        final ArrayList<FileItem> listA = new ArrayList<>(Arrays.asList( file1A ));
        final ArrayList<FileItem> listB = new ArrayList<>(Arrays.asList( file1B ));

        // Nothing to do when equal
        final ArrayList<FileMergeResult> expected = new ArrayList<>(Arrays.asList( new FileMergeResult(MergeResult.NewFile, file1A.clone(), null) ));
        ArrayList<FileMergeResult> actual = FileItemHelper.mergeFileList(listA, listB, SyncDirection.ToB, OneWayStrategy.NewFilesInDateFolder);
        FileItemHelper_Test.resetFlagMergeResult(actual);

        assertEquals(expected, actual);
    }
}
