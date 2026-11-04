package com.example.taskflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.taskflow.data.Project;
import com.example.taskflow.data.TaskQuery;
import com.example.taskflow.fragments.DiscussionFragment;
import com.example.taskflow.fragments.FilesFragment;
import com.example.taskflow.fragments.OverviewFragment;
import com.example.taskflow.fragments.ProjectHost;
import com.example.taskflow.fragments.ProjectTabFragment;
import com.example.taskflow.fragments.TasksFragment;
import com.google.android.material.tabs.TabLayout;

/**
 * Hosts the header, the TabLayout and the four fragments. It holds the ONE working
 * Project object for this visit; fragments read it through ProjectHost. The
 * fragments are added once and then shown/hidden, so each tab keeps its own
 * search, filters and sort while you move between tabs.
 */
public class ProjectActivity extends AppCompatActivity implements ProjectHost {

    public static final String EXTRA_PROJECT = "extra_project";

    private static final String STATE_PROJECT = "state_project";
    private static final String STATE_TAB = "state_tab";
    private static final String[] FRAGMENT_TAGS = {"tab_overview", "tab_tasks", "tab_files", "tab_discussion"};

    private Project project;
    private int currentTab = TAB_OVERVIEW;

    private TextView tvProjectName;
    private TextView tvProjectTag;
    private TabLayout tabLayout;
    private final ProjectTabFragment[] fragments = new ProjectTabFragment[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        if (savedInstanceState != null) {
            project = savedInstanceState.getParcelable(STATE_PROJECT);
            currentTab = savedInstanceState.getInt(STATE_TAB, TAB_OVERVIEW);
        }
        if (project == null) {
            project = getIntent().getParcelableExtra(EXTRA_PROJECT);
        }
        if (project == null) {
            finish();
            return;
        }

        tvProjectName = findViewById(R.id.tvProjectName);
        tvProjectTag = findViewById(R.id.tvProjectTag);
        tabLayout = findViewById(R.id.tabLayout);

        bindHeader();
        setupFragments(savedInstanceState == null);
        setupTabs();
        setupBackNavigation();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_PROJECT, project);
        outState.putInt(STATE_TAB, currentTab);
    }

    // ---------------- header, fragments, tabs ----------------

    private void bindHeader() {
        tvProjectName.setText(project.getName());
        tvProjectTag.setText(project.getTag());
    }

    private void setupFragments(boolean firstTime) {
        FragmentManager fm = getSupportFragmentManager();
        if (firstTime) {
            String id = project.getId();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, OverviewFragment.newInstance(id), FRAGMENT_TAGS[TAB_OVERVIEW])
                    .add(R.id.fragmentContainer, TasksFragment.newInstance(id), FRAGMENT_TAGS[TAB_TASKS])
                    .add(R.id.fragmentContainer, FilesFragment.newInstance(id), FRAGMENT_TAGS[TAB_FILES])
                    .add(R.id.fragmentContainer, DiscussionFragment.newInstance(id), FRAGMENT_TAGS[TAB_DISCUSSION])
                    .commitNow();
        }
        for (int i = 0; i < fragments.length; i++) {
            fragments[i] = (ProjectTabFragment) fm.findFragmentByTag(FRAGMENT_TAGS[i]);
        }
    }

    private void setupTabs() {
        for (int i = 0; i < fragments.length; i++) {
            tabLayout.addTab(tabLayout.newTab());
        }
        updateTabTitles();

        TabLayout.Tab tab = tabLayout.getTabAt(currentTab);
        if (tab != null) {
            tab.select();
        }
        showFragment(currentTab);

        // Added last, so the setup above does not trigger it.
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab selected) {
                showFragment(selected.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab unselected) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab reselected) {
            }
        });
    }

    /** Totals on the tabs come straight from the project's records. */
    private void updateTabTitles() {
        setTabTitle(TAB_OVERVIEW, "Overview");
        setTabTitle(TAB_TASKS, "Tasks (" + project.getTotalTaskCount() + ")");
        setTabTitle(TAB_FILES, "Files (" + project.getAttachments().size() + ")");
        setTabTitle(TAB_DISCUSSION, "Discussion (" + project.getCommentCount() + ")");
    }

    private void setTabTitle(int index, String title) {
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        if (tab != null) {
            tab.setText(title);
        }
    }

    /** Shows exactly one fragment and hides the rest. */
    private void showFragment(int index) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == index) {
                tx.show(fragments[i]);
            } else {
                tx.hide(fragments[i]);
            }
        }
        tx.commitNow();
        currentTab = index;
        fragments[index].refreshContent();
    }

    // ---------------- ProjectHost ----------------

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void onProjectDataChanged() {
        bindHeader();
        updateTabTitles();
        for (ProjectTabFragment fragment : fragments) {
            if (fragment != null) {
                fragment.refreshContent();
            }
        }
    }

    @Override
    public void openTab(int tabIndex) {
        TabLayout.Tab tab = tabLayout.getTabAt(tabIndex);
        if (tab != null) {
            tab.select();
        }
        if (currentTab != tabIndex) {
            showFragment(tabIndex);
        }
    }

    @Override
    public void openTasksWithStatus(int status) {
        ((TasksFragment) fragments[TAB_TASKS]).applyExternalFilter(status, TaskQuery.DATE_ALL);
        openTab(TAB_TASKS);
    }

    @Override
    public void openOverdueTasks() {
        ((TasksFragment) fragments[TAB_TASKS]).applyExternalFilter(TaskQuery.ANY, TaskQuery.DATE_OVERDUE);
        openTab(TAB_TASKS);
    }

    // ---------------- going back ----------------

    private void setupBackNavigation() {
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWithResult();
            }
        });
        // The system Back button/gesture takes the same path as the toolbar arrow.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishWithResult();
            }
        });
    }

    /** Sends the updated project back; Home replaces its copy by id. */
    private void finishWithResult() {
        Intent result = new Intent();
        result.putExtra(EXTRA_PROJECT, project);
        setResult(RESULT_OK, result);
        finish();
    }
}
