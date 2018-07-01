package theakki.synctool.System;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import theakki.synctool.Job.JobHandler;
import theakki.synctool.Job.NamedConnectionHandler;

public class ImportExport
{
    private static final String L_Tag = ImportExport.class.getSimpleName();

    // Singleton
    private static final ImportExport ourInstance = new ImportExport();
    public static ImportExport getInstance() {
        return ourInstance;
    }

    // XML
    private static final String XML_TAG_ROOT = "SynctoolConfig";
    private static final String XML_TAG_CONNECTIONS = "Connections";
    private static final String XML_TAG_JOBS = "Jobs";
    private static final String XML_TAG_SETTINGS = "Settings";


    private ImportExport()
    {
    }

    public void Export(Context context, Uri uri, boolean exportWithPasswords)
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();
            Document doc = parser.newDocument();

            // ROOT - Element
            Element root = doc.createElement(XML_TAG_ROOT);

            // Jobs
            Element jobs = doc.createElement(XML_TAG_JOBS);
            Element exportedJobs = JobHandler.getInstance().getJobs(doc);
            jobs.appendChild(exportedJobs);
            root.appendChild(jobs);

            // Settings
            Element settings = doc.createElement(XML_TAG_SETTINGS);
            Element exportedSettings = SettingsHandler.getInstance().getSettings(doc);
            settings.appendChild(exportedSettings);
            root.appendChild(settings);

            // Connections
            Element connections = doc.createElement(XML_TAG_CONNECTIONS);
            Element exportedConnections = NamedConnectionHandler.getInstance().getConnections(doc, !exportWithPasswords);
            connections.appendChild(exportedConnections);
            root.appendChild(connections);

            doc.appendChild(root);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
            transformer.transform(new DOMSource(doc), new StreamResult(outputStream));
        }
        catch (Exception e)
        {
            Log.e(L_Tag, e.getStackTrace().toString());
            e.printStackTrace();
        }
    }


    public void Import(Context context, Uri uri)
    {
        Element node;

        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbBuild = dbFactory.newDocumentBuilder();

            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            InputSource source = new InputSource(inputStream);
            Document doc = dbBuild.parse(source);
            node = doc.getDocumentElement();

        }
        catch (Exception e)
        {
            Log.e(L_Tag, e.getStackTrace().toString());
            e.printStackTrace();
            return;
        }

        importNode(node);
    }


    private void importNode(Element node)
    {
        final String name = node.getTagName();
        if(name.compareToIgnoreCase(XML_TAG_ROOT) != 0)
        {
            final String strErrorMessage = "Name of Node '" + name + "' not expected";
            Log.e(L_Tag, strErrorMessage);
            throw new IllegalArgumentException(strErrorMessage);
        }

        NodeList children = node.getChildNodes();
        for(int i = 0; i < children.getLength(); ++i)
        {
            Element child = (Element) children.item(i);
            final String childName = child.getTagName();

            NodeList childChildren = child.getChildNodes();
            final int iCountChildChildren = childChildren.getLength();

            if(iCountChildChildren != 1)
            {
                final String errorMessage = "Node '" + childName + "' has " + iCountChildChildren + " children insteand of 1";
                Log.e(L_Tag, errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }

            Element childChild = (Element) childChildren.item(0);


            if(XML_TAG_CONNECTIONS.compareToIgnoreCase(childName) == 0)
            {
                NamedConnectionHandler.getInstance().setup(childChild, true);
            }
            else if(XML_TAG_JOBS.compareToIgnoreCase(childName) == 0)
            {
                JobHandler.getInstance().setup(childChild, true);
            }
            else if(XML_TAG_SETTINGS.compareToIgnoreCase(childName) == 0)
            {
                SettingsHandler.getInstance().setup(childChild);
            }
            else
            {
                final String strErrorMessage = "Name of Node '" + childName + "' not expected/implemented";
                Log.e(L_Tag, strErrorMessage);
                throw new IllegalArgumentException(strErrorMessage);
            }
        }
    }
}
