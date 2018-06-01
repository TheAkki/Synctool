package theakki.synctool.Data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class handle a tree with string elements
 * @author theakki
 * @since 0.1
 */
public class StringTree implements Parcelable, Iterable<StringTree>
{
    private String  _content;
    private ArrayList<StringTree> _children;


    /**
     * Constructor
     * @param value Value
     */
    public StringTree(String value)
    {
        _content = value;
        _children = new ArrayList<>();
    }


    /**
     * Constructor to create class from Parcel
     * @param in
     */
    @SuppressWarnings("unchecked")
    public StringTree(Parcel in)
    {
        _content = in.readString();
        _children = in.readArrayList(StringTree.class.getClassLoader());
    }

    /**
     * Return the Content of Node
     * @return Content
     */
    public String getData()
    {
        return _content;
    }


    /**
     * Add child node
     * @param child Child
     */
    public void add(StringTree child)
    {
        _children.add(child);
    }


    /**
     * Add a list of strings in the tree. Is a necessary parent node not existing it will be created
     * @param list List with tree elements
     */
    public void include(String[] list)
    {
        StringTree actual = this;

        for(String s : list)
        {
            if(s.length() == 0)
                continue;

            boolean bFound = false;
            for(StringTree child : actual)
            {
                if(child._content.equals(s))
                {
                    bFound = true;
                    actual = child;
                    break;
                }
            }
            if(bFound == false)
            {
                StringTree n = new StringTree(s);
                actual.add(n);
                actual = n;
            }
        }
    }


    @Override
    public int describeContents()
    {
        // ToDO: Not sure what is to do
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(_content);
        dest.writeList(_children);
    }


    /**
     * Creator element for parceling
     */
    public static Creator<StringTree> CREATOR = new Creator<StringTree>()
    {

        @Override
        public StringTree createFromParcel(Parcel source) {
            return new StringTree(source);
        }

        @Override
        public StringTree[] newArray(int size) {
            return new StringTree[size];
        }
    };


    /**
     * Return a Iterator for this class to iterate over all children
     * @return Iterator
     */
    public Iterator<StringTree> iterator()
    {
        return new TreeIterator();
    }


    /**
     * This class provide an iterator for all children of the StringTree
     * @author theakki
     * @since 0.1
     */
    public class TreeIterator implements Iterator<StringTree>
    {
        int _current = 0;

        @Override
        public boolean hasNext()
        {
            return  _current < _children.size();
        }


        @Override
        public StringTree next()
        {
            if(hasNext() == false)
                throw new NoSuchElementException();
            return _children.get(_current++);
        }
    }
}
