package theakki.synctool.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;

import theakki.synctool.R;

/**
 * This class handle Folder items in tree view
 * @author theakki
 * @since 0.1
 */
public class TreeItemHolder extends TreeNode.BaseNodeViewHolder<TreeItemHolder.TreeItem>
{

    /**
     * Constructor
     * @param context Context of application
     */
    public TreeItemHolder(Context context)
    {
        super(context);
    }


    @Override
    public View createNodeView(TreeNode node, TreeItem value)
    {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.treeview_folder, null, false);
        TextView tvValue = view.findViewById(R.id.txt_Foldername);
        tvValue.setText(value.Name());
        tvValue.setPadding(value.Deep() * 30, 5, 0, 5);

        return view;
    }

    /**
     * This class collect information about a folder item in
     * @author theakki
     * @since 0.1
     */
    public static class TreeItem
    {
        private String _Name;
        private String _Path;
        private int _Deep;


        /**
         * Constructor
         * @param name  Folder name
         * @param path  Folder path
         * @param deep  Deep in structure
         */
        public TreeItem(String name, String path, int deep)
        {
            _Name = name;
            _Path = path;
            _Deep = deep;
        }


        /**
         * Constructor
         * @param name Folder name
         * @param path Folder poth
         */
        public TreeItem(String name, String path)
        {
            _Name = name;
            _Path = path;
            _Deep = 0;
        }


        /**
         * Return the folder name
         * @return Folder name
         */
        public String Name(){ return _Name; }


        /**
         * Return the path
         * @return Path
         */
        public String Path(){ return _Path; }


        /**
         * Return the deep in the tree
         * @return Deep
         */
        public int Deep(){ return _Deep; }


        @Override
        public String toString() {
            return _Name;
        }
    }
}



