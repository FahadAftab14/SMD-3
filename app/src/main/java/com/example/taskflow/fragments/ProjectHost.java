package com.example.taskflow.fragments;

import com.example.taskflow.data.Project;

/**
 * What ProjectActivity offers to its fragments. Fragments never keep their
 * own copy of the project: they always read it from getProject() and tell the
 * host when something changed.
 */
public interface ProjectHost {

    int TAB_OVERVIEW = 0;
    int TAB_TASKS = 1;
    int TAB_FILES = 2;
    int TAB_DISCUSSION = 3;

    /** The one working project object for this ProjectActivity visit. */
    Project getProject();

    /** Call after any change to tasks, attachments or project info. Refreshes header, tab counts and all fragments. */
    void onProjectDataChanged();

    /** Selects a tab (the TabLayout indicator and the visible fragment stay in sync). */
    void openTab(int tabIndex);

    /** Opens Tasks with the given status filter (Task.STATUS_*) and all other filters cleared. */
    void openTasksWithStatus(int status);

    /** Opens Tasks with All statuses and the Overdue date filter. */
    void openOverdueTasks();
}
