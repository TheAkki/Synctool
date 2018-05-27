package theakki.synctool.Job.Settings;

/**
 * Syncdirection
 * @author theakki
 * @since 0.1
 */

public enum SyncDirection
{
    /** Sync from B (Source) to A (Target), Meaning B is master */
    ToA,
    /** Sync from A (Source) to B (Target), Meaning A is master*/
    ToB,
    /** Sync in booth directions */
    Booth
}
