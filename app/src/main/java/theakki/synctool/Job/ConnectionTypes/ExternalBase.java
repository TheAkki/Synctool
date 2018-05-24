package theakki.synctool.Job.ConnectionTypes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by theakki on 27.03.18.
 */

public abstract class ExternalBase
{
    protected String _Username = "";
    protected String _Password = "";
    protected Boolean _PasswordStore = false;
    protected Boolean _PasswordIsSet = false;
    protected String _URL = "";
    protected String _LocalPath = "";
    protected int _Port = 0;

    final private String TAG_Username = "User";
    final private String TAG_Password = "Password";
    final private String ATTR_PasswordStore = "Store";
    final private String TAG_URL = "URL";
    final private String TAG_LocalPath = "LocalPath";
    final private String TAG_Port = "Port";


    protected void setValue(Element Node)
    {
        final String name = Node.getNodeName();
        if(name.compareToIgnoreCase(TAG_Username) == 0)
        {
            _Username = Node.getTextContent();
        }
        else if(name.compareToIgnoreCase(TAG_Password) == 0)
        {
            _Password = Node.getTextContent();
            _PasswordIsSet = true;
            String strAttrStore = Node.getAttribute(ATTR_PasswordStore);
            if(strAttrStore.compareToIgnoreCase("true") == 0)
                _PasswordStore = true;
        }
        else if(name.compareToIgnoreCase(TAG_URL) == 0)
        {
            _URL = Node.getTextContent();
        }
        else if(name.compareToIgnoreCase(TAG_LocalPath) == 0)
        {
            _LocalPath = Node.getTextContent();
        }
        else if(name.compareToIgnoreCase(TAG_Port) == 0)
        {
            _Port = Integer.parseInt(Node.getTextContent());
        }
        else
        {
            throw new IllegalArgumentException("Unknown Node '" + name + "'");
        }
    }

    protected void appendSettings(Document doc, Element node)
    {
        // Username
        Element username = doc.createElement(TAG_Username);
        username.setTextContent(_Username);
        node.appendChild(username);

        // Password
        if(_PasswordStore && _PasswordIsSet)
        {
            Element passwd = doc.createElement(TAG_Password);
            passwd.setTextContent(_Password);
            node.appendChild(passwd);
        }

        // Url
        Element url = doc.createElement(TAG_URL);
        url.setTextContent(_URL);
        node.appendChild(url);

        // Local Path
        Element localPath = doc.createElement(TAG_LocalPath);
        localPath.setTextContent(_LocalPath);
        node.appendChild(localPath);

        // Port
        Element port = doc.createElement(TAG_Port);
        port.setTextContent(String.valueOf(_Port));
        node.appendChild(port);
    }

    public String User() {return  _Username; }
    public void User(String user) { _Username = user; }

    public String Password() {return _Password;}
    public void Password(String password) { _Password = password; }

    public String Url() { return _URL; }
    public void Url(String url){ _URL = url; }

    public String LocalPath(){ return _LocalPath; }
    public void LocalPath(String path){ _LocalPath = path; }

    public int Port(){ return _Port; }
    public void Port(int port){ _Port = port; }
}
