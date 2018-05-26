package theakki.synctool.Job.Settings;

/**
 * Syncdirection
 * @author theakki
 * @since 0.1
 */

public enum SyncDirection
{
    /** Sync from B (Source) to A (Target) */
    ToA,
    /** Sync from A (Source) to B (Target) */
    ToB,
    /** Sync in booth directions */
    Booth
}
