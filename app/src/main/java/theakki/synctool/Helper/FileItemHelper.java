package theakki.synctool.Helper;

import java.io.File;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import theakki.synctool.Job.FileItem;
import theakki.synctool.Job.Merger.FileMergeResult;
import theakki.synctool.Job.Merger.MergeResult;
import theakki.synctool.Job.Settings.OneWayStrategy;
import theakki.synctool.Job.Settings.SyncDirection;

/**
 * Util class for class FileItem
 * @author theakki
 * @since 0.1
 */
public class FileItemHelper
{
    /**
     * This Method convert an array list of objects to array list of FileItem.
     * It will not checked if object is a FileItem.
     * @param objects Array list of objects
     * @return Array list of FileItem
     */
    public static ArrayList<FileItem> convertFromObjectArray(ArrayList<Object> objects)
    {
        if(objects == null)
            return new ArrayList<>();

        ArrayList<FileItem> fileItems = new ArrayList<>(objects.size());
        for (Object object : objects)
        {
            fileItems.add((FileItem)object);
        }

        return fileItems;
    }

    public static ArrayList<FileItem> clone(ArrayList<FileItem> source)
    {
        ArrayList<FileItem> list = new ArrayList<>(source.size());
        for(FileItem s : source)
            list.add( s.clone() );

        return list;
    }


    /**
     * Concat two parts of a path. It's also adjust the necessary seperator
     * @param first First part of path
     * @param last  Next part of path
     * @return Concated path
     */
    public static String concatPath(String first, String last)
    {
        if(first.endsWith(File.separator))
        {
            if(last.startsWith(File.separator))
            {
                return first + last.substring(File.separator.length());
            }
            else
            {
                return first + last;
            }
        }
        else
        {
            if(last.startsWith(File.separator))
            {
                return first + last;
            }
            else
            {
                return first + File.separator + last;
            }
        }
    }

    public static String[] splittPath(String path)
    {
        String p;
        if(path.startsWith(File.separator))
        {
            p = path.substring(1);
        }
        else
            p = path;

        return p.split(File.separator);
    }


    /**
     * This Methods extract the relative Path from a full specified path
     * @param fullpath Full path
     * @param filename File name
     * @param remotePath Remote Path to root
     * @return
     */
    public static String getRelativePath(final String fullpath, final String filename, final String remotePath)
    {
        final int lengthRemotePath = (remotePath.endsWith(File.separator)) ?  remotePath.length() - 1 : remotePath.length();

        String result = fullpath.substring(lengthRemotePath);
        result = result.substring(0, result.length() - filename.length());

        return result;
    }


    /**
     * This method analyse file items and write them in a list. This list is the parameter result. All entrys will be added.
     * @param filelistA List with files from side A
     * @param filelistB List with files from side B
     * @param result List with merge results
     * @param SkipEqual Parameter to mark that files which are equal should not include in this list.
     * @param SkipMove Parameter to mark that file which are only moved should not include in this list.
     * @param AToB Parameter to mark that the direction is A to B.
     * @param MoveAsNew Parameter to mark that a moved file is used as new file
     */
    public static void fillMergeList(   ArrayList<FileItem> filelistA,
                                        ArrayList<FileItem> filelistB,
                                        ArrayList<FileMergeResult> result,
                                        boolean SkipEqual,
                                        boolean SkipMove,
                                        boolean AToB,
                                        boolean MoveAsNew)
    {
        for(int i = 0; i < filelistA.size(); ++i)
        {
            FileItem FileA = filelistA.get(i);

            // check if File A is already checked
            if(FileA.Flag != FileItem.FLAG_UNKNOWN)
                continue;

            SortedMap<MergeResult, FileItem> resultMap = new TreeMap<>();


            for(int j = 0; j < filelistB.size(); ++j)
            {
                FileItem FileB = filelistB.get(j);

                // check if File B is already checked
                if((FileB.Flag != FileItem.FLAG_UNKNOWN))
                    continue;

                // check: same folder & same filename
                if((FileA.RelativePath.equals(FileB.RelativePath)) && (FileA.FileName.equals(FileB.FileName)))
                {
                    // match exactly
                    if((FileA.Modified == FileB.Modified) && (FileA.FileSize == FileB.FileSize))
                    {
                        resultMap.put(MergeResult.MatchExactly, FileB);
                        break;  // Do not check other files
                    }
                    // same name, same folder, but changed time or size: get newer one
                    else if(FileA.FileSize == FileB.FileSize || FileA.Modified != FileB.Modified)
                    {
                        if(FileA.Modified > FileB.Modified)
                        {
                            resultMap.put(MergeResult.DateChanged_ANewer, FileB);
                        }
                        else
                        {
                            resultMap.put(MergeResult.DateChanged_BNewer, FileB);
                        }

                        break; // Do not check other files
                    }
                    else if(FileA.Modified == FileB.Modified)
                    {
                        // Same Filename, Folder and Time, but not the size: Something is wrong
                        resultMap.put(MergeResult.NotRelatedFile, FileB);

                        // Do not break here. Maybe there is an better solution
                    }
                }
                // check same folder
                else if(FileA.RelativePath.equals(FileB.RelativePath))
                {
                    // renamed
                    if((FileA.Modified == FileB.Modified) && (FileA.FileSize == FileB.FileSize))
                    {
                        if(AToB)
                        {
                            resultMap.put(MergeResult.Renamed_B, FileB);
                        }
                        else
                        {
                            resultMap.put(MergeResult.Renamed_A, FileB);
                        }
                    }
                }
                // check same Filename
                else if(FileA.FileName.equals(FileB.FileName))
                {
                    // moved
                    if((FileA.Modified == FileB.Modified) && (FileA.FileSize == FileB.FileSize))
                    {
                        if(AToB)
                        {
                            resultMap.put(MergeResult.Moved_B, FileB);
                        }
                        else
                        {
                            resultMap.put(MergeResult.Moved_A, FileB);
                        }
                    }
                }
            }


            MergeResult BestResultType = (resultMap.isEmpty()) ? MergeResult.NewFile : resultMap.firstKey();
            FileItem BestResult = (resultMap.isEmpty()) ? null : resultMap.get(BestResultType);

            boolean bSwapFromTo = false;
            switch(BestResultType)
            {
                case MatchExactly:
                    if(SkipEqual)
                        continue;
                    BestResult.Flag = FileA.Flag = FileItem.FLAG_ANALYZED_MATCH_EXACTLY;
                    break;

                case DateChanged_ANewer:
                    BestResult.Flag = FileA.Flag = FileItem.FLAG_ANALYZED_MATCH_FILE;
                    break;

                case DateChanged_BNewer:
                    BestResult.Flag = FileA.Flag = FileItem.FLAG_ANALYZED_MATCH_FILE;
                    break;

                case Renamed_A: // fall through
                case Renamed_B:
                    if(SkipMove)
                        continue;
                    BestResult.Flag = FileA.Flag = FileItem.FLAG_ANALYZED_MATCH_OBJECT;
                    bSwapFromTo = true;
                    break;

                case Moved_A: // fall through
                case Moved_B:
                    if(MoveAsNew == false)
                    {
                        if(SkipMove)
                            continue;
                        BestResult.Flag = FileA.Flag = FileItem.FLAG_ANALYZED_MATCH_OBJECT;
                        bSwapFromTo = true;
                        break;
                    }
                    // fall through to NewFile

                case NewFile:
                    BestResultType = MergeResult.NewFile;
                    BestResult = null;
                    FileA.Flag = FileItem.FLAG_ANALYZED;
            }

            FileMergeResult tempResult = new FileMergeResult();
            tempResult.State = BestResultType;
            tempResult.FileA = FileA.clone();
            tempResult.FileB = (BestResult == null) ? null : BestResult.clone();
            if(bSwapFromTo)
            {
                FileItem temp = tempResult.FileA;
                tempResult.FileA = tempResult.FileB;
                tempResult.FileB = temp;
            }
            result.add(tempResult);
        }
    }


    /**
     * This Method define the parameters for the method fillMergeList and call this later.
     * @param filelistA List with FileItem from Side A
     * @param filelistB List with FileItem from Side B
     * @param direction SyncDirection
     * @param singleside Strategy for synchronize in one direction
     * @return
     */
    public static ArrayList<FileMergeResult> mergeFileList(ArrayList<FileItem> filelistA, ArrayList<FileItem> filelistB, SyncDirection direction, OneWayStrategy singleside)
    {
        ArrayList<FileMergeResult> result = new ArrayList<>();

        boolean onlyChanges = true;
        boolean aToB;
        boolean bToA;
        boolean skipMoveAToB = false;
        boolean skipMoveBToA = false;
        boolean movedFilesAsNewFile = false;

        // Detect which calls
        switch(direction)
        {
            case ToA:
                aToB = false;
                bToA = true;
                skipMoveAToB = false;   // don't care
                skipMoveBToA = false;
                break;

            case ToB:
                aToB = true;
                bToA = false;
                skipMoveAToB = false;
                skipMoveBToA = false; // don't care
                break;

            case Booth:
                aToB = true;
                bToA = true;
                break;

            default:
                throw new RuntimeException("Illegal State detected");
        }

        switch(direction)
        {
            case ToA: // fallthrough
            case ToB:
                switch(singleside)
                {
                    case Standard: // fall through
                    case Mirror:
                        onlyChanges = true;
                        break;
                    case NewFilesInDateFolder:
                        onlyChanges = true;
                        // one of the next setting is don't care
                        skipMoveAToB = true;
                        skipMoveBToA = true;
                        break;
                    case AllFilesInDateFolder:
                        movedFilesAsNewFile = true;
                        onlyChanges = false;
                        // one of the next setting is don't care
                        skipMoveAToB = true;
                        skipMoveBToA = true;
                        break;
                    default:
                        throw new RuntimeException("Illegal State detected");
                }
                break;

            case Booth:
                onlyChanges = true;
                break;
        }

        if(aToB)
        {
            fillMergeList(filelistA, filelistB, result, onlyChanges, skipMoveAToB, true, movedFilesAsNewFile);
        }
        if(bToA)
        {
            fillMergeList(filelistB, filelistA, result, onlyChanges, skipMoveBToA, false, movedFilesAsNewFile);
        }

        return result;
    }
}
