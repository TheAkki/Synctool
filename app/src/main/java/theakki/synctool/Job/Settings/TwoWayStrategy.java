package theakki.synctool.Job.Settings;

/**
 * Strategy for syncronize in booth directions. (Conflict handling)
 * @author theakki
 * @since 0.1
 */
public enum TwoWayStrategy
{
    AWins,  /** A wins, B will be overwritten */
    BWins   /** B wins, A will be overwritten */
}
