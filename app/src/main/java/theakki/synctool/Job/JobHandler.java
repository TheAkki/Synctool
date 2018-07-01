package theakki.synctool.Job;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import theakki.synctool.Job.Scheduler.SchedulerInfo;

/**
 * This class collect all synchronization jobs.
 * @author theakki
 * @since 0.1
 */
public class JobHandler
{
    private final static String L_Tag = JobHandler.class.getSimpleName();

    private final static String TAG_Name = "JobHandler";
    private final static String TAG_Settings = "Settings";

    public static final String DEFAULT_SETTINGS = "<" + TAG_Name + "/>";

    // Singleton
    private static final JobHandler ourInstance = new JobHandler();
    public static JobHandler getInstance()
    {
        return ourInstance;
    }


    private JobHandler()
    {
    }

    private boolean _initDone = false;


    public void setup(String Settings, boolean forceReload)
    {
        if(forceReload)
        {
            _Jobs.clear();
            _initDone = false;
        }

        if(_initDone == true)
        {
            Log.d(L_Tag, "Init already done");
            return;
        }

        Element Node;

        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbBuild = dbFactory.newDocumentBuilder();
            InputSource source = new InputSource(new StringReader(Settings));
            Document doc = dbBuild.parse(source);
            Node = doc.getDocumentElement();

        }
        catch (Exception e)
        {
            Log.e(L_Tag, e.getStackTrace().toString());
            e.printStackTrace();
            return;
        }

        loadNode(Node);

    }

    public void setup(Element Node, boolean forceReload)
    {
        if(forceReload)
        {
            _Jobs.clear();
            _initDone = false;
        }

        if(_initDone == true)
        {
            Log.d(L_Tag, "Init already done");
            return;
        }

        loadNode(Node);
    }


    private void loadNode(Element node)
    {
        final String name = node.getTagName();
        if(name.compareToIgnoreCase(TAG_Name) != 0)
        {
            final String strErrorMessage = "Name of Node '" + name + "' not expected";
            Log.e(L_Tag, strErrorMessage);
            throw new IllegalArgumentException(strErrorMessage);
        }

        NodeList children = node.getChildNodes();
        for(int i = 0; i < children.getLength(); ++i)
        {
            Element child = (Element) children.item(i);
            SyncJob job = new SyncJob(child);
            if(job == null)
            {
                final String strErrorMessage = "Job is not created";
                Log.e(L_Tag, strErrorMessage);
                throw new IllegalStateException(strErrorMessage);
            }

            _Jobs.add(job);
        }
    }



    @Nullable
    public static SyncJob getJob(String settings)
    {
        Element Node;

        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbBuild = dbFactory.newDocumentBuilder();
            InputSource source = new InputSource(new StringReader(settings));
            Document doc = dbBuild.parse(source);
            Node = doc.getDocumentElement();

        }
        catch (Exception e)
        {
            Log.e(L_Tag, e.getStackTrace().toString());
            e.printStackTrace();
            return null;
        }

        final String name = Node.getTagName();
        if(name.compareToIgnoreCase(TAG_Settings) != 0)
        {
            final String strErrorMessage = "Name of Node '" + name + "' not expected";
            Log.e(L_Tag, strErrorMessage);
            throw new IllegalArgumentException(strErrorMessage);
        }

        return new SyncJob( (Element) Node.getChildNodes().item(0) );
    }


    public String getData()
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();
            Document doc = parser.newDocument();

            Element root = getJobs(doc);

            doc.appendChild(root);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            return writer.getBuffer().toString();
        }
        catch (Exception e)
        {
            Log.e(L_Tag, e.getStackTrace().toString());
            e.printStackTrace();
            return "";
        }
    }


    public Element getJobs(Document doc)
    {
        Element root = doc.createElement(TAG_Name);

        for (SyncJob Job : _Jobs)
        {
            Element child = Job.getJobSettings(doc);
            root.appendChild(child);
        }

        return root;
    }


    @NonNull
    public static String getSettings(SyncJob job)
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();
            Document doc = parser.newDocument();
            Element root = doc.createElement(TAG_Settings);

            Element child = job.getJobSettings(doc);
            root.appendChild(child);

            doc.appendChild(root);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            return writer.getBuffer().toString();
        }
        catch(TransformerException e)
        {
            Log.e(L_Tag, e.getStackTrace().toString());
            e.printStackTrace();
        }
        catch(ParserConfigurationException e)
        {
            Log.e(L_Tag, e.getStackTrace().toString());
            e.printStackTrace();
        }
        catch (Exception e)
        {
            Log.e(L_Tag, e.getStackTrace().toString());
            e.printStackTrace();
            throw e;
        }
        return "";
    }


    public void Do(Activity context, boolean onlyActive)
    {
        for(SyncJob job : _Jobs)
        {
            if(onlyActive && job.Active() == false)
                continue;
            job.execute(context);
        }
    }


    public SyncJob getByName(String name)
    {
        for(SyncJob job : _Jobs)
        {
            if(job.Name().compareTo(name) == 0)
                return job;
        }

        final String strError = "Job with name '" + name + "' not found";
        Log.e(L_Tag, strError);
        throw new Resources.NotFoundException(strError);
    }


    public ArrayList<SyncJob> getByScheduler(SchedulerInfo schedulerInfo, boolean onlyActive)
    {
        ArrayList<SyncJob> result = new ArrayList<>();
        for(SyncJob job : _Jobs)
        {
            if(job.Active() == false && onlyActive)
                continue;

            if(job.Scheduler().equal(schedulerInfo))
            {
                result.add(job);
            }
        }
        return result;
    }


    public ArrayList<JobInfo> getJobInfo()
    {
        ArrayList<JobInfo> result = new ArrayList<>();
        for(SyncJob job : _Jobs)
        {
            result.add( job.getJobInfo() );
        }
        return result;
    }


    public boolean existJobByName(String name)
    {
        for(SyncJob job : _Jobs)
        {
            if(job.Name().compareToIgnoreCase(name) == 0)
                return true;
        }
        return false;
    }


    public boolean removeJobByName(String name)
    {
        for(int i = 0; i < _Jobs.size(); ++i)
        {
            final SyncJob job = _Jobs.get(i);
            if(job.Name().equals(name))
            {
                _Jobs.remove(i);
                return true;
            }
        }
        return false;
    }


    /**
     * Reset the stati of all finished jobs
     */
    public void resetStatusWhenFinished()
    {
        for(SyncJob job : _Jobs)
        {
            job.resetStatusWhenFinished();
        }
    }


    public boolean isSchedulingActive()
    {
        boolean result = false;
        for(SyncJob job : _Jobs)
        {
            result = result || job.Scheduler().Active();
        }
        return result;
    }


    public ArrayList<SchedulerInfo> getSchedulers(boolean onlyActive)
    {
        ArrayList<SchedulerInfo> result = new ArrayList<>();
        for(SyncJob job : _Jobs)
        {
            if(job.Active() == false && onlyActive)
                continue;

            result.add( job.Scheduler() );
        }
        return result;
    }


    public void addJob(SyncJob job)
    {
        _Jobs.add(job);
    }

    public boolean updateJob(String name, SyncJob job)
    {
        for(int i = 0; i < _Jobs.size(); ++i)
        {
            if(_Jobs.get(i).Name().equals(name))
            {
                _Jobs.set(i, job);
                return true;
            }
        }

        Log.e(L_Tag, "Job with name '" + name + "' not found" );
        return false;
    }


    private ArrayList<SyncJob> _Jobs = new ArrayList<>();
}
