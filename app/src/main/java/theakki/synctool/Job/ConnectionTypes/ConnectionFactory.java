package theakki.synctool.Job.ConnectionTypes;

import org.w3c.dom.Element;

import theakki.synctool.Job.IConnection;

/**
 * Created by theakki on 27.03.18.
 */

public class ConnectionFactory
{
    public static IConnection create(String Name, Element Node)
    {
        if(Name.compareToIgnoreCase(LocalPath.getType()) == 0)
        {
            return new LocalPath(Node);
        }
        else if(Name.compareToIgnoreCase(FTPConnection.getType()) == 0)
        {
            return new FTPConnection(Node);
        }
        else if(Name.compareToIgnoreCase(OwnCloud.getType()) == 0)
        {
            return new OwnCloud(Node);
        }
        return null;
    }

    public static ConnectionTypes connectiontypeFromString(String type, ConnectionTypes def)
    {
        if(type.compareToIgnoreCase(ConnectionTypes.Local.toString()) == 0)
        {
            return ConnectionTypes.Local;
        }else if(type.compareToIgnoreCase(ConnectionTypes.FTP.toString())  == 0)
        {
            return ConnectionTypes.FTP;
        }
        else if(type.compareToIgnoreCase(ConnectionTypes.OwnCloud.toString()) == 0)
        {
            return ConnectionTypes.OwnCloud;
        }

        return def;
    }

}
