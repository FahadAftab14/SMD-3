package com.example.taskflow.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.taskflow.R;
import com.example.taskflow.adapters.TaskListAdapter;
import com.example.taskflow.data.Project;
import com.example.taskflow.data.Task;
import com.example.taskflow.data.TaskQuery;
import com.example.taskflow.ui.TaskDialogHelper;
import com.example.taskflow.util.DateHelper;
import com.example.taskflow.util.SimpleTextWatcher;

import java.util.ArrayList;

/**
 * The task list: search + Status / Priority / Date filters + Sort, all applied
 * together through one TaskQuery. The query lives in this fragment object, so it
 * survives tab switches during the same ProjectActivity visit.
 */
public class TasksFragment extends ProjectTabFragment implements TaskListAdapter.Listener {

    private interface OnChoice {
        void onChoice(int index);
    }

    private final TaskQuery query = new TaskQuery();
    private final ArrayList<Task> shownTasks = new ArrayList<>();
    private TaskListAdapter adapter;

    private EditText etSearch;
    private TextView tvStatus;
    private TextView tvPriority;
    private TextView tvDate;
    private TextView tvSort;
    private TextView tvShowing;
    private ListView listTasks;
    private View emptyNoTasks;
    private View emptyNoMatch;

    public static TasksFragment newInstance(String projectId) {
        return withProjectId(new TasksFragment(), projectId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            query.restoreFrom(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        query.saveTo(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearch = view.findViewById(R.id.etTaskSearch);
        tvStatus = view.findViewById(R.id.tvFilterStatus);
        tvPriority = view.findViewById(R.id.tvFilterPriority);
        tvDate = view.findViewById(R.id.tvFilterDate);
        tvSort = view.findViewById(R.id.tvFilterSort);
        tvShowing = view.findViewById(R.id.tvShowingTasks);
        listTasks = view.findViewById(R.id.listTasks);
        emptyNoTasks = view.findViewById(R.id.emptyNoTasks);
        emptyNoMatch = view.findViewById(R.id.emptyNoMatch);

        adapter = new TaskListAdapter(requireContext(), shownTasks, this);
        listTasks.setAdapter(adapter);
        listTasks.setOnItemClickListener((parent, row, position, id) -> {
            Task task = adapter.getItem(position);
            TaskDialogHelper.showEditTask(requireContext(), host, task.getId());
        });

        // Put the saved search text in first, then start listening for changes.
        etSearch.setText(query.search);
        etSearch.addTextChangedListener(new SimpleTextWatcher(text -> {
            query.search = text;
            refreshContent();
        }));

        tvStatus.setOnClickListener(v -> showMenu(v, TaskQuery.STATUS_OPTIONS, query.status + 1, index -> {
            query.status = index - 1;
            refreshContent();
        }));
        tvPriority.setOnClickListener(v -> showMenu(v, TaskQuery.PRIORITY_OPTIONS, query.priority + 1, index -> {
            query.priority = index - 1;
            refreshContent();
        }));
        tvDate.setOnClickListener(v -> showMenu(v, TaskQuery.DATE_OPTIONS, query.date, index -> {
            query.date = index;
            refreshContent();
        }));
        tvSort.setOnClickListener(v -> showMenu(v, TaskQuery.SORT_OPTIONS, query.sort, index -> {
            query.sort = index;
            refreshContent();
        }));

        view.findViewById(R.id.tvResetFilters).setOnClickListener(v -> resetFilters());
        view.findViewById(R.id.btnEmptyResetFilters).setOnClickListener(v -> resetFilters());
        view.findViewById(R.id.btnTasksAddTask).setOnClickListener(
                v -> TaskDialogHelper.showAddTask(requireContext(), host));
        view.findViewById(R.id.btnEmptyAddTask).setOnClickListener(
                v -> TaskDialogHelper.showAddTask(requireContext(), host));

        onRefresh();
    }

    /** Called by the Overview cards: show only this status (or the Overdue date filter). */
    public void applyExternalFilter(int status, int dateFilter) {
        query.search = "";
        query.status = status;
        query.priority = TaskQuery.ANY;
        query.date = dateFilter;
        if (etSearch != null) {
            etSearch.setText("");
        }
        refreshContent();
    }

    private void resetFilters() {
        query.reset();
        if (etSearch != null) {
            etSearch.setText("");
        }
        refreshContent();
    }

    private void showMenu(View anchor, String[] options, int currentIndex, final OnChoice callback) {
        PopupMenu popup = new PopupMenu(requireContext(), anchor);
        for (int i = 0; i < options.length; i++) {
            String label = i == currentIndex ? options[i] + "  \u2713" : options[i];
            popup.getMenu().add(Menu.NONE, i, i, label);
        }
        popup.setOnMenuItemClickListener(item -> {
            callback.onChoice(item.getItemId());
            return true;
        });
        popup.show();
    }

    @Override
    protected void onRefresh() {
        Project project = currentProject();
        int total = project.getTotalTaskCount();

        shownTasks.clear();
        shownTasks.addAll(query.apply(project.getTasks(), DateHelper.today()));
        adapter.notifyDataSetChanged();

        tvShowing.setText("Showing " + shownTasks.size() + " of " + total + " tasks");
        tvStatus.setText("Status: " + TaskQuery.STATUS_SHORT[query.status + 1] + " \u25BE");
        tvPriority.setText("Priority: " + TaskQuery.PRIORITY_SHORT[query.priority + 1] + " \u25BE");
        tvDate.setText("Date: " + TaskQuery.DATE_SHORT[query.date] + " \u25BE");
        tvSort.setText("Sort: " + TaskQuery.SORT_SHORT[query.sort] + " \u25BE");

        // Two different empty states: no tasks at all, versus tasks that don't match.
        if (total == 0) {
            listTasks.setVisibility(View.GONE);
            emptyNoTasks.setVisibility(View.VISIBLE);
            emptyNoMatch.setVisibility(View.GONE);
        } else if (shownTasks.isEmpty()) {
            listTasks.setVisibility(View.GONE);
            emptyNoTasks.setVisibility(View.GONE);
            emptyNoMatch.setVisibility(View.VISIBLE);
        } else {
            listTasks.setVisibility(View.VISIBLE);
            emptyNoTasks.setVisibility(View.GONE);
            emptyNoMatch.setVisibility(View.GONE);
        }
    }

    /** Ticking the CheckBox sets the status to Done; unticking returns it to To do. */
    @Override
    public void onTaskChecked(Task task, boolean checked) {
        Task live = currentProject().findTask(task.getId());
        if (live != null) {
            live.setStatus(checked ? Task.STATUS_DONE : Task.STATUS_TODO);
        }
        host.onProjectDataChanged();
    }
}
