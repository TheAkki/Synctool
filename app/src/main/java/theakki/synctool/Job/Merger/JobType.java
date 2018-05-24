package theakki.synctool.Job.Merger;

/**
 * Define the syncronization job
 * @author theakki
 * @since 0.1
 */
public enum JobType
{
    Nothing,    /** Do nothing */
    Read,       /** Read a file */
    Write,      /** Write a file */
    Delete,     /** Delete a file */
    Move        /** Move a file */
}
