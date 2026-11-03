package com.example.taskflow.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.taskflow.R;

/** Discussion is "Coming Soon": a plain TextView, nothing else. */
public class DiscussionFragment extends ProjectTabFragment {

    public static DiscussionFragment newInstance(String projectId) {
        return withProjectId(new DiscussionFragment(), projectId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discussion, container, false);
    }

    @Override
    protected void onRefresh() {
        // Nothing to refresh.
    }
}
