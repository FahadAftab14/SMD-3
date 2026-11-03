package com.example.taskflow.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.taskflow.data.Project;

/**
 * Base class for the four tab fragments. Each one receives only the project ID
 * in its arguments and reads the live project object from the host activity,
 * so there is never a second copy of the data inside a fragment.
 */
public abstract class ProjectTabFragment extends Fragment {

    public static final String ARG_PROJECT_ID = "arg_project_id";

    protected ProjectHost host;

    protected static <T extends ProjectTabFragment> T withProjectId(T fragment, String projectId) {
        Bundle args = new Bundle();
        args.putString(ARG_PROJECT_ID, projectId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof ProjectHost)) {
            throw new IllegalStateException("The host activity must implement ProjectHost");
        }
        host = (ProjectHost) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        host = null;
    }

    /** The current project, read from the host. Checks the ID passed in the arguments. */
    protected Project currentProject() {
        Project project = host.getProject();
        Bundle args = getArguments();
        String expectedId = args == null ? null : args.getString(ARG_PROJECT_ID);
        if (expectedId != null && !expectedId.equals(project.getId())) {
            throw new IllegalStateException("This fragment belongs to a different project");
        }
        return project;
    }

    /** Redraws this tab from the current project. Safe to call at any time. */
    public final void refreshContent() {
        if (host != null && getView() != null) {
            onRefresh();
        }
    }

    protected abstract void onRefresh();

    /** A hidden fragment re-reads the records as soon as it becomes visible again. */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refreshContent();
        }
    }
}
