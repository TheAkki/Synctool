package theakki.synctool.System;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import theakki.synctool.Helper.FileItemHelper;
import theakki.synctool.Receiver.BootEndReceiver;

public class SettingsHandler
{
    // LogCat
    private final String L_Tag = SettingsHandler.class.getSimpleName();

    // XML
    private final static String TAG_Name = "Settingshandler";
    private final static String TAG_Settings = "Settings";
    private final static String TAG_StartOnBootEnd = "StartBootOnEnd";
    public static final String DEFAULT_SETTINGS = "<" + TAG_Name + "/>";

    // Singleton
    private static final SettingsHandler ourInstance = new SettingsHandler();
    public static SettingsHandler getInstance() {
        return ourInstance;
    }

    // Member
    private Context _Context;


    private SettingsHandler()
    {
    }

    public void init(Context context)
    {
        _Context = context;
    }

    public String getData()
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();
            Document doc = parser.newDocument();

            Element root = getSettings(doc);

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

    public void setup(String Settings)
    {
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

        loadSettingHandler(Node);
    }

    public void setup(Element Node)
    {
        loadSettingHandler(Node);
    }


    private void loadSettingHandler(Element node)
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
            final String strChildName = child.getTagName();

            if(TAG_Settings.compareToIgnoreCase(strChildName) == 0)
            {
                loadSettings(child);
            }
            else
            {
                Log.e(L_Tag, "Node with name '" + strChildName + "' not implemented");
            }
        }
    }


    private void loadSettings(Element node)
    {
        NodeList children = node.getChildNodes();
        for(int i = 0; i < children.getLength(); ++i)
        {
            Element child = (Element) children.item(i);
            final String strChildName = child.getTagName();

            if(TAG_StartOnBootEnd.compareToIgnoreCase(strChildName) == 0)
            {
                _bStartOnBootEnd = Boolean.parseBoolean( child.getTextContent() );
            }
            else
            {
                Log.e(L_Tag, "Node with name '" + strChildName + "' not implemented");
            }
        }
    }


    public Element getSettings(Document doc)
    {
        Element root = doc.createElement(TAG_Name);

        Element settings = doc.createElement(TAG_Settings);
        getSettings(doc, settings);
        root.appendChild(settings);

        return root;
    }


    private void getSettings(Document doc, Element elementRoot)
    {
        // Start on Boot end
        Element sobe = doc.createElement(TAG_StartOnBootEnd);
        sobe.setTextContent(String.valueOf(_bStartOnBootEnd));
        elementRoot.appendChild(sobe);
    }


    private boolean _bStartOnBootEnd = false;
    public void StartWithBootEnd(boolean value)
    {
        _bStartOnBootEnd = value;
        configureStartWithBootEnd(value);
    }
    public boolean StartWithBootEnd() { return _bStartOnBootEnd; }



    private void configureStartWithBootEnd(boolean enabled)
    {
        Log.d(L_Tag, "Set BootEndReceiver to : " + enabled);

        int flag=(enabled ?
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED);

        ComponentName component = new ComponentName(_Context.getApplicationContext(), BootEndReceiver.class);

        _Context.getPackageManager().setComponentEnabledSetting(component, flag, PackageManager.DONT_KILL_APP);

        int b = _Context.getPackageManager().getComponentEnabledSetting(component);
    }

    private final static String ApplicationFolder = "SyncTool";
    public String getApplicationDataPath()
    {
        return FileItemHelper.concatPath( Environment.getExternalStorageDirectory().getAbsolutePath(),  ApplicationFolder);
    }


}
