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
    private boolean isEqualWithoutFlag(ArrayList<FileMergeResult> expected, ArrayList<FileMergeResult> actual)
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

    @Test
    public void merge_IsEqualNoSkipEqual() throws Exception
    {
        FileItem file1A = new FileItem("file1.txt", "/path1/", 12345, 987654321);
        FileItem file1B = new FileItem("file1.txt", "/path1/", 12345, 987654321);

        final ArrayList<FileItem> listA_AtoB = new ArrayList<>(Arrays.asList( file1A ));
        final ArrayList<FileItem> listA_BtoA = FileItemHelper.clone( listA_AtoB );
        final ArrayList<FileItem> listB_AtoB = new ArrayList<>(Arrays.asList( file1B ));
        final ArrayList<FileItem> listB_BtoA =  FileItemHelper.clone( listB_AtoB );

        final ArrayList<FileMergeResult> expectedAtoB = new ArrayList<>(Arrays.asList( new FileMergeResult(MergeResult.MatchExactly, file1A, file1B) ));
        final ArrayList<FileMergeResult> expectedBtoA = new ArrayList<>(Arrays.asList( new FileMergeResult(MergeResult.MatchExactly, file1B, file1A) ));

        ArrayList<FileMergeResult> actualAtoB = new ArrayList<>();
        ArrayList<FileMergeResult> actualBtoA = new ArrayList<>();

        FileItemHelper.fillMergeList(listA_AtoB, listB_AtoB, actualAtoB, false, false, true);
        assertTrue( isEqualWithoutFlag(expectedAtoB, actualAtoB) );

        FileItemHelper.fillMergeList(listA_BtoA, listB_BtoA, actualBtoA, false, false, false);
        assertTrue( isEqualWithoutFlag(expectedBtoA, actualBtoA) );
    }

    @Test
    public void merge_IsEqualSkipEqual() throws Exception
    {
        FileItem file1A = new FileItem("file1.txt", "/path1/", 12345, 987654321);
        FileItem file1B = new FileItem("file1.txt", "/path1/", 12345, 987654321);

        final ArrayList<FileItem> listA_AtoB = new ArrayList<>(Arrays.asList( file1A ));
        final ArrayList<FileItem> listA_BtoA = FileItemHelper.clone( listA_AtoB );
        final ArrayList<FileItem> listB_AtoB = new ArrayList<>(Arrays.asList( file1B ));
        final ArrayList<FileItem> listB_BtoA =  FileItemHelper.clone( listB_AtoB );

        final ArrayList<FileMergeResult> expectedAtoB = new ArrayList<>();
        final ArrayList<FileMergeResult> expectedBtoA = new ArrayList<>();

        ArrayList<FileMergeResult> actualAtoB = new ArrayList<>();
        ArrayList<FileMergeResult> actualBtoA = new ArrayList<>();

        FileItemHelper.fillMergeList(listA_AtoB, listB_AtoB, actualAtoB, true, false, true);
        assertTrue( isEqualWithoutFlag(expectedAtoB, actualAtoB) );

        FileItemHelper.fillMergeList(listA_BtoA, listB_BtoA, actualBtoA, true, false, false);
        assertTrue( isEqualWithoutFlag(expectedBtoA, actualBtoA) );
    }


}
