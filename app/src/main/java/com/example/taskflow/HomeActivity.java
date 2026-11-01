package com.example.taskflow;

import android.content.Intent;
import android.text.TextUtils;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskflow.adapters.ProjectAdapter;
import com.example.taskflow.models.Project;
import com.example.taskflow.models.Task;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Requirement 2: Home screen with
 *   1) a welcome header,
 *   2) workspace info + "New Project" dialog,
 *   3) the "Upcoming" projects list.
 *
 * Requirement 4: task ticks made inside ProjectActivity are reflected here
 * because the (mutated) Project object is returned through the
 * Activity Result API and swapped back into the adapter's list.
 */
public class HomeActivity extends AppCompatActivity implements ProjectAdapter.OnProjectClickListener {

    private TextView tvWelcomeMessage;
    private Button btnNewProject;

    private RecyclerView rvProjects;
    private ProjectAdapter projectAdapter;
    private ArrayList<Project> projectList;

    private int selectedProjectPosition = -1;
    private ActivityResultLauncher<Intent> projectActivityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        registerProjectActivityLauncher();
        initViews();
        loadInitialProjects();
        setupProjectsRecyclerView();
        setupClickListeners();
    }

    private void initViews() {
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);
        btnNewProject = findViewById(R.id.btnNewProject);
        rvProjects = findViewById(R.id.rvProjects);

        tvWelcomeMessage.setText(getString(R.string.welcome_message, "Fahad"));
    }

    // Seed data so the "Upcoming" list is not empty on first launch.
    private void loadInitialProjects() {
        projectList = new ArrayList<>();

        ArrayList<Task> serviceTasks = new ArrayList<>();
        serviceTasks.add(new Task(UUID.randomUUID().toString(),
                "Create brand guideline for website design", true));
        serviceTasks.add(new Task(UUID.randomUUID().toString(),
                "Create low fidelity service page website", false));

        ArrayList<String> serviceAttachments = new ArrayList<>();
        serviceAttachments.add("sports-projectv2.fig");
        serviceAttachments.add("briefing_from_client.pdf");
        serviceAttachments.add("mount_asset.png");

        projectList.add(new Project(
                UUID.randomUUID().toString(),
                "User Interface",
                "Service Page Website",
                "Make a page display about services for the website company with blue and red colors",
                serviceAttachments,
                serviceTasks
        ));

    }

    private void setupProjectsRecyclerView() {
        projectAdapter = new ProjectAdapter(projectList, this);
        rvProjects.setLayoutManager(new LinearLayoutManager(this));
        rvProjects.setAdapter(projectAdapter);
    }

    private void setupClickListeners() {
        btnNewProject.setOnClickListener(v -> showNewProjectDialog());
    }

    // Requirement 2.2: AlertDialog asking for tag, name, description and attachment.
    private void showNewProjectDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_project, null);

        EditText etProjectName = dialogView.findViewById(R.id.etProjectName);
        EditText etProjectTag = dialogView.findViewById(R.id.etProjectTag);
        EditText etAttachment = dialogView.findViewById(R.id.etAttachment);
        EditText etProjectDescription = dialogView.findViewById(R.id.etProjectDescription);
        TextView btnSubmit = dialogView.findViewById(R.id.btnSubmitNewProject);
        TextView ivClose = dialogView.findViewById(R.id.ivCloseNewProjectDialog);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // Let our own rounded card drawable show through instead of the
        // default AlertDialog window background.
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        ivClose.setOnClickListener(v -> dialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            String name = etProjectName.getText().toString().trim();
            String tag = etProjectTag.getText().toString().trim();
            String attachment = etAttachment.getText().toString().trim();
            String description = etProjectDescription.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(tag)) {
                Toast.makeText(this, "Project name and tag are required", Toast.LENGTH_SHORT).show();
                return;
            }

            addNewProjectToUpcomingList(tag, name, description, attachment);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void addNewProjectToUpcomingList(String tag, String name, String description, String attachment) {
        ArrayList<String> attachments = new ArrayList<>();
        if (!TextUtils.isEmpty(attachment)) {
            for (String fileName : attachment.split(",")) {
                String trimmed = fileName.trim();
                if (!trimmed.isEmpty()) {
                    attachments.add(trimmed);
                }
            }
        }

        Project newProject = new Project(
                UUID.randomUUID().toString(),
                tag,
                name,
                description,
                attachments,
                new ArrayList<>()
        );

        projectList.add(0, newProject);
        projectAdapter.notifyItemInserted(0);
        rvProjects.scrollToPosition(0);
    }

    /**
     * Requirement 6: launches ProjectActivity for a result instead of
     * hardcoding data — the same Project object travels there via Intent
     * extra and comes back (possibly mutated) via the result Intent.
     */
    private void registerProjectActivityLauncher() {
        projectActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Project updatedProject = result.getData().getParcelableExtra(ProjectActivity.EXTRA_PROJECT);
                        if (updatedProject != null && selectedProjectPosition != -1) {
                            projectList.set(selectedProjectPosition, updatedProject);
                            projectAdapter.notifyItemChanged(selectedProjectPosition);
                        }
                    }
                }
        );
    }

    @Override
    public void onProjectClick(Project project, int position) {
        selectedProjectPosition = position;
        Intent intent = new Intent(HomeActivity.this, ProjectActivity.class);
        intent.putExtra(ProjectActivity.EXTRA_PROJECT, project);
        projectActivityLauncher.launch(intent);
    }
}
