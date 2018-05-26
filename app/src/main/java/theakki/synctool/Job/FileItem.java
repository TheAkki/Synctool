package theakki.synctool.Job;

/**
 * Class to store information about a file item
 * @author theakki
 * @since 0.1
 */
public class FileItem implements Cloneable
{
    final static public int FLAG_UNKNOWN = 0;
    final static public int FLAG_ANALYZED = 1;
    final static public int FLAG_ANALYZED_MATCH_EXACTLY = 2;
    final static public int FLAG_ANALYZED_MATCH_FOLDER = 3;
    final static public int FLAG_ANALYZED_MATCH_FILE = 4;
    final static public int FLAG_ANALYZED_MATCH_OBJECT = 5;


    /**
     * Constructor
     * @param name File name
     * @param relativePath Relative path
     * @param size File size
     * @param modifiedAt File last modified at
     */
    public FileItem(String name, String relativePath, long size, long modifiedAt)
    {
        Flag = FLAG_UNKNOWN;
        FileName = name;
        RelativePath = relativePath;
        FileSize = size;
        Modified = modifiedAt;
    }


    /**
     * Contructor
     * @param name File name
     * @param relativePath Relative path
     * @param size File size
     * @param modifiedAt File last modified at
     * @param mimeType MimeType
     */
    public FileItem(String name, String relativePath, long size, long modifiedAt, String mimeType)
    {
        this(name, relativePath, size, modifiedAt);
        MimeType = mimeType;
    }


    public int Flag = FLAG_UNKNOWN;
    public String FileName = "";
    public String RelativePath = "";
    public long FileSize = 0;
    public long Modified = 0;
    public String MimeType = "";


    @Override
    public FileItem clone()
    {
        FileItem result = new FileItem(FileName, RelativePath, FileSize, Modified, MimeType);
        result.Flag = this.Flag;

        return result;
    }


    /**
     * Compares two instances of FileItem
     * @param other Other Instance
     * @param WithoutFlags Also equal when comparision flags are not equal
     * @param WithoutMime Also equal when MimeType not match. It's not in every case possible to have a valid MimeType
     * @return True when equal
     */
    public boolean isEqual(FileItem other, boolean WithoutFlags, boolean WithoutMime)
    {
        if(WithoutFlags == false && Flag != other.Flag)
            return false;
        if(FileName.equals(other.FileName) == false)
            return false;
        if(RelativePath.equals(other.RelativePath) == false)
            return false;
        if(FileSize != other.FileSize)
            return false;
        if(Modified != other.Modified)
            return false;
        if(WithoutMime == false && MimeType.equals(other.MimeType) == false)
            return false;

        return true;
    }
}
