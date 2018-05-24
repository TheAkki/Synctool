package theakki.synctool.Job.Settings;

/**
 * Strategies for sync in one way.
 *
 * @author theakki
 * @since 0.1
 */
public enum OneWayStrategy
{
    Standard,               /** Copy changed files */
    Mirror,                 /** Copy changed files, remove not existing files */
    NewFilesInDateFolder,   /** Create Folder with actual Date and copy new Files in it */
    AllFilesInDateFolder    /** Create Folder with actual Date and copy all Files in it */
}
