package com.example.taskflow.data;

import com.example.taskflow.util.DateHelper;

import java.util.ArrayList;
import java.util.UUID;

/**
 * The five starting projects shown on Home. Every task due date is calculated
 * from the device's current date, so "Due today" and "Overdue" are always
 * true when the app starts. The main project is built to match the design:
 * 12 tasks (5 To do, 3 In progress, 4 Done, 2 Overdue), 3 files, 3 comments.
 */
public final class SampleData {

    private static final int NONE = Integer.MIN_VALUE;

    private final int today = DateHelper.today();
    private long clock = System.currentTimeMillis() - 10000000L;

    private SampleData() {
    }

    public static ArrayList<Project> createProjects() {
        return new SampleData().build();
    }

    private ArrayList<Project> build() {
        ArrayList<Project> projects = new ArrayList<>();
        projects.add(buildServicePageWebsite());
        projects.add(buildLoginApi());
        projects.add(buildMobileDashboard());
        projects.add(buildReleaseChecklist());
        projects.add(buildDesignAssets());
        return projects;
    }

    private Project buildServicePageWebsite() {
        Project p = new Project(newId(), "Service Page Website", "User Interface",
                "Make a page display about services for the company website in blue and red "
                        + "and provide some price information on each product card.", 3);

        attach(p, AttachmentCatalog.SPORTS_PROJECT, 20220520);
        attach(p, AttachmentCatalog.CLIENT_BRIEF, 20220520);
        attach(p, AttachmentCatalog.MOUNT_ASSET, 20220521);

        task(p, "Build header layout", Task.STATUS_TODO, Task.PRIORITY_HIGH, 0);
        task(p, "Add form validation", Task.STATUS_TODO, Task.PRIORITY_HIGH, 0);
        task(p, "Create brand guideline for website design", Task.STATUS_DONE, Task.PRIORITY_NORMAL, -4);
        task(p, "Create low fidelity service page website", Task.STATUS_TODO, Task.PRIORITY_NORMAL, -7);
        task(p, "Configure pricing table responsive grid", Task.STATUS_IN_PROGRESS, Task.PRIORITY_NORMAL, 3);
        task(p, "Draft hero copy and CTA text", Task.STATUS_DONE, Task.PRIORITY_LOW, -6);
        task(p, "Design pricing cards", Task.STATUS_TODO, Task.PRIORITY_NORMAL, NONE);
        task(p, "Write service descriptions", Task.STATUS_TODO, Task.PRIORITY_LOW, 5);
        task(p, "Set up responsive breakpoints", Task.STATUS_IN_PROGRESS, Task.PRIORITY_HIGH, 1);
        task(p, "Review color contrast", Task.STATUS_IN_PROGRESS, Task.PRIORITY_NORMAL, -2);
        task(p, "Export logo assets", Task.STATUS_DONE, Task.PRIORITY_LOW, -3);
        task(p, "Collect client feedback", Task.STATUS_DONE, Task.PRIORITY_NORMAL, NONE);
        return p;
    }

    private Project buildLoginApi() {
        Project p = new Project(newId(), "Login API", "Backend",
                "Build authentication endpoints and JWT token refresh handlers", 2);

        attach(p, AttachmentCatalog.API_SPEC, 20220518);
        attach(p, AttachmentCatalog.MEETING_NOTES, 20220519);

        task(p, "Design user table schema", Task.STATUS_DONE, Task.PRIORITY_HIGH, -9);
        task(p, "Implement password hashing", Task.STATUS_DONE, Task.PRIORITY_HIGH, -6);
        task(p, "Create registration endpoint", Task.STATUS_DONE, Task.PRIORITY_NORMAL, -4);
        task(p, "Implement login endpoint", Task.STATUS_IN_PROGRESS, Task.PRIORITY_HIGH, 1);
        task(p, "Add JWT token signing", Task.STATUS_IN_PROGRESS, Task.PRIORITY_HIGH, 2);
        task(p, "Build token refresh handler", Task.STATUS_TODO, Task.PRIORITY_NORMAL, 4);
        task(p, "Add rate limiting", Task.STATUS_TODO, Task.PRIORITY_NORMAL, NONE);
        task(p, "Write API documentation", Task.STATUS_TODO, Task.PRIORITY_LOW, 7);
        task(p, "Add unit tests for auth", Task.STATUS_TODO, Task.PRIORITY_NORMAL, -1);
        task(p, "Set up CI pipeline", Task.STATUS_TODO, Task.PRIORITY_LOW, NONE);
        return p;
    }

    private Project buildMobileDashboard() {
        Project p = new Project(newId(), "Mobile Dashboard", "Frontend",
                "Responsive overview widgets and telemetry cards", 1);

        attach(p, AttachmentCatalog.WIREFRAME, 20220517);

        task(p, "Sketch dashboard layout", Task.STATUS_DONE, Task.PRIORITY_NORMAL, -8);
        task(p, "Choose chart library", Task.STATUS_DONE, Task.PRIORITY_LOW, -5);
        task(p, "Build overview widgets", Task.STATUS_IN_PROGRESS, Task.PRIORITY_HIGH, 2);
        task(p, "Create telemetry card component", Task.STATUS_IN_PROGRESS, Task.PRIORITY_NORMAL, 3);
        task(p, "Add responsive breakpoints", Task.STATUS_TODO, Task.PRIORITY_NORMAL, 6);
        task(p, "Connect live data feed", Task.STATUS_TODO, Task.PRIORITY_HIGH, NONE);
        task(p, "Add dark mode", Task.STATUS_TODO, Task.PRIORITY_LOW, NONE);
        task(p, "Test on tablets", Task.STATUS_TODO, Task.PRIORITY_NORMAL, 10);
        return p;
    }

    private Project buildReleaseChecklist() {
        Project p = new Project(newId(), "Release Checklist", "QA & Ops",
                "Pre-production verification and smoke tests", 2);

        attach(p, AttachmentCatalog.RELEASE_NOTES, 20220522);

        task(p, "Run regression suite", Task.STATUS_DONE, Task.PRIORITY_HIGH, -3);
        task(p, "Verify staging deployment", Task.STATUS_DONE, Task.PRIORITY_HIGH, -2);
        task(p, "Complete smoke tests", Task.STATUS_DONE, Task.PRIORITY_NORMAL, -1);
        task(p, "Sign off release notes", Task.STATUS_DONE, Task.PRIORITY_LOW, 0);
        return p;
    }

    private Project buildDesignAssets() {
        return new Project(newId(), "Design Assets", "Design",
                "Vector icons and illustration library for landing pages", 0);
    }

    // ---- small helpers ----

    private static String newId() {
        return UUID.randomUUID().toString();
    }

    /** offsetDays is relative to today (0 = today, -3 = three days ago); NONE = no due date. */
    private void task(Project project, String description, int status, int priority, int offsetDays) {
        int due = offsetDays == NONE ? DateHelper.NO_DATE : DateHelper.addDays(today, offsetDays);
        clock += 60000L;
        project.addTask(new Task(newId(), description, status, priority, due, clock));
    }

    private void attach(Project project, String catalogId, int linkedDate) {
        CatalogItem item = AttachmentCatalog.findById(catalogId);
        project.addAttachment(AttachmentCatalog.createLink(item, DateHelper.millisOf(linkedDate)));
    }
}
