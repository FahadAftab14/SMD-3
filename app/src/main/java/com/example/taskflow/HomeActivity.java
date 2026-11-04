package com.example.taskflow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taskflow.adapters.ProjectListAdapter;
import com.example.taskflow.data.AttachmentCatalog;
import com.example.taskflow.data.Project;
import com.example.taskflow.data.SampleData;
import com.example.taskflow.ui.ProjectDialogHelper;
import com.example.taskflow.util.SimpleTextWatcher;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

/**
 * Home screen. It owns the FULL project collection (allProjects). The ListView
 * shows shownProjects, which is allProjects filtered by the search text.
 * Projects are always found by their unique id, never by name or row position.
 */
public class HomeActivity extends AppCompatActivity {

    private static final String STATE_PROJECTS = "state_projects";

    private final ArrayList<Project> allProjects = new ArrayList<>();
    private final ArrayList<Project> shownProjects = new ArrayList<>();
    private ProjectListAdapter adapter;
    private String searchQuery = "";

    private TextView tvShowingCount;
    private ListView listProjects;

    private ActivityResultLauncher<Intent> projectLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        registerProjectLauncher();
        loadProjects(savedInstanceState);
        initViews();
        applyFilter();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_PROJECTS, allProjects);
    }

    private void loadProjects(Bundle savedInstanceState) {
        ArrayList<Project> restored = null;
        if (savedInstanceState != null) {
            restored = savedInstanceState.getParcelableArrayList(STATE_PROJECTS);
        }
        if (restored != null) {
            allProjects.addAll(restored);
        } else {
            allProjects.addAll(SampleData.createProjects());
        }
    }

    private void initViews() {
        TextView tvWelcome = findViewById(R.id.tvWelcomeMessage);
        tvWelcome.setText(getString(R.string.welcome_message, "Fahad"));

        tvShowingCount = findViewById(R.id.tvShowingProjects);
        listProjects = findViewById(R.id.listProjects);
        listProjects.setEmptyView(findViewById(R.id.tvProjectsEmpty));

        adapter = new ProjectListAdapter(this, shownProjects);
        listProjects.setAdapter(adapter);

        // Clicking a row opens that project. The row's Project object is handed over
        // through an intent extra; Home later finds it again by its id.
        listProjects.setOnItemClickListener((parent, row, position, id) -> {
            Project clicked = adapter.getItem(position);
            Intent intent = new Intent(HomeActivity.this, ProjectActivity.class);
            intent.putExtra(ProjectActivity.EXTRA_PROJECT, clicked);
            projectLauncher.launch(intent);
        });

        EditText etSearch = findViewById(R.id.etProjectSearch);
        etSearch.addTextChangedListener(new SimpleTextWatcher(text -> {
            searchQuery = text;
            applyFilter();
        }));

        findViewById(R.id.btnNewProject).setOnClickListener(v -> showNewProjectDialog());
    }

    private void showNewProjectDialog() {
        ProjectDialogHelper.show(this, null, (name, tag, description, attachment) -> {
            // A new project: its own unique id, no tasks, no comments, 0/0 and 0%.
            Project project = new Project(UUID.randomUUID().toString(), name, tag, description, 0);
            if (attachment != null) {
                project.addAttachment(AttachmentCatalog.createLink(attachment));
            }
            allProjects.add(project);
            applyFilter();
            int position = shownProjects.indexOf(project);
            if (position >= 0) {
                listProjects.setSelection(position);
            }
        });
    }

    /** Search is case-insensitive and matches any part of the project name or tag. */
    private void applyFilter() {
        String needle = searchQuery.trim().toLowerCase(Locale.ROOT);
        shownProjects.clear();
        for (Project project : allProjects) {
            boolean matches = needle.isEmpty()
                    || project.getName().toLowerCase(Locale.ROOT).contains(needle)
                    || project.getTag().toLowerCase(Locale.ROOT).contains(needle);
            if (matches) {
                shownProjects.add(project);
            }
        }
        adapter.notifyDataSetChanged();

        int total = allProjects.size();
        if (needle.isEmpty()) {
            tvShowingCount.setText("Showing " + total + (total == 1 ? " project" : " projects"));
        } else {
            tvShowingCount.setText("Showing " + shownProjects.size() + " of " + total + " projects");
        }
    }

    /**
     * ProjectActivity sends back its (possibly changed) project. The matching Home
     * project is replaced BY ID, so nothing is duplicated and no other project changes.
     */
    private void registerProjectLauncher() {
        projectLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Intent data = result.getData();
                    if (data == null) {
                        return;
                    }
                    Project updated = data.getParcelableExtra(ProjectActivity.EXTRA_PROJECT);
                    if (updated == null) {
                        return;
                    }
                    for (int i = 0; i < allProjects.size(); i++) {
                        if (allProjects.get(i).getId().equals(updated.getId())) {
                            allProjects.set(i, updated);
                            break;
                        }
                    }
                    applyFilter();
                });
    }
}
