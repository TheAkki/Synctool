package theakki.synctool.Job;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

/**
 * Created by theakki on 26.03.18.
 */

public class JobHandler
{
    private final String TAG_Name = "JobHandler";
    private final static String TAG_Settings = "Settings";

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
            e.printStackTrace();
            return;
        }

        final String name = Node.getTagName();
        if(name.compareToIgnoreCase(TAG_Name) != 0)
            throw new IllegalArgumentException("Name of Node '" + name + "' not expected");

        NodeList childs = Node.getChildNodes();
        for(int i = 0; i < childs.getLength(); ++i)
        {
            Element child = (Element) childs.item(i);
            SyncJob job = new SyncJob(child);
            if(job == null)
                throw new IllegalStateException("Job is not created");

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
            return null;
        }

        final String name = Node.getTagName();
        if(name.compareToIgnoreCase(TAG_Settings) != 0)
            throw new IllegalArgumentException("Name of Node '" + name + "' not expected");

        return  new SyncJob( (Element) Node.getChildNodes().item(0) );
    }


    public String getData()
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();
            Document doc = parser.newDocument();
            Element root = doc.createElement(TAG_Name);


            for (SyncJob Job : _Jobs)
            {
                Element child = Job.getSettings(doc);
                root.appendChild(child);
            }

            doc.appendChild(root);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            return writer.getBuffer().toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
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

            Element child = job.getSettings(doc);
            root.appendChild(child);

            doc.appendChild(root);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            return writer.getBuffer().toString();
        }
        catch(TransformerException e)
        {
            e.printStackTrace();
        }
        catch(ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
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
        throw new Resources.NotFoundException("Job with name '" + name + "' not found");
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


    public Boolean removeJobByName(String name)
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


    public void addJob(SyncJob job)
    {
        _Jobs.add(job);
    }

    private ArrayList<SyncJob> _Jobs = new ArrayList<>();
}
