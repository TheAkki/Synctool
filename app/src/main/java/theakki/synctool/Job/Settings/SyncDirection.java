package theakki.synctool.Job.Settings;

/**
 * Syncdirection
 * @author theakki
 * @since 0.1
 */

public enum SyncDirection
{
    ToA,    /** Sync from B (Source) to A (Target) */
    ToB,    /** Sync from A (Source) to B (Target) */
    Booth   /** Sync in booth directions */
}
