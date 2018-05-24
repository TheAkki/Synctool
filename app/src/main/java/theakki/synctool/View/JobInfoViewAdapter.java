package theakki.synctool.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import theakki.synctool.Job.JobHandler;
import theakki.synctool.Job.JobInfo;
import theakki.synctool.Job.Settings.SyncDirection;
import theakki.synctool.Job.SyncJob;
import theakki.synctool.R;

/**
 * Created by theakki on 06.04.18.
 */

public class JobInfoViewAdapter extends ArrayAdapter<JobInfo>
{

    private int _layoutResource;
    private boolean _useOnlyName;

    public JobInfoViewAdapter(Context context, int layoutResource, List<JobInfo> jobInfoList, boolean useOnlyName)
    {
        super(context, layoutResource, jobInfoList);
        _layoutResource = layoutResource;
        _useOnlyName = useOnlyName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(_layoutResource, null);
        }

        JobInfo item = getItem(position);
        JobInfo jobInfo;
        if(_useOnlyName)
            jobInfo = JobHandler.getInstance().getByName(item.Name).getJobInfo();
        else
            jobInfo = item;


        if (jobInfo != null) {
            TextView active = view.findViewById(R.id.txt_Active);
            TextView name = view.findViewById(R.id.txt_JobName);
            TextView status = view.findViewById(R.id.txt_Status);
            TextView act = view.findViewById(R.id.txt_ActNumber);
            TextView max = view.findViewById(R.id.txt_MaxNumber);
            ProgressBar prog = view.findViewById(R.id.pb_Status);

            //TextView centreTextView = (TextView) view.findViewById(R.id.centreTextView);

            if (active != null) {
                if(jobInfo.IsActive)
                    active.setText(R.string.JobInfo_Active);
                else
                    active.setText(R.string.JobInfo_NotActive);
            }
            if (name != null) {
                name.setText(jobInfo.Name);
            }
            if (status != null) {
                status.setText(SyncJob.getStatusText(jobInfo.Status));
            }
            if(act != null)
            {
                act.setText("" + jobInfo.ActiveElements);
            }
            if(max != null)
            {
                max.setText("" + jobInfo.MaxElements);
            }
            if(prog != null)
            {
                if(jobInfo.MaxElements == 0)
                    prog.setProgress(0);
                else
                    prog.setProgress(jobInfo.ActiveElements * 100 / jobInfo.MaxElements);
            }

            JobHandler.getInstance().getByName(jobInfo.Name).setAdapter(this);
        }

        return view;
    }
}