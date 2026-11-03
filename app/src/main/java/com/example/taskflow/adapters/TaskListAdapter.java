package com.example.taskflow.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.taskflow.R;
import com.example.taskflow.data.Task;
import com.example.taskflow.util.DateHelper;

import java.util.List;

/**
 * Feeds the Tasks ListView. Rows are identified by the Task object (and its
 * id), never by name or position. "Today" is read again each time a row is
 * drawn, so OVERDUE is always current.
 */
public class TaskListAdapter extends BaseAdapter {

    public interface Listener {
        void onTaskChecked(Task task, boolean checked);
    }

    private final Context context;
    private final List<Task> tasks;
    private final Listener listener;

    public TaskListAdapter(Context context, List<Task> tasks, Listener listener) {
        this.context = context;
        this.tasks = tasks;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Task getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return tasks.get(position).getId().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder;
        if (row == null) {
            row = LayoutInflater.from(context).inflate(R.layout.row_task, parent, false);
            holder = new Holder(row);
            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        final Task task = tasks.get(position);
        int today = DateHelper.today();

        holder.tvDescription.setText(task.getDescription());
        if (task.isDone()) {
            // Done rows are struck through but keep a dark, readable colour.
            holder.tvDescription.setPaintFlags(holder.tvDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvDescription.setTextColor(context.getColor(R.color.done_text));
        } else {
            holder.tvDescription.setPaintFlags(holder.tvDescription.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvDescription.setTextColor(context.getColor(R.color.text_primary));
        }

        String dueLabel = DateHelper.format(task.getDueDate());
        if (task.getDueDate() == today) {
            dueLabel = dueLabel + " (today)";
        }
        holder.tvMeta.setText(Task.STATUS_LABELS[task.getStatus()] + " \u2022 "
                + Task.PRIORITY_LABELS[task.getPriority()] + " \u2022 " + dueLabel);
        holder.tvOverdue.setVisibility(task.isOverdue(today) ? View.VISIBLE : View.GONE);

        // Clear any old click handler before changing the checked state (rows are reused).
        holder.checkBox.setOnClickListener(null);
        holder.checkBox.setChecked(task.isDone());
        holder.checkBox.setContentDescription("Mark done: " + task.getDescription());
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTaskChecked(task, ((CheckBox) v).isChecked());
            }
        });
        return row;
    }

    private static class Holder {
        final CheckBox checkBox;
        final TextView tvDescription;
        final TextView tvMeta;
        final TextView tvOverdue;

        Holder(View row) {
            checkBox = row.findViewById(R.id.cbTaskDone);
            tvDescription = row.findViewById(R.id.tvTaskDescription);
            tvMeta = row.findViewById(R.id.tvTaskMeta);
            tvOverdue = row.findViewById(R.id.tvTaskOverdue);
        }
    }
}
