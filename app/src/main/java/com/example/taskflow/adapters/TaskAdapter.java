package com.example.taskflow.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskflow.R;
import com.example.taskflow.models.Task;

import java.util.List;

/**
 * Binds a Project's task/checklist items inside ProjectActivity.
 * Ticking a CheckBox updates the underlying Task object directly and
 * notifies the host activity so it can keep the Home screen count in sync
 * once the user navigates back.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskCheckListener {
        void onTaskCheckChanged(int position, boolean isChecked);
    }

    private final List<Task> taskList;
    private final OnTaskCheckListener listener;

    public TaskAdapter(List<Task> taskList, OnTaskCheckListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        // Clear the listener before setChecked to avoid firing a stale
        // callback caused by RecyclerView view recycling.
        holder.checkBoxTask.setOnCheckedChangeListener(null);
        holder.checkBoxTask.setText(task.getDescription());
        holder.checkBoxTask.setChecked(task.isCompleted());

        holder.checkBoxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                taskList.get(adapterPosition).setCompleted(isChecked);
                if (listener != null) {
                    listener.onTaskCheckChanged(adapterPosition, isChecked);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBoxTask;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxTask = itemView.findViewById(R.id.checkBoxTask);
        }
    }
}
