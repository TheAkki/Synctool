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


    /**
     * This method analyse file items and write them in a list. This list is the parameter result. All entrys will be added.
     * @param filelistA List with files from side A
     * @param filelistB List with files from side B
     * @param result List with merge results
     * @param SkipEqual Parameter to mark that files which are equal should not include in this list.
     * @param SkipMove Parameter to mark that file which are only moved should not include in this list.
     * @param AToB Parameter to mark that the direction is A to B.
     */
    public static void fillMergeList(ArrayList<FileItem> filelistA, ArrayList<FileItem> filelistB, ArrayList<FileMergeResult> result, boolean SkipEqual, boolean SkipMove, boolean AToB)
    {
        for(int i = 0; i < filelistA.size(); ++i)
        {
            FileItem FileA = filelistA.get(i);

            // check if File A is already checked
            if(FileA.Flag != FileItem.FLAG_UNKNOWN)
                continue;

            SortedMap<MergeResult, FileItem> resultMap = new TreeMap<MergeResult, FileItem>();


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

            switch(BestResultType)
            {
                case MatchExactly:
                    if(SkipEqual)
                        continue;
                    BestResult.Flag = FileA.Flag = FileItem.FLAG_ANALYZED_MATCH_EXACTLY;

                case DateChanged_ANewer:
                    BestResult.Flag = FileA.Flag = FileItem.FLAG_ANALYZED_MATCH_FILE;

                case DateChanged_BNewer:
                    BestResult.Flag = FileA.Flag = FileItem.FLAG_ANALYZED_MATCH_FILE;

                case Renamed_A: // fall through
                case Renamed_B:
                    if(SkipMove)
                        continue;
                    BestResult.Flag = FileA.Flag = FileItem.FLAG_ANALYZED_MATCH_OBJECT;

                case Moved_A: // fall through
                case Moved_B:
                    if(SkipMove)
                        continue;
                    BestResult.Flag = FileA.Flag = FileItem.FLAG_ANALYZED_MATCH_OBJECT;

                case NewFile:
                    FileA.Flag = FileItem.FLAG_ANALYZED;
            }

            FileMergeResult tempResult = new FileMergeResult();
            tempResult.State = BestResultType;
            tempResult.FileA = FileA.clone();
            tempResult.FileB = (BestResult == null) ? null : BestResult.clone();
            if(AToB)
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
        boolean aToB = true;    // Defaultwerte für ToB
        boolean bToA = false;   // Defaultwerte für ToB
        boolean skipMoveAToB = false;
        boolean skipMoveBToA = true;

        switch(direction)
        {
            case ToA:
                aToB = false;
                bToA = true;
                skipMoveAToB = true;
                skipMoveBToA = false;
                // fall through
            case ToB:
                switch(singleside)
                {
                    case Standard: // fall through
                    case Mirror:
                        onlyChanges = true;
                        break;
                    case NewFilesInDateFolder:
                        onlyChanges = true;
                        skipMoveAToB = true;
                        break;
                    case AllFilesInDateFolder:
                        onlyChanges = false;
                        break;
                }
                break;
            case Booth:
                aToB = true;
                bToA = true;
                onlyChanges = true;
                break;
        }

        if(aToB)
        {
            fillMergeList(filelistA, filelistB, result, onlyChanges, skipMoveAToB, false);
        }
        if(bToA)
        {
            fillMergeList(filelistB, filelistA, result, onlyChanges, skipMoveBToA, true);
        }

        return result;
    }
}
