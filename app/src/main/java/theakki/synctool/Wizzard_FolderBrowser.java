package theakki.synctool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.io.File;

import theakki.synctool.Data.StringTree;
import theakki.synctool.Helper.FileItemHelper;
import theakki.synctool.View.TreeItemHolder;

/**
 * Class to show a page with a folder tree.
 * @author theakki
 * @since 0.1
 */
public class Wizzard_FolderBrowser extends AppCompatActivity
{
    public static final String EXTRA_RECEIVE_FOLDERS = "Folders";
    public static final String EXTRA_RECEIVE_PATH_OFFSET = "PathOffset";
    public static final String EXTRA_SEND_SELECTED = "SelectedFolder";

    private TreeNode _treeData;

    private Button _buttonCancel;
    private Button _buttonOk;
    private TextView _txtSelectedPath;

    private String _SelectedPath = "/";
    private String _PathOffset = "/";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Get the view from new_activity.xml
        setContentView(R.layout.activity_folderbrowser);
        Bundle extras = getIntent().getExtras();

        String pathOffset = extras.getString(EXTRA_RECEIVE_PATH_OFFSET);
        if(pathOffset != null)
            _PathOffset = pathOffset;

        StringTree data = extras.getParcelable(EXTRA_RECEIVE_FOLDERS);
        _treeData = transform(data);

        // Button Cancel
        _buttonCancel = findViewById(R.id.btn_Cancel);
        _buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelClick();
            }
        });


        // Button Ok
        _buttonOk = findViewById(R.id.btn_Ok);
        _buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOkClick();
            }
        });

        // Textedit Selected Path
        _txtSelectedPath = findViewById(R.id.txt_selectedPath);
        _txtSelectedPath.setText(_SelectedPath);


        AndroidTreeView treeView = new AndroidTreeView(this, _treeData);
        treeView.setUse2dScroll(true);
        treeView.setDefaultAnimation(true);
        treeView.setDefaultViewHolder(TreeItemHolder.class);

        treeView.setDefaultNodeClickListener(new TreeNode.TreeNodeClickListener() {
            @Override
            public void onClick(TreeNode node, Object value) {
                onItemClicked(node, value);
            }
        });

        FrameLayout fl = findViewById(R.id.fl_browser);
        fl.addView(treeView.getView());
    }


    private void onItemClicked(TreeNode node, Object value)
    {
        boolean isSelected = node.isSelected();
        node.setSelected(!isSelected);

        //int i = 0;

        TreeItemHolder.TreeItem item = (TreeItemHolder.TreeItem) value;
        _SelectedPath = item.Path();
        _txtSelectedPath.setText(_SelectedPath);
    }


    private void onCancelClick()
    {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }


    private void onOkClick()
    {
        Intent intentOk = new Intent();
        intentOk.putExtra(EXTRA_SEND_SELECTED, FileItemHelper.concatPath(_PathOffset, _SelectedPath));
        setResult(Activity.RESULT_OK, intentOk);
        finish();
    }


    private TreeNode transform(StringTree st)
    {
        TreeNode root = TreeNode.root();
        transform(root, st, File.separator, 0);
        return root;
    }


    private void transform(TreeNode parent, StringTree st, String path, int indent)
    {
        for(StringTree s : st)
        {
            final String folderName = s.getData();
            final String folderPath = FileItemHelper.concatPath(path, folderName);

            TreeNode tn = new TreeNode( new TreeItemHolder.TreeItem(folderName, folderPath, indent));
            transform(tn, s, folderPath, indent + 1);
            parent.addChild(tn);
        }
    }
}
