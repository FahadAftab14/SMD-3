package com.example.taskflow.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.taskflow.R;
import com.example.taskflow.data.AttachmentCatalog;
import com.example.taskflow.data.AttachmentLink;
import com.example.taskflow.data.CatalogItem;
import com.example.taskflow.data.Project;
import com.example.taskflow.data.Task;
import com.example.taskflow.ui.ProjectDialogHelper;
import com.example.taskflow.ui.TaskDialogHelper;
import com.example.taskflow.util.DateHelper;

/**
 * Project summary: description (with Edit Project), progress, the
 * To do / In progress / Done / Overdue breakdown, quick links and Add Task.
 * Every number is calculated from the project's records each time it is shown.
 */
public class OverviewFragment extends ProjectTabFragment {

    private TextView tvDescription;
    private TextView tvPercent;
    private TextView tvCompleted;
    private ProgressBar progressBar;
    private TextView tvTodoCount;
    private TextView tvInProgressCount;
    private TextView tvDoneCount;
    private TextView tvOverdueCount;
    private TextView tvAttachmentsSummary;
    private TextView tvDiscussionSummary;

    public static OverviewFragment newInstance(String projectId) {
        return withProjectId(new OverviewFragment(), projectId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvDescription = view.findViewById(R.id.tvOverviewDescription);
        tvPercent = view.findViewById(R.id.tvOverviewPercent);
        tvCompleted = view.findViewById(R.id.tvOverviewCompleted);
        progressBar = view.findViewById(R.id.pbOverviewProgress);
        tvTodoCount = view.findViewById(R.id.tvTodoCount);
        tvInProgressCount = view.findViewById(R.id.tvInProgressCount);
        tvDoneCount = view.findViewById(R.id.tvDoneCount);
        tvOverdueCount = view.findViewById(R.id.tvOverdueCount);
        tvAttachmentsSummary = view.findViewById(R.id.tvAttachmentsSummary);
        tvDiscussionSummary = view.findViewById(R.id.tvDiscussionSummary);

        view.findViewById(R.id.tvEditProject).setOnClickListener(v -> showEditProjectDialog());

        // Task Breakdown cards open the Tasks tab with that filter applied.
        view.findViewById(R.id.cardTodo).setOnClickListener(v -> host.openTasksWithStatus(Task.STATUS_TODO));
        view.findViewById(R.id.cardInProgress).setOnClickListener(v -> host.openTasksWithStatus(Task.STATUS_IN_PROGRESS));
        view.findViewById(R.id.cardDone).setOnClickListener(v -> host.openTasksWithStatus(Task.STATUS_DONE));
        view.findViewById(R.id.cardOverdue).setOnClickListener(v -> host.openOverdueTasks());

        view.findViewById(R.id.linkAttachments).setOnClickListener(v -> host.openTab(ProjectHost.TAB_FILES));
        view.findViewById(R.id.linkDiscussion).setOnClickListener(v -> host.openTab(ProjectHost.TAB_DISCUSSION));

        // Same task dialog as the Tasks tab; after saving we stay on Overview.
        view.findViewById(R.id.btnOverviewAddTask).setOnClickListener(
                v -> TaskDialogHelper.showAddTask(requireContext(), host));

        onRefresh();
    }

    private void showEditProjectDialog() {
        ProjectDialogHelper.show(requireContext(), currentProject(),
                (name, tag, description, attachment) -> {
                    currentProject().updateInfo(name, tag, description);
                    host.onProjectDataChanged();
                });
    }

    @Override
    protected void onRefresh() {
        Project project = currentProject();
        int today = DateHelper.today();
        int total = project.getTotalTaskCount();
        int done = project.getDoneTaskCount();
        int percent = project.getProgressPercent();

        tvDescription.setText(project.getDescription());
        tvPercent.setText(percent + "%");
        tvCompleted.setText(done + " of " + total + " tasks completed");
        progressBar.setMax(100);
        progressBar.setProgress(percent);

        // To do + In progress + Done always equals the total. Overdue is a separate
        // subset of the unfinished tasks and is never added to those three.
        tvTodoCount.setText(String.valueOf(project.getTaskCountByStatus(Task.STATUS_TODO)));
        tvInProgressCount.setText(String.valueOf(project.getTaskCountByStatus(Task.STATUS_IN_PROGRESS)));
        tvDoneCount.setText(String.valueOf(done));
        tvOverdueCount.setText(String.valueOf(project.getOverdueTaskCount(today)));

        tvAttachmentsSummary.setText(buildAttachmentsSummary(project));
        int comments = project.getCommentCount();
        tvDiscussionSummary.setText(comments + (comments == 1 ? " comment" : " comments"));
    }

    private String buildAttachmentsSummary(Project project) {
        int count = project.getAttachments().size();
        if (count == 0) {
            return "No files";
        }
        StringBuilder names = new StringBuilder();
        int shownNames = 0;
        for (AttachmentLink link : project.getAttachments()) {
            CatalogItem item = AttachmentCatalog.findById(link.getCatalogId());
            if (item == null) {
                continue;
            }
            if (shownNames == 2) {
                names.append(", ...");
                break;
            }
            if (shownNames > 0) {
                names.append(", ");
            }
            names.append(item.getDisplayName());
            shownNames++;
        }
        return count + (count == 1 ? " file" : " files") + " \u2022 " + names;
    }
}
