package theakki.synctool.Job;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.*;
import android.util.Log;
import android.widget.BaseAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;

import theakki.synctool.Helper.Date;
import theakki.synctool.Helper.FileItemHelper;
import theakki.synctool.Helper.StringHelper;
import theakki.synctool.Job.ConnectionTypes.ConnectionFactory;
import theakki.synctool.Job.IncludeExclude.AnalyzeHelper;
import theakki.synctool.Job.IncludeExclude.AnalyzeResult;
import theakki.synctool.Job.Merger.DoingSide;
import theakki.synctool.Job.Merger.JobType;
import theakki.synctool.Job.Scheduler.SchedulerInfo;
import theakki.synctool.Job.Settings.OneWayStrategy;
import theakki.synctool.Job.Merger.DoingList;
import theakki.synctool.Job.Merger.FileMergeResult;
import theakki.synctool.Job.Settings.SettingsHelper;
import theakki.synctool.Job.Settings.SyncDirection;
import theakki.synctool.Job.Settings.TwoWayStrategy;
import theakki.synctool.R;

/**
 * This class define and handle a synchronization job.
 * @author theakki
 * @since 0.1
 */
public class SyncJob extends AsyncTask<Context, Integer, Integer>
{
    private final static String L_TAG = SyncJob.class.getSimpleName();

    // XML Tags
    private final static String TAG_ROOT = "SyncJob";
    private final static String TAG_SideA = "SideA";
    private final static String TAG_SideB = "SideB";
    private final static String TAG_Name = "Name";
    private final static String ATTR_Type = "type";
    private final static String TAG_Active = "Active";
    private final static String TAG_SyncDirection = "SyncDir";
    private final static String TAG_OneWayStrategy = "StrategyOneWay";
    private final static String TAG_TwoWayStrategy = "StrategyTwoWay";
    private final static String TAG_WhiteList = "IncludeList";
    private final static String TAG_Blacklist = "ExcludeList";
    private final static String TAG_Item = "Item";
    private final static String TAG_Schedule = "Schedule";
    private final static String ATTR_Schedule_Active = "Active";
    private final static String ATTR_Schedule_Hour =  "Hour";
    private final static String ATTR_Schedule_Minute = "Minute";

    public static final String DEFAULT_SETTINGS = "<" + TAG_ROOT + "/>";


    // Attributees
    private SchedulerInfo _Scheduling = new SchedulerInfo();


    /**
     * Constructor for creating from XML Node
     * @param Node XML-Node
     */
    public SyncJob(Element Node)
    {
        loadJobSettings(Node);
    }


    /**
     * Constructor
     */
    public SyncJob()
    {
    }

    public void loadJobSettings(Element Node)
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
                {
                    final String strError = "Connection '" + type + "' not implemented";
                    Log.e(L_TAG, strError);
                    throw new IllegalArgumentException(strError);
                }
                _SideA = sideA;
            }
            else if(elementName.compareToIgnoreCase(TAG_SideB) == 0)
            {
                String type = child.getAttribute(ATTR_Type);
                IConnection sideB= ConnectionFactory.create(type, (Element) child.getChildNodes().item(0));
                if(sideB == null)
                {
                    final String strError = "Connection '" + type + "' not implemented";
                    Log.e(L_TAG, strError);
                    throw new IllegalArgumentException(strError);
                }
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
                _SingleStrategy = SettingsHelper.OneWayStrategyFromString( child.getTextContent(), true, OneWayStrategy.Standard );
            }
            else if(elementName.compareToIgnoreCase(TAG_TwoWayStrategy) == 0)
            {
                _BoothStrategy = SettingsHelper.TwoWayStrategyFromString( child.getTextContent(), true, TwoWayStrategy.AWins);
            }
            else if(elementName.compareToIgnoreCase(TAG_Schedule) == 0)
            {
                final String Active = child.getAttribute(ATTR_Schedule_Active);
                if(Active != null)
                {
                    _Scheduling.Active( Boolean.parseBoolean(Active) );
                }

                final String hour = child.getAttribute(ATTR_Schedule_Hour);
                if(hour != null)
                {
                    _Scheduling.Hour( Integer.parseInt(hour) );
                }

                final String minute = child.getAttribute(ATTR_Schedule_Minute);
                if(minute != null)
                {
                    _Scheduling.Minute( Integer.parseInt(minute)  );
                }
            }
        }
    }


    protected void insertInList(Element parent, ArrayList<String> list, boolean clear)
    {
        if(clear)
            list.clear();

        NodeList children = parent.getChildNodes();
        for(int i = 0; i < children.getLength(); ++i)
        {
            Element child = (Element) children.item(i);
            final String nodeName = child.getTagName();
            if(nodeName.compareToIgnoreCase(TAG_Item) != 0)
                throw new IllegalArgumentException("Unexpected Nodename '" + nodeName + "'.");

            /* It is necessary to trim this line. In other cases it's not so easy to match file properties */
            final String trimmedLine = StringHelper.reduceSpaces( child.getTextContent() );
            list.add( trimmedLine );
        }
    }


    public Element getJobSettings(Document doc)
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
            Element sideA = _SideA.getJobSettings(doc);
            sideARoot.appendChild(sideA);
            root.appendChild(sideARoot);
        }

        // Path B
        if(_SideB != null)
        {
            Element sideBRoot = doc.createElement(TAG_SideB);
            sideBRoot.setAttribute(ATTR_Type, _SideB.Type());
            Element sideB = _SideB.getJobSettings(doc);
            sideBRoot.appendChild(sideB);
            root.appendChild(sideBRoot);
        }

        // Sync Direction
        Element syncDir = doc.createElement(TAG_SyncDirection);
        syncDir.setTextContent(_Direction.toString());
        root.appendChild(syncDir);

        // One Way Sync Strategy
        Element ows = doc.createElement(TAG_OneWayStrategy);
        ows.setTextContent(_SingleStrategy.toString() );
        root.appendChild(ows);

        // Two Way Sync Strategy
        Element tws = doc.createElement(TAG_TwoWayStrategy);
        tws.setTextContent(_BoothStrategy.toString());
        root.appendChild(tws);

        // WhiteList
        Element wl = doc.createElement(TAG_WhiteList);
        getTextListNodes(_IncludeList, doc, wl);
        root.appendChild(wl);

        // Blacklist
        Element bl = doc.createElement(TAG_Blacklist);
        getTextListNodes(_ExcludeList, doc, bl);
        root.appendChild(bl);

        // Schedule
        Element schedule = doc.createElement(TAG_Schedule);
        schedule.setAttribute(ATTR_Schedule_Active, Boolean.toString(_Scheduling.Active()) );
        schedule.setAttribute(ATTR_Schedule_Hour, Integer.toString(_Scheduling.Hour()) );
        schedule.setAttribute(ATTR_Schedule_Minute, Integer.toString(_Scheduling.Minute()) );
        root.appendChild(schedule);

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


    /**
     * Reset status from finished to not started
     */
    public void resetStatusWhenFinished()
    {
        if(_status == SyncStatus.FINISH)
        {
            _status = SyncStatus.NOT_STARTED;
            _actNumber = 0;
            _maxNumber = 0;

            sendUpdate();
        }
    }



    private int _status = 0;
    public int getProcess(){ return _status; }
    private int _actNumber = 0;
    public  int getActNumber(){ return _actNumber; }
    public  int _maxNumber = 0;
    public int getMaxNumber(){ return _maxNumber; }

    private BaseAdapter _adapter;
    public void setAdapter(@Nullable BaseAdapter adapter){_adapter = adapter; }

    @Override
    protected Integer doInBackground(Context... params)
    {
        Context act = params[0];
        Log.d(L_TAG, "Start background job for job '" + Name() + "'");

        Do(act);

        return 0;
    }

    @Override
    protected void onProgressUpdate(Integer... params)
    {
        _status = params[0];
        _actNumber = params[1];
        _maxNumber = params[2];

        sendUpdate();
    }

    private void sendUpdate()
    {
        if(_adapter != null)
            _adapter.notifyDataSetChanged();
    }

    public static int getStatusText(int status)
    {
        switch(status)
        {
            case SyncStatus.NOT_STARTED:
                return R.string.SyncStatus_NotStarted;
            case SyncStatus.CONNECT_FILES_A:
                return R.string.SyncStatus_ConnectA;
            case SyncStatus.CONNECT_FILES_B:
                return R.string.SyncStatus_ConnectB;
            case SyncStatus.READ_FILES_A:
                return R.string.SyncStatus_ReadFilesA;
            case SyncStatus.READ_FILES_B:
                return R.string.SyncStatus_ReadFilesB;
            case SyncStatus.ANALYSE_FILES:
                return R.string.SyncStatus_AnalyseFiles;
            case SyncStatus.CREATE_JOB:
                return R.string.SyncStatus_CreateJobs;
            case SyncStatus.COPY_FILES:
                return R.string.SyncStatus_CopyFiles;
            case SyncStatus.FINISH:
                return R.string.SyncStatus_Finish;

            default:
                {
                    final String strError = "No text for status '" + status + "' available";
                    Log.e(L_TAG, strError);
                    throw new IllegalStateException(strError);
                }
        }
    }


    @Override
    protected void onPostExecute(Integer result)
    {
        super.onPostExecute(result);
    }


    private void Do(Context context) throws IllegalStateException
    {
        if(_SideA == null)
            throw new IllegalStateException("Connection A is null");
        if(_SideB == null)
            throw new IllegalStateException("Connection B is null");

        _SideA.RequestPermissions(context);

        _SideB.RequestPermissions(context);

        publishProgress(SyncStatus.CONNECT_FILES_A, 0, 0);
        _SideA.Connect(context);
        publishProgress(SyncStatus.CONNECT_FILES_B, 0, 0);
        _SideB.Connect(context);

        ArrayList<AnalyzeResult> whitelist = AnalyzeHelper.prepareList(_IncludeList);
        ArrayList<AnalyzeResult> blacklist = AnalyzeHelper.prepareList(_ExcludeList);

        publishProgress(SyncStatus.READ_FILES_A, 0, 0);
        ArrayList<FileItem> FilesA = _SideA.getFileList();
        ArrayList<FileItem> FilteredFilesA = AnalyzeHelper.filterFileList(FilesA, blacklist, whitelist);

        publishProgress(SyncStatus.READ_FILES_B, 0, 0);
        ArrayList<FileItem> FilesB = _SideB.getFileList();
        ArrayList<FileItem> FilteredFilesB = AnalyzeHelper.filterFileList(FilesB, blacklist, whitelist);

        publishProgress(SyncStatus.ANALYSE_FILES, 0, 0);
        ArrayList<FileMergeResult> MergedFiles = FileItemHelper.mergeFileList(FilteredFilesA, FilteredFilesB, _Direction, _SingleStrategy);
        publishProgress(SyncStatus.CREATE_JOB, 0, 0);
        ArrayList<DoingList> JobList = ApplyStrategy(MergedFiles);

        Apply(JobList, _SideA, _SideB);

        _SideA.Disconnect();
        _SideB.Disconnect();

        publishProgress(SyncStatus.FINISH, 1, 1);
    }


    private void throwIllegalState(DoingList doing)
    {
        final String strError = "Invalid combination " +
                                "A:'" + doing.SideA.Type.toString() + "' and " +
                                "B:'" + doing.SideB.Type.toString() + "'";
        Log.e(L_TAG, strError);
        throw new IllegalStateException(strError);
    }

    protected void Apply(ArrayList<DoingList> list, IConnection conSideA, IConnection conSideB)
    {
        int success = 0;
        int errors = 0;
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
                            result = conSideB.Move(FileItemHelper.concatPath(job.SideB.File.RelativePath, job.SideB.File.FileName), job.SideB.Param);
                            break;

                        case Delete:
                            result = conSideB.Delete(FileItemHelper.concatPath(job.SideB.File.RelativePath, job.SideB.File.FileName));
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
                    result = conSideA.Move(FileItemHelper.concatPath(job.SideA.File.RelativePath, job.SideA.File.FileName), job.SideA.Param);
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
                    result = conSideA.Delete(FileItemHelper.concatPath(job.SideA.File.RelativePath, job.SideA.File.FileName));
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
                errors++;
            }
            else
            {
                publishProgress(SyncStatus.COPY_FILES, success, list.size());
            }
        }

        if(errors > 0)
            Log.e(L_TAG, "Finished sync (" + _Name + ") with " + errors + " errors");

        Log.d(L_TAG, "Finished sync (" + _Name + ")");
    }


    private boolean Copy(DoingSide Source, DoingSide Target, IConnection conSource, IConnection conTarget)
    {
        try
        {
            File tempFile = File.createTempFile("temporaery", ".tmp");
            final String strFileSource = FileItemHelper.concatPath(Source.File.RelativePath, Source.File.FileName);

            conSource.Read(strFileSource, tempFile);
            conTarget.Write(tempFile, Target.File);

            tempFile.delete();
        }
        catch(Exception e)
        {
            Log.e(L_TAG, e.getStackTrace().toString());
            e.printStackTrace();
            return false;
        }
        return true;

    }


    protected ArrayList<DoingList> ApplyStrategy(ArrayList<FileMergeResult> MergedFiles)
    {
        ArrayList<DoingList> result = new ArrayList<>();

        String prefixPathA = "";
        String prefixPathB = "";

        if(_SingleStrategy == OneWayStrategy.AllFilesInDateFolder || _SingleStrategy == OneWayStrategy.NewFilesInDateFolder)
        {
            if(_Direction == SyncDirection.ToA)
            {
                prefixPathA = FileItemHelper.concatPath("/", Date.getDate());
            }
            else if(_Direction == SyncDirection.ToB)
            {
                prefixPathB = FileItemHelper.concatPath("/", Date.getDate());
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
                        temp.SideA.File = res.FileA.clone();
                        temp.SideA.File.RelativePath = prefixPathA + temp.SideA.File.RelativePath;
                        temp.SideB.File = res.FileA.clone();
                        temp.SideB.File.RelativePath = prefixPathB + temp.SideB.File.RelativePath;
                        result.add(temp);
                    }
                    break;

                case DateChanged_BNewer:
                    if(_Direction == SyncDirection.ToA || _Direction == SyncDirection.Booth)
                    {
                        DoingList temp = new DoingList();
                        temp.SideA.Type = JobType.Write;
                        temp.SideB.Type = JobType.Read;
                        temp.SideA.File = res.FileB.clone();
                        temp.SideA.File.RelativePath = prefixPathA + temp.SideA.File.RelativePath;
                        temp.SideB.File = res.FileB.clone();
                        temp.SideB.File.RelativePath = prefixPathB + temp.SideB.File.RelativePath;
                        result.add(temp);
                    }
                    break;

                case Renamed_A: // fall through
                case Moved_A:
                    {
                        DoingList temp = new DoingList();
                        temp.SideA.Type = JobType.Move;
                        temp.SideA.File = res.FileA.clone();
                        temp.SideA.File.RelativePath = prefixPathA + res.FileA.RelativePath;
                        temp.SideA.File.Modified = res.FileB.Modified;
                        temp.SideA.Param= prefixPathA + res.FileB.RelativePath + res.FileB.FileName;
                        result.add(temp);
                    }
                    break;

                case Renamed_B: // fall through
                case Moved_B:
                    {
                        DoingList temp = new DoingList();
                        temp.SideB.Type = JobType.Move;
                        temp.SideB.File = res.FileB.clone();
                        temp.SideB.File.RelativePath = prefixPathB + temp.SideB.File.RelativePath;
                        temp.SideB.File.Modified = res.FileA.Modified;
                        temp.SideB.Param= prefixPathB + res.FileA.RelativePath + res.FileA.FileName;
                        result.add(temp);
                    }
                    break;

                case NewFile:
                    if(res.FileA == null)
                    {
                        DoingList temp = new DoingList();
                        temp.SideB.Type = JobType.Read;
                        temp.SideB.File = res.FileB.clone();
                        temp.SideB.File.RelativePath = prefixPathB + temp.SideB.File.RelativePath;

                        temp.SideA.Type = JobType.Write;
                        temp.SideA.File = res.FileB.clone();
                        temp.SideA.File.RelativePath = prefixPathA + temp.SideA.File.RelativePath;
                        result.add(temp);
                    }
                    else
                    {
                        DoingList temp = new DoingList();
                        temp.SideA.Type = JobType.Read;
                        temp.SideA.File = res.FileA.clone();
                        temp.SideA.File.RelativePath = prefixPathA + temp.SideA.File.RelativePath;

                        temp.SideB.Type = JobType.Write;
                        temp.SideB.File = res.FileA.clone();
                        temp.SideB.File.RelativePath = prefixPathB + temp.SideB.File.RelativePath;
                        result.add(temp);
                    }
                    break;
            }
        }

        return result;
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

    private OneWayStrategy _SingleStrategy = OneWayStrategy.Standard;
    public OneWayStrategy StrategyOneWay() { return _SingleStrategy; }
    public void StrategyOneWay(OneWayStrategy Strategy) { _SingleStrategy = Strategy; }

    private TwoWayStrategy _BoothStrategy = TwoWayStrategy.BWins;
    public TwoWayStrategy StrategyTwoWay() { return _BoothStrategy; }
    public void StrategyTwoWay(TwoWayStrategy Strategy){ _BoothStrategy = Strategy; }


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


    /**
     * Return the Scheduler information
     * @return Scheduler information
     */
    public SchedulerInfo Scheduler(){ return _Scheduling; }


    /**
     * Set Scheduler information
     * @param sinfo Scheduler information
     */
    public void Scheduler(@NonNull SchedulerInfo sinfo) { _Scheduling = sinfo; }

    public class SyncStatus
    {
        public static final int NOT_STARTED = 0;
        public static final int CONNECT_FILES_A = 1;
        public static final int CONNECT_FILES_B = 2;
        public static final int READ_FILES_A = 3;
        public static final int READ_FILES_B = 4;
        public static final int ANALYSE_FILES = 5;
        public static final int CREATE_JOB = 6;
        public static final int COPY_FILES = 7;
        public static final int FINISH = 10;
    }
}
