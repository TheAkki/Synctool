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


    public MergeResult State;
    public FileItem FileA;
    public FileItem FileB;
}
