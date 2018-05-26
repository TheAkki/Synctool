package theakki.synctool.Job.Settings;

/**
 * Strategy for synchronize in booth directions. (Conflict handling)
 * @author theakki
 * @since 0.1
 */
public enum TwoWayStrategy
{
    /** A wins, B will be overwritten */
    AWins,
    /** B wins, A will be overwritten */
    BWins
}
