package theakki.synctool;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import theakki.synctool.Job.IncludeExclude.AnalyzeHelper;
import theakki.synctool.Job.IncludeExclude.AnalyzeResult;
import theakki.synctool.Job.IncludeExclude.AnalyzeStrategy;

import static org.junit.Assert.*;



public class AnalyzeHelper_Test
{
    @Test
    public void detect_MimeType() throws Exception
    {
        final ArrayList<String> list = new ArrayList<>(Arrays.asList("Mime: text/plain"));
        final ArrayList<AnalyzeResult> expected = new ArrayList<>(Arrays.asList(new AnalyzeResult(AnalyzeStrategy.MimeType, "text/plain")));
        final ArrayList<AnalyzeResult> actual = AnalyzeHelper.prepareList(list);

        assertEquals(expected, actual);
    }


    @Test
    public void detect_AllFiles() throws Exception
    {
        final ArrayList<String> list = new ArrayList<>(Arrays.asList("*"));
        final ArrayList<AnalyzeResult> expected = new ArrayList<>(Arrays.asList(new AnalyzeResult(AnalyzeStrategy.FileMatch, "")));
        final ArrayList<AnalyzeResult> actual = AnalyzeHelper.prepareList(list);

        assertEquals(expected, actual);
    }


    @Test
    public void detect_FileEnds() throws Exception
    {
        final ArrayList<String> list = new ArrayList<>(Arrays.asList("*.jpg"));
        final ArrayList<AnalyzeResult> expected = new ArrayList<>(Arrays.asList(new AnalyzeResult(AnalyzeStrategy.FileEndsWith, ".jpg")));
        final ArrayList<AnalyzeResult> actual = AnalyzeHelper.prepareList(list);

        assertEquals(expected, actual);
    }


    @Test
    public void detect_NameMatchWithPathSeparator() throws Exception
    {
        final ArrayList<String> list = new ArrayList<>(Arrays.asList("/file.dat"));
        final ArrayList<AnalyzeResult> expected = new ArrayList<>(Arrays.asList(new AnalyzeResult(AnalyzeStrategy.FileNameMatch, "file.dat")));
        final ArrayList<AnalyzeResult> actual = AnalyzeHelper.prepareList(list);

        assertEquals(expected, actual);
    }


    @Test
    public void detect_NameMatch() throws Exception
    {
        final ArrayList<String> list = new ArrayList<>(Arrays.asList("file.jpg"));
        final ArrayList<AnalyzeResult> expected = new ArrayList<>(Arrays.asList(new AnalyzeResult(AnalyzeStrategy.FileNameMatch, "file.jpg")));
        final ArrayList<AnalyzeResult> actual = AnalyzeHelper.prepareList(list);

        assertEquals(expected, actual);
    }


    @Test
    public void detect_PathsEnds() throws Exception
    {
        final ArrayList<String> list = new ArrayList<>(Arrays.asList("*/f.jpg"));
        final ArrayList<AnalyzeResult> expected = new ArrayList<>(Arrays.asList(new AnalyzeResult(AnalyzeStrategy.PathEndsWith, "/f.jpg")));
        final ArrayList<AnalyzeResult> actual = AnalyzeHelper.prepareList(list);

        assertEquals(expected, actual);
    }


    @Test
    public void detect_FileStartsWith() throws Exception
    {
        final ArrayList<String> list = new ArrayList<>(Arrays.asList("f*"));
        final ArrayList<AnalyzeResult> expected = new ArrayList<>(Arrays.asList(new AnalyzeResult(AnalyzeStrategy.FileStartsWith, "f")));
        final ArrayList<AnalyzeResult> actual = AnalyzeHelper.prepareList(list);

        assertEquals(expected, actual);
    }


    @Test
    public void detect_PathStartsWith() throws Exception
    {
        final ArrayList<String> list = new ArrayList<>(Arrays.asList("/f*"));
        final ArrayList<AnalyzeResult> expected = new ArrayList<>(Arrays.asList(new AnalyzeResult(AnalyzeStrategy.PathStartsWith, "/f")));
        final ArrayList<AnalyzeResult> actual = AnalyzeHelper.prepareList(list);

        assertEquals(expected, actual);
    }
}
