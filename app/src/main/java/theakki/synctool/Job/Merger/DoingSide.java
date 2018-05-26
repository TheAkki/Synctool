package theakki.synctool.Job.Merger;

/**
 * Class to store settings for doing on one synchronization side.
 * @author theakki
 * @since 0.1
 */
public class DoingSide
{
    public JobType Type = JobType.Nothing;
    public String Filename;
    public String Param = "";
    public long Timestamp = 0;
}
