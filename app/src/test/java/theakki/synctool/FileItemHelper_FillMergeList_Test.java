package theakki.synctool;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import theakki.synctool.Helper.FileItemHelper;
import theakki.synctool.Job.FileItem;
import theakki.synctool.Job.Merger.FileMergeResult;
import theakki.synctool.Job.Merger.MergeResult;

public class FileItemHelper_FillMergeList_Test
{
    @Test
    public void fill_IsEqual_A2B_NoSkipEqual() throws Exception
    {
        FileItem file1A = new FileItem("file1.txt", "/path1/", 12345, 987654321);
        FileItem file1B = new FileItem("file1.txt", "/path1/", 12345, 987654321);

        final ArrayList<FileItem> listA = new ArrayList<>(Arrays.asList( file1A ));
        final ArrayList<FileItem> listB = new ArrayList<>(Arrays.asList( file1B ));

        /* Expected Result: We found exactly the needed file */
        final ArrayList<FileMergeResult> expected = new ArrayList<>(Arrays.asList( new FileMergeResult(MergeResult.MatchExactly, file1A.clone(), file1B.clone()) ));
        ArrayList<FileMergeResult> actual= new ArrayList<>();

        FileItemHelper.fillMergeList(listA, listB, actual, false, false, true, false);
        FileItemHelper_Test.resetFlagMergeResult(actual);
        assertEquals(expected, actual);
    }


    @Test
    public void fill_IsEqual_B2A_NoSkipEqual() throws Exception
    {
        FileItem file1A = new FileItem("file1.txt", "/path1/", 12345, 987654321);
        FileItem file1B = new FileItem("file1.txt", "/path1/", 12345, 987654321);

        final ArrayList<FileItem> listA = new ArrayList<>(Arrays.asList( file1A ));
        final ArrayList<FileItem> listB = new ArrayList<>(Arrays.asList( file1B ));

        /* Expected Result: We found exactly the needed file */
        final ArrayList<FileMergeResult> expected = new ArrayList<>(Arrays.asList( new FileMergeResult(MergeResult.MatchExactly, file1B.clone(), file1A.clone()) ));
        ArrayList<FileMergeResult> actual= new ArrayList<>();

        FileItemHelper.fillMergeList(listB, listA, actual, false, false, false, false);
        FileItemHelper_Test.resetFlagMergeResult(actual);
        assertEquals(expected, actual);
    }


    @Test
    public void fill_IsEqual_A2B_SkipEqual() throws Exception
    {
        FileItem file1A = new FileItem("file1.txt", "/path1/", 12345, 987654321);
        FileItem file1B = new FileItem("file1.txt", "/path1/", 12345, 987654321);

        final ArrayList<FileItem> listA = new ArrayList<>(Arrays.asList( file1A ));
        final ArrayList<FileItem> listB = new ArrayList<>(Arrays.asList( file1B ));

        /* Expected Result: We found nothing. Because we skip equal */
        final ArrayList<FileMergeResult> expected = new ArrayList<>();
        ArrayList<FileMergeResult> actual= new ArrayList<>();

        FileItemHelper.fillMergeList(listA, listB, actual, true, false, true, false);
        FileItemHelper_Test.resetFlagMergeResult(actual);
        assertEquals(expected, actual);
    }


    @Test
    public void fill_IsEqual_B2A_SkipEqual() throws Exception
    {
        FileItem file1A = new FileItem("file1.txt", "/path1/", 12345, 987654321);
        FileItem file1B = new FileItem("file1.txt", "/path1/", 12345, 987654321);

        final ArrayList<FileItem> listA = new ArrayList<>(Arrays.asList( file1A ));
        final ArrayList<FileItem> listB = new ArrayList<>(Arrays.asList( file1B ));

        /* Expected Result: We found nothing. Because we skip equal */
        final ArrayList<FileMergeResult> expected = new ArrayList<>();
        ArrayList<FileMergeResult> actual= new ArrayList<>();

        FileItemHelper.fillMergeList(listB, listA, actual, true, false, false, false);
        FileItemHelper_Test.resetFlagMergeResult(actual);
        assertEquals(expected, actual);
    }


    @Test
    public void fill_Moved_A2B() throws Exception
    {
        FileItem file1A = new FileItem("file1.txt", "/path1/", 12345, 987654321);
        FileItem file1B = new FileItem("file1.txt", "/path1/path2/", 12345, 987654321);

        final ArrayList<FileItem> listA = new ArrayList<>(Arrays.asList( file1A ));
        final ArrayList<FileItem> listB = new ArrayList<>(Arrays.asList( file1B ));

        /*  A is master. So B is moved.
            FileA: B
            FileB: A
         */
        final ArrayList<FileMergeResult> expected = new ArrayList<>(Arrays.asList( new FileMergeResult(MergeResult.Moved_B, file1B.clone(), file1A.clone()) ));
        ArrayList<FileMergeResult> actual= new ArrayList<>();

        FileItemHelper.fillMergeList(listA, listB, actual, false, false, true, false);
        FileItemHelper_Test.resetFlagMergeResult(actual);
        assertEquals(expected, actual);
    }


    @Test
    public void fill_Moved_B2A() throws Exception
    {
        FileItem file1A = new FileItem("file1.txt", "/path1/", 12345, 987654321);
        FileItem file1B = new FileItem("file1.txt", "/path1/path2/", 12345, 987654321);

        final ArrayList<FileItem> listA = new ArrayList<>(Arrays.asList( file1A ));
        final ArrayList<FileItem> listB = new ArrayList<>(Arrays.asList( file1B ));

        /*  B is master. So A is moved.
            FileA: A
            FileB: B
         */
        final ArrayList<FileMergeResult> expected = new ArrayList<>(Arrays.asList( new FileMergeResult(MergeResult.Moved_A, file1A.clone(), file1B.clone()) ));
        ArrayList<FileMergeResult> actual= new ArrayList<>();

        FileItemHelper.fillMergeList(listB, listA, actual, false, false, false, false);
        FileItemHelper_Test.resetFlagMergeResult(actual);
        assertEquals(expected, actual);
    }


    @Test
    public void fill_Renamed_A2B() throws Exception
    {
        FileItem file1A = new FileItem("file1.txt", "/path1/", 12345, 987654321);
        FileItem file1B = new FileItem("file2.txt", "/path1/", 12345, 987654321);

        final ArrayList<FileItem> listA = new ArrayList<>(Arrays.asList( file1A ));
        final ArrayList<FileItem> listB = new ArrayList<>(Arrays.asList( file1B ));

        /**
         *  A is Master. So rename B
         *  FileA: B
         *  FileB: A
         */
        final ArrayList<FileMergeResult> expected = new ArrayList<>(Arrays.asList( new FileMergeResult(MergeResult.Renamed_B, file1B.clone(), file1A.clone()) ));
        ArrayList<FileMergeResult> actual= new ArrayList<>();

        FileItemHelper.fillMergeList(listA, listB, actual, false, false, true, false);
        FileItemHelper_Test.resetFlagMergeResult(actual);
        assertEquals(expected, actual);
    }


    @Test
    public void fill_Renamed_B2A() throws Exception
    {
        FileItem file1A = new FileItem("file1.txt", "/path1/", 12345, 987654321);
        FileItem file1B = new FileItem("file2.txt", "/path1/", 12345, 987654321);

        final ArrayList<FileItem> listA = new ArrayList<>(Arrays.asList( file1A ));
        final ArrayList<FileItem> listB = new ArrayList<>(Arrays.asList( file1B ));

        /**
         *  B is Master. So rename A
         *  FileA: A
         *  FileB: B
         */
        final ArrayList<FileMergeResult> expected = new ArrayList<>(Arrays.asList( new FileMergeResult(MergeResult.Renamed_A, file1A.clone(), file1B.clone()) ));
        ArrayList<FileMergeResult> actual= new ArrayList<>();

        FileItemHelper.fillMergeList(listB, listA, actual, false, false, false, false);
        FileItemHelper_Test.resetFlagMergeResult(actual);
        assertEquals(expected, actual);
    }


}
