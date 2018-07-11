package theakki.synctool.Job.Settings;

/**
 * Strategies for sync in one way.
 *
 * @author theakki
 * @since 0.1
 */
public enum OneWayStrategy
{
    /** Copy changed files */
    Standard,
    /** Copy changed files, remove not existing files */
    Mirror,
    /** Create Folder with actual Date and copy new Files in it */
    NewFilesInDateFolder,
    /** Create Folder with actual Date and copy all Files in it */
    AllFilesInDateFolder,
    /** Move files in selected folder */
    MoveFiles,
    /** Create Folder with actual Date and move all files in it*/
    MoveFilesInDateFolder
}
