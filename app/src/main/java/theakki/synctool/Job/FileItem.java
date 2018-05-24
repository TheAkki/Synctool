package theakki.synctool.Job;

/**
 * Created by theakki on 27.03.18.
 */

public class FileItem implements Cloneable
{
    final static public int FLAG_UNKNOWN = 0;
    final static public int FLAG_ANALIZED = 1;
    final static public int FLAG_ANALIZED_MATCH_EXACTLY = 2;
    final static public int FLAG_ANALIZED_MATCH_FOLDER = 3;
    final static public int FLAG_ANALIZED_MATCH_FILE = 4;
    final static public int FLAG_ANALIZED_MATCH_OBJECT = 5;

    public int Flag = FLAG_UNKNOWN;
    public String FileName = "";
    public String RelativePath = "";
    public long FileSize = 0;
    public long Modified = 0;
    public String MimeType = "";


    @Override
    protected FileItem clone()
    {
        FileItem result = new FileItem();
        result.Flag = this.Flag;
        result.FileName = this.FileName;
        result.RelativePath = this.RelativePath;
        result.FileSize = this.FileSize;
        result.Modified = this.Modified;
        result.MimeType = this.MimeType;

        return result;
    }
}
