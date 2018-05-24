/**
 * Created by theakki on 25.03.18.
 */

package theakki.synctool.Job;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.widget.BaseAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;

import theakki.synctool.Helper.Date;
import theakki.synctool.Job.ConnectionTypes.ConnectionFactory;
import theakki.synctool.Job.Merger.DoingSide;
import theakki.synctool.Job.Merger.JobType;
import theakki.synctool.Job.Settings.OneWayStrategy;
import theakki.synctool.Job.Merger.DoingList;
import theakki.synctool.Job.Merger.FileMergeResult;
import theakki.synctool.Job.Merger.MergeResult;
import theakki.synctool.Job.Settings.SettingsHelper;
import theakki.synctool.Job.Settings.SyncDirection;
import theakki.synctool.Job.Settings.TwoWayStrategy;
import theakki.synctool.R;

public class SyncJob extends AsyncTask<Activity, Integer, Integer>
{
    private final String TAG_ROOT = "SyncJob";
    private final String TAG_SideA = "SideA";
    private final String TAG_SideB = "SideB";
    private final String TAG_Name = "Name";
    private final String ATTR_Type = "type";
    private final String TAG_Active = "Active";
    private final String TAG_SyncDirection = "SyncDir";
    private final String TAG_OneWayStrategy = "StrategyOneWay";
    private final String TAG_TwoWayStrategy = "StrategyTwoWay";
    private final String TAG_WhiteList = "IncludeList";
    private final String TAG_Blacklist = "ExcludeList";
    private final String TAG_Item = "Item";

    public SyncJob(Element Node)
    {
        final String name = Node.getNodeName();
        if(name.compareToIgnoreCase(TAG_ROOT) != 0)
            throw new IllegalArgumentException("Node name '" + name + "' not expected");

        NodeList childs = Node.getChildNodes();
        for(int i = 0; i < childs.getLength(); ++i)
        {
            Element child = (Element)childs.item(i);
            final String elementName = child.getNodeName();
            if(elementName.compareToIgnoreCase(TAG_SideA) == 0)
            {
                String type = child.getAttribute(ATTR_Type);
                IConnection sideA= ConnectionFactory.create(type, (Element) child.getChildNodes().item(0));
                if(sideA == null)
                    throw new IllegalArgumentException("Connection '" + type + "' not implemented");
                _SideA = sideA;
            }
            else if(elementName.compareToIgnoreCase(TAG_SideB) == 0)
            {
                String type = child.getAttribute(ATTR_Type);
                IConnection sideB= ConnectionFactory.create(type, (Element) child.getChildNodes().item(0));
                if(sideB == null)
                    throw new IllegalArgumentException("Connection '" + type + "' not implemented");
                _SideB = sideB;
            }
            else if(elementName.compareToIgnoreCase(TAG_Name) == 0)
            {
                _Name = child.getTextContent();
            }
            else if(elementName.compareToIgnoreCase(TAG_Active) == 0)
            {
                _isActive = Boolean.parseBoolean(child.getTextContent());
            }
            else if(elementName.compareToIgnoreCase(TAG_Blacklist) == 0)
            {
                insertInList(child, _ExcludeList, true);
            }
            else if(elementName.compareToIgnoreCase(TAG_WhiteList) == 0)
            {
                insertInList(child, _IncludeList, true);
            }
            else if(elementName.compareToIgnoreCase(TAG_SyncDirection) == 0)
            {
                _Direction = SettingsHelper.SyncDirectionFromString( child.getTextContent(), true, SyncDirection.Booth );
            }
            else if(elementName.compareToIgnoreCase(TAG_OneWayStrategy) == 0)
            {
                _SingleStategie = SettingsHelper.OneWayStrategyFromString( child.getTextContent(), true, OneWayStrategy.Standard );
            }
            else if(elementName.compareToIgnoreCase(TAG_TwoWayStrategy) == 0)
            {
                _BoothStrategie = SettingsHelper.TwoWayStrategyFromString( child.getTextContent(), true, TwoWayStrategy.AWins);
            }
        }
    }


    public SyncJob()
    {

    }


    protected void insertInList(Element parent, ArrayList<String> list, boolean clear)
    {
        if(clear)
            list.clear();

        NodeList childs = parent.getChildNodes();
        for(int i = 0; i < childs.getLength(); ++i)
        {
            Element child = (Element) childs.item(i);
            final String nodeName = child.getTagName();
            if(nodeName.compareToIgnoreCase(TAG_Item) != 0)
                throw new IllegalArgumentException("Unexpected Nodename '" + nodeName + "'.");
            list.add( child.getTextContent() );
        }
    }


    public Element getSettings(Document doc)
    {
        Element root = doc.createElement(TAG_ROOT);

        // Name
        Element name = doc.createElement(TAG_Name);
        name.setTextContent(_Name);
        root.appendChild(name);

        // Active
        Element active = doc.createElement(TAG_Active);
        active.setTextContent("" + _isActive);
        root.appendChild(active);

        // Path A
        if(_SideA != null)
        {
            Element sideARoot = doc.createElement(TAG_SideA);
            sideARoot.setAttribute(ATTR_Type, _SideA.Type());
            Element sideA = _SideA.getSettings(doc);
            sideARoot.appendChild(sideA);
            root.appendChild(sideARoot);
        }

        // Path B
        if(_SideB != null)
        {
            Element sideBRoot = doc.createElement(TAG_SideB);
            sideBRoot.setAttribute(ATTR_Type, _SideB.Type());
            Element sideB = _SideB.getSettings(doc);
            sideBRoot.appendChild(sideB);
            root.appendChild(sideBRoot);
        }

        // Sync Direction
        Element syncDir = doc.createElement(TAG_SyncDirection);
        syncDir.setTextContent(_Direction.toString());
        root.appendChild(syncDir);

        // One Way Sync Strategy
        Element ows = doc.createElement(TAG_OneWayStrategy);
        ows.setTextContent(_SingleStategie.toString() );
        root.appendChild(ows);

        // Two Way Sync Strategy
        Element tws = doc.createElement(TAG_TwoWayStrategy);
        tws.setTextContent(_BoothStrategie.toString());
        root.appendChild(tws);

        // WhiteList
        Element wl = doc.createElement(TAG_WhiteList);
        getTextListNodes(_IncludeList, doc, wl);
        root.appendChild(wl);

        // Blacklist
        Element bl = doc.createElement(TAG_Blacklist);
        getTextListNodes(_ExcludeList, doc, bl);
        root.appendChild(bl);

        return root;
    }


    protected void getTextListNodes(ArrayList<String> list, Document doc, Element root)
    {
        for(String strItem : list)
        {
            Element item = doc.createElement(TAG_Item);
            item.setTextContent( strItem );
            root.appendChild(item);
        }
    }


    public JobInfo getJobInfo()
    {
        JobInfo result = new JobInfo();

        result.Name = _Name;
        result.IsActive = _isActive;
        result.Status = _status;
        result.ActiveElements = _actNumber;
        result.MaxElements = _maxNumber;

        return result;
    }


    public static final int STATUS_NOT_STARTED = 0;
    public static final int STATUS_CONNECT_FILES_A = 1;
    public static final int STATUS_CONNECT_FILES_B = 2;
    public static final int STATUS_READ_FILES_A = 3;
    public static final int STATUS_READ_FILES_B = 4;
    public static final int STATUS_ANALYSE_FILES = 5;
    public static final int STATUS_CREATE_JOB = 6;
    public static final int STATUS_FINISH = 10;


    private int _status = 0;
    public int getProcess(){ return _status; }
    private int _actNumber = 0;
    public  int getActNumber(){ return _actNumber; }
    public  int _maxNumber = 0;
    public int getMaxNumber(){ return _maxNumber; }

    private BaseAdapter _adapter;
    public void setAdapter(@Nullable BaseAdapter adapter){_adapter = adapter; }

    @Override
    protected Integer doInBackground(Activity... params)
    {
        Activity act = params[0];
        Do(act);

        return 0;
    }

    @Override
    protected void onProgressUpdate(Integer... params)

    {
        _status = params[0];
        _actNumber = params[1];
        _maxNumber = params[2];

        if(_adapter != null)
            _adapter.notifyDataSetChanged();
    }

    public static int getStatusText(int status)
    {
        switch(status)
        {
            case STATUS_NOT_STARTED:
                return R.string.SyncStatus_NotStarted;
            case STATUS_CONNECT_FILES_A:
                return R.string.SyncStatus_ConnectA;
            case STATUS_CONNECT_FILES_B:
                return R.string.SyncStatus_ConnectB;
            case STATUS_READ_FILES_A:
                return R.string.SyncStatus_ReadFilesA;
            case STATUS_READ_FILES_B:
                return R.string.SyncStatus_ReadFilesB;
            case STATUS_ANALYSE_FILES:
                return R.string.SyncStatus_AnalyseFiles;
            case STATUS_CREATE_JOB:
                return R.string.SyncStatus_CreateJobs;
            case STATUS_FINISH:
                return R.string.SyncStatus_Finish;

            default:
                throw new IllegalStateException("No text for status '" + status + "' available");
        }
    }


    @Override
    protected void onPostExecute(Integer result)
    {
        super.onPostExecute(result);
    }


    private void Do(Activity context) throws IllegalStateException
    {
        if(_SideA == null)
            throw new IllegalStateException("Connection A is null");
        if(_SideB == null)
            throw new IllegalStateException("Connection B is null");

        _SideA.RequestPermissions(context);
        _SideB.RequestPermissions(context);

        publishProgress(STATUS_CONNECT_FILES_A, 0, 0);
        _SideA.Connect(context);
        publishProgress(STATUS_CONNECT_FILES_B, 0, 0);
        _SideB.Connect(context);

        publishProgress(STATUS_READ_FILES_A, 0, 0);
        ArrayList<FileItem> FilesA = _SideA.getFileList();
        publishProgress(STATUS_READ_FILES_B, 0, 0);
        ArrayList<FileItem> FilesB = _SideB.getFileList();

        publishProgress(STATUS_ANALYSE_FILES, 0, 0);
        ArrayList<FileMergeResult> MergedFiles = MergeFileList(FilesA, FilesB);
        publishProgress(STATUS_CREATE_JOB, 0, 0);
        ArrayList<DoingList> JobList = ApplyStrategy(MergedFiles);

        Apply(JobList, _SideA, _SideB);

        _SideA.Disconnect();
        _SideB.Disconnect();
    }


    private void throwIllegalState(DoingList doing)
    {
        throw new IllegalStateException("Invalid combination " +
                                        "A:'" + doing.SideA.Type.toString() + "' and " +
                                        "B:'" + doing.SideB.Type.toString() + "'");
    }

    protected void Apply(ArrayList<DoingList> list, IConnection conSideA, IConnection conSideB)
    {
        for(DoingList job : list)
        {
            boolean result = false;

            switch(job.SideA.Type)
            {
                case Nothing:
                    // Single-Side actions only
                    switch(job.SideB.Type)
                    {
                        case Nothing:
                            throw new IllegalStateException("Side A + B are empty");

                        case Move:
                            result = conSideB.Move(job.SideB.Filename, job.SideB.Param);
                            break;

                        case Delete:
                            result = conSideB.Delete(job.SideB.Filename);
                            break;

                        default:
                            throwIllegalState(job);
                    }
                    break;

                case Move:
                    switch(job.SideB.Type)
                    {
                        case Nothing:
                            // Expected other side
                            break;

                        default:
                            throwIllegalState(job);
                    }
                    result = conSideA.Move(job.SideA.Filename, job.SideA.Param);
                    break;

                case Delete:
                    switch(job.SideB.Type)
                    {
                        case Nothing:
                            // Expected other side
                            break;

                        default:
                            throwIllegalState(job);
                    }
                    result = conSideA.Delete(job.SideA.Filename);
                    break;

                case Read:
                    switch(job.SideB.Type)
                    {
                        case Write:
                            result = Copy(job.SideA, job.SideB, conSideA, conSideB);
                            break;

                        default:
                            throwIllegalState(job);
                    }
                    // No Single-Side Action allowed
                    break;

                case Write:
                    switch(job.SideB.Type)
                    {
                        case Read:
                            result = Copy(job.SideB, job.SideA, conSideB, conSideA);
                            break;

                        default:
                            throwIllegalState(job);
                    }
                    // No Single-Side Action allowed
                    break;

                default:
                    throw new IllegalStateException("Action '" + job.SideA.Type.toString() + "' not implemented");
            }

            if(result == false)
            {
                // fail...
            }
        }
    }


    private boolean Copy(DoingSide Source, DoingSide Target, IConnection conSource, IConnection conTarget)
    {
        try
        {
            File tempFile = File.createTempFile("temporaery", ".tmp");
            conSource.Read(Source.Filename, tempFile);
            conTarget.Write(tempFile, Target.Filename);

            tempFile.delete();
        }
        catch(Exception e)
        {
            return false;
        }
        return true;

    }


    protected ArrayList<DoingList> ApplyStrategy(ArrayList<FileMergeResult> MergedFiles)
    {
        ArrayList<DoingList> result = new ArrayList<>();

        String prefixPathA = "";
        String prefixPathB = "";

        if(_SingleStategie == OneWayStrategy.AllFilesInDateFolder || _SingleStategie == OneWayStrategy.NewFilesInDateFolder)
        {
            if(_Direction == SyncDirection.ToA)
            {
                prefixPathA = "/" + Date.getDate();
            }
            else if(_Direction == SyncDirection.ToB)
            {
                prefixPathB = "/" + Date.getDate();
            }
        }

        for(int i = 0; i < MergedFiles.size(); ++i)
        {
            FileMergeResult res = MergedFiles.get(i);
            switch(res.State)
            {
                case MatchExactly:
                    // Do nothing
                    break;

                case DateChanged_ANewer:
                    if(_Direction == SyncDirection.ToB || _Direction == SyncDirection.Booth)
                    {
                        DoingList temp = new DoingList();
                        temp.SideA.Type= JobType.Read;
                        temp.SideB.Type= JobType.Write;
                        temp.SideA.Filename= prefixPathA + res.FileA.RelativePath + res.FileA.FileName;
                        temp.SideB.Filename = prefixPathB + res.FileB.RelativePath + res.FileB.FileName;
                        temp.SideB.Timestamp = res.FileA.Modified;
                        result.add(temp);
                    }
                    break;

                case DateChanged_BNewer:
                    if(_Direction == SyncDirection.ToA || _Direction == SyncDirection.Booth)
                    {
                        DoingList temp = new DoingList();
                        temp.SideA.Type = JobType.Write;
                        temp.SideB.Type = JobType.Read;
                        temp.SideA.Filename = prefixPathA + res.FileA.RelativePath + res.FileA.FileName;
                        temp.SideB.Filename = prefixPathB + res.FileB.RelativePath + res.FileB.FileName;
                        temp.SideA.Timestamp = res.FileB.Modified;
                        result.add(temp);
                    }
                    break;

                case Renamed_A: // fall through
                case Moved_A:
                    {
                        DoingList temp = new DoingList();
                        temp.SideA.Type = JobType.Move;
                        temp.SideA.Filename = prefixPathA + res.FileA.RelativePath + res.FileA.FileName;
                        temp.SideA.Param= prefixPathA + res.FileB.RelativePath + res.FileB.FileName;
                        temp.SideA.Timestamp = res.FileB.Modified;
                        result.add(temp);
                    }
                    break;

                case Renamed_B: // fall through
                case Moved_B:
                    {
                        DoingList temp = new DoingList();
                        temp.SideB.Type = JobType.Move;
                        temp.SideB.Filename = prefixPathB + res.FileB.RelativePath + res.FileB.FileName;
                        temp.SideB.Param = prefixPathB + res.FileA.RelativePath + res.FileA.FileName;
                        temp.SideB.Timestamp = res.FileA.Modified;
                        result.add(temp);
                    }
                    break;

                case NewFile:
                    if(res.FileA == null)
                    {
                        DoingList temp = new DoingList();
                        temp.SideB.Type = JobType.Read;
                        temp.SideB.Filename = prefixPathB + res.FileB.RelativePath + res.FileB.FileName;
                        temp.SideA.Type = JobType.Write;
                        temp.SideA.Filename = prefixPathA + res.FileB.RelativePath + res.FileB.FileName;
                        temp.SideA.Timestamp = res.FileB.Modified;
                    }
                    else
                    {
                        DoingList temp = new DoingList();
                        temp.SideA.Type = JobType.Read;
                        temp.SideA.Filename = prefixPathA + res.FileA.RelativePath + res.FileA.FileName;
                        temp.SideB.Type = JobType.Write;
                        temp.SideB.Filename = prefixPathB + res.FileA.RelativePath + res.FileA.FileName;
                        temp.SideB.Timestamp = res.FileA.Modified;
                    }
                    break;
            }
        }

        return result;
    }



    protected ArrayList<FileMergeResult> MergeFileList(ArrayList<FileItem> A, ArrayList<FileItem> B)
    {
        ArrayList<FileMergeResult> result = new ArrayList<>();

        boolean onlyChanges = true;
        boolean aToB = true;    // Defaultwerte für ToB
        boolean bToA = false;   // Defaultwerte für ToB
        boolean skipMoveAToB = false;
        boolean skipMoveBToA = true;

        switch(_Direction)
        {
            case ToA:
                aToB = false;
                bToA = true;
                skipMoveAToB = true;
                skipMoveBToA = false;
                // fall through
            case ToB:
                switch(_SingleStategie)
                {
                    case Standard: // fall through
                    case Mirror: // fall through
                    case NewFilesInDateFolder:
                        onlyChanges = true;
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
            fillMergeList(A, B, result, onlyChanges, skipMoveAToB, false);
        }
        if(bToA)
        {
            fillMergeList(B, A, result, onlyChanges, skipMoveBToA, true);
        }

        return result;
    }


    private void fillMergeList(ArrayList<FileItem> A, ArrayList<FileItem> B, ArrayList<FileMergeResult> result, boolean SkipEqual, boolean SkipMove, boolean AToB)
    {
        for(int i = 0; i < A.size(); ++i)
        {
            FileItem FileA = A.get(i);

            // check if File A is already checked
            if(FileA.Flag != FileItem.FLAG_UNKNOWN)
                continue;

            SortedMap<MergeResult, FileItem> resultMap = new TreeMap<MergeResult, FileItem>();


            for(int j = 0; j < B.size(); ++j)
            {
                FileItem FileB = B.get(j);

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
                    BestResult.Flag = FileA.Flag = FileItem.FLAG_ANALIZED_MATCH_EXACTLY;

                case DateChanged_ANewer:
                    BestResult.Flag = FileA.Flag = FileItem.FLAG_ANALIZED_MATCH_FILE;

                case DateChanged_BNewer:
                    BestResult.Flag = FileA.Flag = FileItem.FLAG_ANALIZED_MATCH_FILE;

                case Renamed_A: // fall through
                case Renamed_B:
                    if(SkipMove)
                        continue;
                    BestResult.Flag = FileA.Flag = FileItem.FLAG_ANALIZED_MATCH_OBJECT;

                case Moved_A: // fall through
                case Moved_B:
                    if(SkipMove)
                        continue;
                    BestResult.Flag = FileA.Flag = FileItem.FLAG_ANALIZED_MATCH_OBJECT;

                case NewFile:
                    FileA.Flag = FileItem.FLAG_ANALIZED;
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

    protected Boolean fileMatch(String FileName, ArrayList<String> List)
    {
        for(String Element : List)
        {
            if(FileName.endsWith(Element))
                return true;
        }
        return false;
    }


    // Sync Direction
    private SyncDirection _Direction =  SyncDirection.Booth; //SyncDirection.ToB;
    public SyncDirection Direction()
    {
        return _Direction;
    }
    public void Direction(SyncDirection Dir)
    {
        _Direction = Dir;
    }

    private OneWayStrategy _SingleStategie = OneWayStrategy.Standard;
    public OneWayStrategy StrategieOneWay() { return _SingleStategie; }
    public void StrategieOneWay(OneWayStrategy Strategie) { _SingleStategie = Strategie; }

    private TwoWayStrategy _BoothStrategie = TwoWayStrategy.BWins;
    public TwoWayStrategy StrategieTwoWay() { return _BoothStrategie; }
    public void StrategieTwoWay(TwoWayStrategy Strategie){ _BoothStrategie = Strategie; }


    private boolean _isActive = true;
    public boolean Active() {return _isActive; }
    public void Active(boolean value) { _isActive = value; }


    private ArrayList<String> _IncludeList = new ArrayList<>();
    private ArrayList<String> _ExcludeList = new ArrayList<>();


    private String _Name = "";
    public String Name(){return _Name;}
    public void Name(String Name){this._Name = Name;}


    private IConnection _SideA;
    public void SideA(IConnection Con)
    {
        _SideA = Con;
    }
    public IConnection SideA() { return _SideA; }   // evtl. clone


    private IConnection _SideB;
    public void SideB(IConnection Con)
    {
        _SideB = Con;
    }
    public IConnection SideB(){ return _SideB; }    // evtl. clone
}
