package theakki.synctool.Job;

import android.support.annotation.Nullable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import theakki.synctool.Job.ConnectionTypes.ConnectionFactory;
import theakki.synctool.Job.ConnectionTypes.ConnectionTypes;

/**
 * This class define and handle a named connection
 * @author theakki
 * @since 0.1
 */
public class NamedConnectionHandler {

    /**
     * This class define parameter of a connection
     * @author theakki
     * @since 0.1
     */
    public static class Connection
    {
        public String Url;
        public ConnectionTypes Type;
        public String User;
        public String Password;
        public int Port = 0;
    }

    /**
     * This class define a short element for a list of connection type and name
     * @author theakki
     * @since 0.1
     */
    public static class Connections
    {
        public String Name;
        public ConnectionTypes Type;
    }


    /**
     * This class define a collection element of a Connection
     * @author theakki
     * @since 0.1
     */
    public class ConnectionContainer
    {
        /**
         * Connection parameter
         */
        public Connection Connection = new Connection();
        /**
         * This class define and handle a synchronization job.
         * @author theakki
         * @since 0.1
         */
        public boolean DontStore = true;
    }

    private final static String TAG_Name = "ConnectionHandler";
    private final static String TAG_Connection = "Connection";
    private final static String ATTR_Name = "Name";
    private final static String TAG_URL = "Url";
    private final static String TAG_User = "User";
    private final static String TAG_Password = "Password";
    private final static String TAG_TYPE = "Type";
    private final static String TAG_Port = "Port";


    public final static String DEFAULT_SETTINGS = "<" + TAG_Name + "/>";

    //private ArrayList<Connection> _Connections = new ArrayList<>();
    private Map<String, ConnectionContainer> _Connections = new HashMap<>();

    // Singleton
    private static final NamedConnectionHandler ourInstance = new NamedConnectionHandler();
    public static NamedConnectionHandler getInstance() {
        return ourInstance;
    }


    private NamedConnectionHandler() {
    }


    public void setup(String settings, boolean forceReload)
    {
        if(forceReload)
            _Connections.clear();

        if(settings.length() == 0)
            return;

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
            e.printStackTrace();
            return;
        }

        loadNode(Node);
    }

    public void setup(Element node, boolean forceReload)
    {
        if(forceReload)
            _Connections.clear();

        loadNode(node);
    }

    private void loadNode(Element node)
    {
        final String name = node.getTagName();
        if(name.compareToIgnoreCase(TAG_Name) != 0)
            throw new IllegalArgumentException("Name of Node '" + name + "' not expected");

        NodeList children = node.getChildNodes();
        for(int i = 0; i < children.getLength(); ++i)
        {
            Element child = (Element) children.item(i);

            ConnectionContainer container = new ConnectionContainer();

            final String childName = child.getTagName();
            if(childName.compareToIgnoreCase(TAG_Connection) != 0)
                throw new IllegalArgumentException("Name of Node '" + name + "' not expected");

            final String Name = child.getAttribute(ATTR_Name);

            NodeList connectionElements = child.getChildNodes();
            for(int j = 0; j < connectionElements.getLength(); ++j)
            {
                Element elem = (Element) connectionElements.item(j);
                final String elemName = elem.getTagName();

                if(TAG_URL.compareToIgnoreCase(elemName) == 0)
                {
                    container.Connection.Url = elem.getTextContent();
                }
                else if(TAG_User.compareToIgnoreCase(elemName) == 0)
                {
                    container.Connection.User = elem.getTextContent();
                }
                else if(TAG_Password.compareToIgnoreCase(elemName) == 0)
                {
                    container.Connection.Password = elem.getTextContent();
                    // Es war gespeichert, also wieder speichern.
                    container.DontStore = false;
                }
                else if(TAG_TYPE.compareToIgnoreCase(elemName) == 0)
                {
                    container.Connection.Type = ConnectionFactory.connectiontypeFromString(elem.getTextContent(), ConnectionTypes.Local);
                }
                else if(TAG_Port.compareToIgnoreCase(elemName) == 0)
                {
                    container.Connection.Port = Integer.parseInt(elem.getTextContent());
                }
            }

            if(container.Connection.Url == null)
                throw new IllegalArgumentException("Missing URL Node");

            _Connections.put(Name, container);
        }
    }


    public String getData()
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();
            Document doc = parser.newDocument();

            Element root = getConnections(doc, false);
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

    public Element getConnections(Document doc, boolean forceRemovePasswords)
    {
        Element root = doc.createElement(TAG_Name);

        for (Map.Entry<String, ConnectionContainer> entry : _Connections.entrySet())
        {
            // Connection
            Element elemConnection = doc.createElement(TAG_Connection);
            elemConnection.setAttribute(ATTR_Name, entry.getKey());
            root.appendChild(elemConnection);

            // Url
            Element elementUrl = doc.createElement(TAG_URL);
            elementUrl.setTextContent(entry.getValue().Connection.Url);
            elemConnection.appendChild(elementUrl);

            // User
            Element elementUser = doc.createElement(TAG_User);
            elementUser.setTextContent(entry.getValue().Connection.User);
            elemConnection.appendChild(elementUser);

            // Password
            if(entry.getValue().DontStore == false && forceRemovePasswords == false)
            {
                Element elementPassword = doc.createElement(TAG_Password);
                elementPassword.setTextContent(entry.getValue().Connection.Password);
                elemConnection.appendChild(elementPassword);
            }

            // Type
            Element elementType = doc.createElement(TAG_TYPE);
            elementType.setTextContent(entry.getValue().Connection.Type.toString());
            elemConnection.appendChild(elementType);

            // Port
            Element elementPort = doc.createElement(TAG_Port);
            elementPort.setTextContent( Integer.toString(entry.getValue().Connection.Port) );
            elemConnection.appendChild(elementPort);
        }

        return root;
    }


    public void add(String Name, Connection connection, boolean StorePassword)
    {
        ConnectionContainer con = new ConnectionContainer();
        con.Connection = connection;
        con.DontStore = !StorePassword;

        _Connections.put(Name, con);
    }

    @Nullable  public Connection getConnection(String Name)
    {
        if(_Connections.containsKey(Name))
            return _Connections.get(Name).Connection;
        return null;
    }


    public void update(String Name, Connection value)
    {
        ConnectionContainer con = _Connections.get(Name);
        con.Connection = value;
        _Connections.put(Name, con);
    }


    public Set<String> getKeys()
    {
        return _Connections.keySet();
    }


    public ArrayList<Connections> getConnections()
    {
        ArrayList<Connections> connections = new ArrayList<>();

        for(Map.Entry<String, ConnectionContainer> entries : _Connections.entrySet())
        {
            Connections temp = new Connections();
            temp.Name = entries.getKey();
            temp.Type = entries.getValue().Connection.Type;

            connections.add(temp);
        }
        return connections;
    }


    public ArrayList<Connections> getConnections(ConnectionTypes type)
    {
        ArrayList<Connections> connections = new ArrayList<>();

        for(Map.Entry<String, ConnectionContainer> entries : _Connections.entrySet())
        {
            if(entries.getValue().Connection.Type == type)
            {
                Connections temp = new Connections();
                temp.Name = entries.getKey();
                temp.Type = entries.getValue().Connection.Type;

                connections.add(temp);
            }
        }
        return connections;
    }


    public boolean existConnection(String connection)
    {
        for(String key : _Connections.keySet())
        {
            if(connection.compareToIgnoreCase(key) == 0)
                return true;
        }
        return false;
    }
}
