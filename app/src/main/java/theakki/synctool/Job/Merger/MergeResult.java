package theakki.synctool.Job.Merger;

/**
 * Result after merging
 * @author theakki
 * @since 0.1
 */

public enum MergeResult
{
    /** Equal: Folder, Name, Time, Size */
    MatchExactly,
    /** Equal: Folder, Name, Size */
    DateChanged_ANewer,
    /** Equal: Folder, Name, Size */
    DateChanged_BNewer,
    /** Equal: Folder, Time, Size */
    Renamed_A,
    /** Equal: Folder, Time, Size */
    Renamed_B,
    /** Equal: Name, Time, Size */
    Moved_A,
    /** Equal: Name, Time, Size */
    Moved_B,
    /** Equal: Nothing */
    NewFile,
    /** Equal: Folder, Name, Time (Different size, but same time ???) */
    NotRelatedFile,
    /** Equal: Folder, Name, Time (???) */
    SizeChanged
}
