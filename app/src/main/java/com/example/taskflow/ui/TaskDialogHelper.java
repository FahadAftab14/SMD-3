package com.example.taskflow.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.taskflow.R;
import com.example.taskflow.data.Project;
import com.example.taskflow.data.Task;
import com.example.taskflow.fragments.ProjectHost;
import com.example.taskflow.util.DateHelper;
import com.example.taskflow.util.SimpleTextWatcher;

import java.util.UUID;

/**
 * The single task dialog used by Overview and Tasks, for adding and editing.
 * Description: multiline, 1-500 characters with a live 0/500 counter.
 * Priority: High / Normal / Low (default Normal). Status: To do / In progress /
 * Done (default To do). Due date: optional, picked with a DatePickerDialog and
 * clearable. Past dates are allowed.
 */
public final class TaskDialogHelper {

    public static final int MAX_DESCRIPTION = 500;

    private TaskDialogHelper() {
    }

    /** Opens the dialog to add a task to the host's current project. */
    public static void showAddTask(final Context context, final ProjectHost host) {
        show(context, host.getProject().getName(), null, new Listener() {
            @Override
            public void onSave(String description, int priority, int status, int dueDate) {
                Task task = new Task(UUID.randomUUID().toString(), description, status, priority,
                        dueDate, System.currentTimeMillis());
                host.getProject().addTask(task);
                host.onProjectDataChanged();
            }

            @Override
            public void onDelete() {
                // not available when adding
            }
        });
    }

    /** Opens the dialog prefilled with an existing task. The task is found by its id. */
    public static void showEditTask(final Context context, final ProjectHost host, final String taskId) {
        Project project = host.getProject();
        Task existing = project.findTask(taskId);
        if (existing == null) {
            return;
        }
        show(context, project.getName(), existing, new Listener() {
            @Override
            public void onSave(String description, int priority, int status, int dueDate) {
                Task task = host.getProject().findTask(taskId);
                if (task != null) {
                    task.setDescription(description);
                    task.setPriority(priority);
                    task.setStatus(status);
                    task.setDueDate(dueDate);
                }
                host.onProjectDataChanged();
            }

            @Override
            public void onDelete() {
                host.getProject().removeTask(taskId);
                host.onProjectDataChanged();
            }
        });
    }

    // ------------------------------------------------------------------

    private interface Listener {
        void onSave(String description, int priority, int status, int dueDate);

        void onDelete();
    }

    private static void show(final Context context, String projectName, Task existing, final Listener listener) {
        final boolean isEdit = existing != null;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_task_form, null);

        TextView tvTitle = view.findViewById(R.id.tvTaskDialogTitle);
        TextView tvSubtitle = view.findViewById(R.id.tvTaskDialogSubtitle);
        final TextView tvCounter = view.findViewById(R.id.tvDescriptionCounter);
        final EditText etDescription = view.findViewById(R.id.etTaskDescription);
        final Spinner spStatus = view.findViewById(R.id.spTaskStatus);
        final Spinner spPriority = view.findViewById(R.id.spTaskPriority);
        final TextView tvDueDate = view.findViewById(R.id.tvDueDateValue);
        final TextView tvClearDate = view.findViewById(R.id.tvClearDueDate);
        TextView btnDelete = view.findViewById(R.id.btnTaskDelete);
        TextView btnCancel = view.findViewById(R.id.btnTaskCancel);
        TextView btnSave = view.findViewById(R.id.btnTaskSave);

        tvTitle.setText(isEdit ? "Edit Task" : "Add New Task");
        tvSubtitle.setText(projectName + " project");
        btnSave.setText(isEdit ? "Save" : "+ Add Task");
        btnDelete.setVisibility(isEdit ? View.VISIBLE : View.GONE);

        // Description with a live counter and a hard 500 character limit.
        etDescription.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_DESCRIPTION)});
        etDescription.addTextChangedListener(new SimpleTextWatcher(new SimpleTextWatcher.Callback() {
            @Override
            public void onTextChanged(String text) {
                tvCounter.setText(text.length() + "/" + MAX_DESCRIPTION);
            }
        }));

        // Status and Priority selectors.
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, Task.STATUS_LABELS);
        statusAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spStatus.setAdapter(statusAdapter);
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, Task.PRIORITY_LABELS);
        priorityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spPriority.setAdapter(priorityAdapter);

        // Defaults: To do, Normal, no due date.
        final int[] dueDate = {DateHelper.NO_DATE};
        spStatus.setSelection(Task.STATUS_TODO);
        spPriority.setSelection(Task.PRIORITY_NORMAL);
        if (isEdit) {
            etDescription.setText(existing.getDescription());
            spStatus.setSelection(existing.getStatus());
            spPriority.setSelection(existing.getPriority());
            dueDate[0] = existing.getDueDate();
        }
        tvCounter.setText(etDescription.getText().length() + "/" + MAX_DESCRIPTION);

        // Optional due date.
        final Runnable showDueDate = new Runnable() {
            @Override
            public void run() {
                boolean hasDate = dueDate[0] != DateHelper.NO_DATE;
                tvDueDate.setText(hasDate ? DateHelper.format(dueDate[0]) : "No due date (tap to choose)");
                tvClearDate.setVisibility(hasDate ? View.VISIBLE : View.GONE);
            }
        };
        showDueDate.run();

        tvDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int base = dueDate[0] != DateHelper.NO_DATE ? dueDate[0] : DateHelper.today();
                new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker picker, int year, int month, int dayOfMonth) {
                        dueDate[0] = DateHelper.toInt(year, month, dayOfMonth);
                        showDueDate.run();
                    }
                }, DateHelper.yearOf(base), DateHelper.month0Of(base), DateHelper.dayOf(base)).show();
            }
        });
        tvClearDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dueDate[0] = DateHelper.NO_DATE;
                showDueDate.run();
            }
        });

        final AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete task?")
                        .setMessage("This task will be removed from the project.")
                        .setPositiveButton("Delete", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface d, int which) {
                                dialog.dismiss();
                                listener.onDelete();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etDescription.getText().toString().trim();
                if (description.length() < 1 || description.length() > MAX_DESCRIPTION) {
                    etDescription.setError("Description must be 1-" + MAX_DESCRIPTION + " characters");
                    return;
                }
                int status = spStatus.getSelectedItemPosition();
                int priority = spPriority.getSelectedItemPosition();
                dialog.dismiss();
                listener.onSave(description, priority, status, dueDate[0]);
            }
        });

        dialog.show();
    }
}
