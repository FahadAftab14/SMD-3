package com.example.taskflow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskflow.R;
import com.example.taskflow.models.Project;

import java.util.List;


 // Binds the list of Projects to the "Upcoming" RecyclerView on HomeActivity.
public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    public interface OnProjectClickListener {
        void onProjectClick(Project project, int position);
    }

    private final List<Project> projectList;
    private final OnProjectClickListener listener;

    public ProjectAdapter(List<Project> projectList, OnProjectClickListener listener) {
        this.projectList = projectList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projectList.get(position);

        holder.tvTag.setText(project.getTag());
        holder.tvProjectName.setText(project.getName());
        holder.tvProjectDescription.setText(project.getDescription());
        holder.tvTaskProgress.setText(
                project.getCompletedTaskCount() + "/" + project.getTotalTaskCount());

        int attachmentCount = project.getAttachments().size();
        holder.tvAttachmentInfo.setText(attachmentCount + (attachmentCount == 1 ? " Attachment" : " Attachments"));

        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onProjectClick(project, adapterPosition);
            }
        });

        holder.btnInvite.setOnClickListener(v ->
                Toast.makeText(v.getContext(), "Invite is out of scope for this assignment", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag, tvProjectName, tvProjectDescription, tvTaskProgress, tvAttachmentInfo;
        TextView btnInvite;

        ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTag = itemView.findViewById(R.id.tvTag);
            tvProjectName = itemView.findViewById(R.id.tvProjectName);
            tvProjectDescription = itemView.findViewById(R.id.tvProjectDescription);
            tvTaskProgress = itemView.findViewById(R.id.tvTaskProgress);
            tvAttachmentInfo = itemView.findViewById(R.id.tvAttachmentInfo);
            btnInvite = itemView.findViewById(R.id.btnInvite);
        }
    }
}
