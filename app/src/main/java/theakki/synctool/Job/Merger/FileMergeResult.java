package theakki.synctool.Job.Merger;

import theakki.synctool.Job.FileItem;

/**
 * Class to store doing things. Necessary to synchronize files.
 * @author theakki
 * @since 0.1
 */
public class FileMergeResult
{
    /**
     * Constructor
     */
    public FileMergeResult()
    {
    }


    /**
     * Contructor
     * @param result Result of merging strategy analyse
     * @param fileA FileInformation A
     * @param fileB FileInformation B
     */
    public FileMergeResult(MergeResult result, FileItem fileA, FileItem fileB)
    {
        State = result;
        FileA = fileA;
        FileB = fileB;
    }


    /**
     * Compare one FileMergeResult with an other
     * @param other Other instance
     * @param WithoutFlags Also equal when comparision flags are not equal
     * @return True when booth are equal
     */
    public boolean isEqual(FileMergeResult other, boolean WithoutFlags)
    {
        if(State != other.State)
            return false;

        if(FileA.isEqual(other.FileA, WithoutFlags, false) == false)
            return false;
        if(FileA.isEqual(other.FileB, WithoutFlags, false) == false)
            return false;

        return true;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof FileMergeResult)
        {
            FileMergeResult o = (FileMergeResult)obj;
            if(State != o.State)
                return false;
            if(FileA.equals(o.FileA) == false)
                return false;
            if(FileB.equals(o.FileB) == false)
                return false;

            return true;
        }
        return false;
    }

    public MergeResult State;
    public FileItem FileA;
    public FileItem FileB;
}
