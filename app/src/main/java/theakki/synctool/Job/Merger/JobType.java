package theakki.synctool.Job.Merger;

/**
 * Define the syncronization job
 * @author theakki
 * @since 0.1
 */
public enum JobType
{
    /** Do nothing */
    Nothing,
    /** Read a file */
    Read,
    /** Write a file */
    Write,
    /** Delete a file */
    Delete,
    /** Move a file */
    Move
}
