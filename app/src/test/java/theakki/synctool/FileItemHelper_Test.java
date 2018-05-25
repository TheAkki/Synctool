package theakki.synctool;

import org.junit.Test;
import static org.junit.Assert.*;

import theakki.synctool.Helper.FileItemHelper;

public class FileItemHelper_Test
{
    @Test
    public void concat_PathWithFileWith () throws Exception
    {
        final String Path = "/Path/";
        final String File = "/file.png";
        final String expected = "/Path/file.png";
        final String actual = FileItemHelper.concatPath(Path, File);

        assertEquals(expected, actual);
    }

    @Test
    public void concat_PathWithFileWithout () throws Exception
    {
        final String Path = "/Path/";
        final String File = "file.png";
        final String expected = "/Path/file.png";
        final String actual = FileItemHelper.concatPath(Path, File);

        assertEquals(expected, actual);
    }

    @Test
    public void concat_PathWithoutFileWith () throws Exception
    {
        final String Path = "/Path";
        final String File = "/file.png";
        final String expected = "/Path/file.png";
        final String actual = FileItemHelper.concatPath(Path, File);

        assertEquals(expected, actual);
    }

    @Test
    public void concat_PathWithoutFileWithout () throws Exception
    {
        final String Path = "/Path";
        final String File = "file.png";
        final String expected = "/Path/file.png";
        final String actual = FileItemHelper.concatPath(Path, File);

        assertEquals(expected, actual);
    }

}
