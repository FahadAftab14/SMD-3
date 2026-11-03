package com.example.taskflow.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.taskflow.R;
import com.example.taskflow.data.Project;

import java.util.List;

/**
 * Feeds the Home ListView. Every number on a row (completed/total, percentage,
 * progress bar, attachment count) is calculated from the Project's own lists.
 */
public class ProjectListAdapter extends BaseAdapter {

    private final Context context;
    private final List<Project> projects;

    public ProjectListAdapter(Context context, List<Project> projects) {
        this.context = context;
        this.projects = projects;
    }

    @Override
    public int getCount() {
        return projects.size();
    }

    @Override
    public Project getItem(int position) {
        return projects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return projects.get(position).getId().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder;
        if (row == null) {
            row = LayoutInflater.from(context).inflate(R.layout.row_project, parent, false);
            holder = new Holder(row);
            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        Project project = projects.get(position);
        int total = project.getTotalTaskCount();
        int done = project.getDoneTaskCount();
        int percent = project.getProgressPercent();
        int attachments = project.getAttachments().size();
        int comments = project.getCommentCount();

        holder.tvTag.setText(project.getTag());
        holder.tvName.setText(project.getName());
        holder.tvDescription.setText(project.getDescription());
        holder.tvTasks.setText(done + "/" + total + " tasks");
        holder.tvPercent.setText(percent + "%");
        holder.progress.setMax(100);
        holder.progress.setProgress(percent);
        holder.tvAttachments.setText(attachments + (attachments == 1 ? " Attachment" : " Attachments"));
        holder.tvComments.setText(comments + (comments == 1 ? " Comment" : " Comments"));
        return row;
    }

    private static class Holder {
        final TextView tvTag;
        final TextView tvName;
        final TextView tvDescription;
        final TextView tvTasks;
        final TextView tvPercent;
        final ProgressBar progress;
        final TextView tvAttachments;
        final TextView tvComments;

        Holder(View row) {
            tvTag = row.findViewById(R.id.tvRowProjectTag);
            tvName = row.findViewById(R.id.tvRowProjectName);
            tvDescription = row.findViewById(R.id.tvRowProjectDescription);
            tvTasks = row.findViewById(R.id.tvRowProjectTasks);
            tvPercent = row.findViewById(R.id.tvRowProjectPercent);
            progress = row.findViewById(R.id.pbRowProjectProgress);
            tvAttachments = row.findViewById(R.id.tvRowProjectAttachments);
            tvComments = row.findViewById(R.id.tvRowProjectComments);
        }
    }
}
