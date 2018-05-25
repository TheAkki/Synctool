package theakki.synctool;

import org.junit.Test;

import theakki.synctool.Helper.StringHelper;

import static org.junit.Assert.*;


public class StringHelper_Test
{
    @Test
    public void reduceSpaces_AtStart() throws Exception
    {
        final String expected = "startsWithSpace";
        final String actual = StringHelper.reduceSpaces("   startsWithSpace");

        assertEquals(expected, actual);
    }


    @Test
    public void reduceSpaces_AtEnd() throws Exception
    {
        final String expected = "endsWithSpaces";
        final String actual = StringHelper.reduceSpaces("endsWithSpaces   ");

        assertEquals(expected, actual);
    }

    @Test
    public void reduceSpaces_DoubleSpacesInMiddle() throws Exception
    {
        final String expected = "double Spaces in middle";
        final String actual = StringHelper.reduceSpaces("double  Spaces  in  middle");

        assertEquals(expected, actual);
    }

    @Test
    public void reduceSpaces_AtStartAtEndInMiddle() throws Exception
    {
        final String expected = "at Start, in Middle and at end";
        final String actual = StringHelper.reduceSpaces("  at Start,  in    Middle     and  at end   ");

        assertEquals(expected, actual);
    }
}
