package theakki.synctool.Job.Merger;

import theakki.synctool.Job.FileItem;

/**
 * Class to store settings for doing on one synchronization side.
 * @author theakki
 * @since 0.1
 */
public class DoingSide
{
    public JobType Type = JobType.Nothing;
    public FileItem File;
    public String Param = "";
    public boolean DeleteAfter = false;
}
