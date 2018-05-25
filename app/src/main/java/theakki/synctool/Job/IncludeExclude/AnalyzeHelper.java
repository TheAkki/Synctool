package theakki.synctool.Job.IncludeExclude;

import java.io.File;
import java.util.ArrayList;

import theakki.synctool.Helper.FileItemHelper;
import theakki.synctool.Job.FileItem;

/**
 * Helper class to do analysation.
 * @author theakki
 * @since 0.1
 */
public class AnalyzeHelper
{
    private static final String MIME_TYPE = "Mime: ";
    private static final char ASTERIX_C = '*';
    private static final String ASTERIX = "" + ASTERIX_C;


    /**
     * This Method analyse a list with Strings and analyse them for checking strategy to match file items
     * @param list String list which is to analyse
     * @return Analysed String list
     */
    public static ArrayList<AnalyzeResult> prepareList(ArrayList<String> list)
    {
        ArrayList<AnalyzeResult> results = new ArrayList<>();
        for(String item : list)
        {
            AnalyzeResult analyze = new AnalyzeResult();

            // MimeType
            if(item.startsWith(MIME_TYPE))
            {
                final String param = item.substring(MIME_TYPE.length());
                if(param.length() > 0)
                {
                    analyze.Strategy = AnalyzeStrategy.MimeType;
                    analyze.Parameter = param;
                }

            }
            // Starts with Asterix
            else if(item.startsWith(ASTERIX))
            {
                final String param = item.substring(ASTERIX.length());

                if(param.length() == 0)
                {
                    analyze.Strategy = AnalyzeStrategy.FileMatch;
                }
                else if(param.contains(ASTERIX))
                {
                    // ToDo: Think about this scheme.... not so easy -> regex
                    analyze.Strategy = AnalyzeStrategy.NotImplemented;
                    analyze.Parameter = item;
                }
                else
                {
                    if(param.contains(File.separator))
                    {
                        analyze.Strategy = AnalyzeStrategy.PathEndsWith;
                        analyze.Parameter = param;
                    }
                    else
                    {
                        analyze.Strategy = AnalyzeStrategy.FileEndsWith;
                        analyze.Parameter = param;
                    }
                }
            }
            // contain Asterix
            else if(item.contains(ASTERIX))
            {
                // ToDo: Think about this scheme.... not so easy -> regex
                analyze.Strategy = AnalyzeStrategy.NotImplemented;
                analyze.Parameter = item;
            }
            // starts with seperator
            else if(item.startsWith(File.separator))
            {
                final String temp = item.substring(1);
                if(temp.endsWith(File.separator))
                {
                    analyze.Strategy = AnalyzeStrategy.PathStartsWith;
                    analyze.Parameter = item;
                }
                else
                {
                    if(temp.contains(File.separator))
                    {
                        analyze.Strategy = AnalyzeStrategy.PathStartsWith;
                        analyze.Parameter = item;
                    }
                    else
                    {
                        analyze.Strategy = AnalyzeStrategy.FileNameMatch;
                        analyze.Parameter = temp;
                    }
                }
            }
            // contains Seperator
            else if(item.contains(File.separator))
            {
                analyze.Strategy = AnalyzeStrategy.PathEndsWith;
                analyze.Parameter = item;
            }
            else
            {
                analyze.Strategy = AnalyzeStrategy.FileNameMatch;
                analyze.Parameter = item;
            }

            results.add(analyze);
        }
        return results;
    }


    /**
     * This method test a file with a list of testcases
     * @param file  File which is to test
     * @param list  List with test cases
     * @return File is matching with less one test case
     */
    public static boolean match(FileItem file, ArrayList<AnalyzeResult> list)
    {
        for(AnalyzeResult analyseItem : list)
        {
            switch(analyseItem.Strategy)
            {
                case MimeType:
                    if(file.MimeType.equals(analyseItem.Parameter))
                        return true;
                    break;

                case FileMatch:
                    return true;

                case FileEndsWith:
                    if(file.FileName.endsWith(analyseItem.Parameter))
                        return true;
                    break;

                case PathEndsWith:
                    final String uri = FileItemHelper.concatPath(file.RelativePath, file.FileName);
                    if(uri.endsWith(analyseItem.Parameter))
                        return true;
                    break;

                case FileStartsWith:
                    final String uri1 = FileItemHelper.concatPath(file.RelativePath, file.FileName);
                    if(uri1.startsWith(analyseItem.Parameter))
                        return true;
                    break;

                case PathStartsWith:
                    if(file.FileName.startsWith(analyseItem.Parameter))
                        return true;
                    break;

                case NotImplemented:
                    continue;

                case NotKnown:
                    continue;

                case RegEx:
                    final String uri2 = FileItemHelper.concatPath(file.RelativePath, file.FileName);
                    // ToDo: Do some regex magic here ...
                    continue; // in case of not implemented yet
            }
        }
        return false;
    }


    /**
     * This Method check if a file is valid. This means that when file is not in blacklist is valid.
     * When file match with blacklist than the file will check with whitelist. When file is also match
     * with whitelist the file is valid.
     * @param file File which is to check.
     * @param blacklist Blacklist
     * @param whitelist Whitelist
     * @return True if a file is valid
     */
    public static boolean fileIsValid(FileItem file, ArrayList<AnalyzeResult> blacklist, ArrayList<AnalyzeResult> whitelist)
    {
        if(match(file, blacklist))
        {
            return match(file, whitelist);
        }
        // not in blacklist so it's valid
        return true;
    }
}
