package com.example.taskflow;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskflow.adapters.TaskAdapter;
import com.example.taskflow.models.Project;
import com.example.taskflow.models.Task;

import java.util.UUID;

/**
 * Requirement 3, 4 & 5: Shows a single Project's read-only details plus an
 * editable checklist. Adding a task or ticking a checkbox mutates the SAME
 * Project object that was received via Intent, which is then sent back to
 * HomeActivity through setResult() so both screens stay in sync.
 */
public class ProjectActivity extends AppCompatActivity implements TaskAdapter.OnTaskCheckListener {

    public static final String EXTRA_PROJECT = "extra_project";
    private static final int MAX_TASK_LENGTH = 500;

    private Project project;

    private TextView ivBack;
    private TextView btnInviteProject;
    private TextView tvTag, tvProjectName, tvProjectDescription, tvNoAttachments;
    private LinearLayout llAttachments;
    private TextView btnAddTask;
    private RecyclerView rvTasks;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        project = getIntent().getParcelableExtra(EXTRA_PROJECT);
        if (project == null) {
            Toast.makeText(this, "Unable to load project", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        bindProjectDetails();
        setupTaskRecyclerView();
        setupClickListeners();
        setupBackNavigation();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        btnInviteProject = findViewById(R.id.btnInviteProject);
        tvTag = findViewById(R.id.tvTag);
        tvProjectName = findViewById(R.id.tvProjectName);
        tvProjectDescription = findViewById(R.id.tvProjectDescription);
        llAttachments = findViewById(R.id.llAttachments);
        tvNoAttachments = findViewById(R.id.tvNoAttachments);
        btnAddTask = findViewById(R.id.btnAddTask);
        rvTasks = findViewById(R.id.rvTasks);
    }

    private void bindProjectDetails() {
        tvTag.setText(project.getTag());
        tvProjectName.setText(project.getName());
        tvProjectDescription.setText(project.getDescription());
        renderAttachmentChips();
    }

    // Cycled per chip so attachments look like the colorful cards in the Figma design.
    private static final int[] ATTACHMENT_CHIP_COLORS = {
            R.color.attachment_chip_pink,
            R.color.attachment_chip_lavender,
            R.color.attachment_chip_cream,
            R.color.attachment_chip_mint
    };

    // Requirement 3 (viewable attachments): builds one chip per attachment, matching the Figma design.
    private void renderAttachmentChips() {
        llAttachments.removeAllViews();

        if (project.getAttachments().isEmpty()) {
            llAttachments.setVisibility(View.GONE);
            tvNoAttachments.setVisibility(View.VISIBLE);
            return;
        }

        llAttachments.setVisibility(View.VISIBLE);
        tvNoAttachments.setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < project.getAttachments().size(); i++) {
            String attachmentName = project.getAttachments().get(i);
            View chip = inflater.inflate(R.layout.item_attachment_chip, llAttachments, false);

            int colorRes = ATTACHMENT_CHIP_COLORS[i % ATTACHMENT_CHIP_COLORS.length];
            chip.setBackgroundTintList(getColorStateList(colorRes));

            TextView tvChipName = chip.findViewById(R.id.tvAttachmentChipName);
            tvChipName.setText(attachmentName);
            llAttachments.addView(chip);
        }
    }

    private void setupTaskRecyclerView() {
        taskAdapter = new TaskAdapter(project.getTasks(), this);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(taskAdapter);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> navigateBackToHome());
        btnAddTask.setOnClickListener(v -> showAddTaskDialog());
        btnInviteProject.setOnClickListener(v ->
                Toast.makeText(this, "Invite is out of scope for this assignment", Toast.LENGTH_SHORT).show());
    }

    // Requirement 5: back arrow returns to HomeActivity with updated data.
    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateBackToHome();
            }
        });
    }

    // Requirement 3: add a task via an AlertDialog with a 500-character EditText.
    private void showAddTaskDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        EditText etTaskDescription = dialogView.findViewById(R.id.etTaskDescription);
        etTaskDescription.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_TASK_LENGTH)});
        TextView btnSubmit = dialogView.findViewById(R.id.btnSubmitTask);
        TextView ivClose = dialogView.findViewById(R.id.ivCloseAddTaskDialog);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        ivClose.setOnClickListener(v -> dialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            String description = etTaskDescription.getText().toString().trim();
            if (TextUtils.isEmpty(description)) {
                Toast.makeText(this, "Task description cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            addTaskToProject(description);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void addTaskToProject(String description) {
        Task newTask = new Task(UUID.randomUUID().toString(), description, false);
        project.addTask(newTask);
        taskAdapter.notifyItemInserted(project.getTasks().size() - 1);
    }

    // Requirement 4: propagate the checked/unchecked state upward (Project is mutated in place).
    @Override
    public void onTaskCheckChanged(int position, boolean isChecked) {
        // Task object itself is already updated by TaskAdapter; nothing extra
        // is required here, but the hook is kept for any future side effects.
    }

    private void navigateBackToHome() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_PROJECT, project);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}