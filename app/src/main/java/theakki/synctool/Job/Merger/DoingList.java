package theakki.synctool.Job.Merger;

/**
 * Class to store doing things. Necessary to synchronize files.
 * @author theakki
 * @since 0.1
 */
public class DoingList
{
    /**
     * Constructor
     * @param sideA Side A
     * @param sideB Side B
     */
    public DoingList(DoingSide sideA, DoingSide sideB)
    {
        SideA = sideA;
        SideB = sideB;
    }


    /**
     * Constructor
     */
    public DoingList()
    {
        SideA = new DoingSide();
        SideB = new DoingSide();
    }

    public DoingSide SideA;
    public DoingSide SideB;
}
