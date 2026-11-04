# TaskFlow (SMD Assignment 3)

A project-management app built with **Fragments, TabLayout and built-in ListViews**. It extends the Assignment 2 app
(Splash -> Home -> Project) with tabs, search, filters, sorting, task editing and file attachments.

## Screens

| Screen | What it does |
| --- | --- |
| Splash | "Get Started" opens Home |
| Home | Welcome, workspace card, **+ New Project**, project search, ListView of projects (progress bar, attachments, comments) |
| Project > Overview | Description + **Edit Project**, progress, To do / In progress / Done / Overdue cards, quick links, Add Task |
| Project > Tasks | Search, Status / Priority / Date / Sort, task ListView with checkbox, edit, delete, OVERDUE badge |
| Project > Files | Search, All / Images / Text chips, Preview (real image or text), Add from catalog, remove with typed confirmation |
| Project > Discussion | "Coming Soon" |

## Project structure

```
com.example.taskflow
  SplashActivity, HomeActivity, ProjectActivity
  data/        Project, Task, AttachmentLink (Parcelable records), CatalogItem, AttachmentCatalog,
               SampleData, TaskQuery (search + filters + sort)
  fragments/   ProjectHost (callback interface), ProjectTabFragment (base),
               OverviewFragment, TasksFragment, FilesFragment, DiscussionFragment
  adapters/    ProjectListAdapter, TaskListAdapter, AttachmentListAdapter (BaseAdapter for ListView)
  ui/          ProjectDialogHelper, TaskDialogHelper, CatalogPickerDialog,
               AttachmentPreviewDialog, RemoveAttachmentDialog
  util/        DateHelper, SimpleTextWatcher, ResourceRef
res/drawable   catalog images (PNG/JPG)      res/raw   catalog text documents
```

## How the data flows

* `HomeActivity` owns the **full list of projects**. Each project has a unique id.
* Clicking a row sends that `Project` to `ProjectActivity` as a Parcelable intent extra.
* `ProjectActivity` keeps **one working project object**. Each fragment gets only the project **id** in its
  arguments and reads the live object through the `ProjectHost` interface. When something changes the fragment calls
  `host.onProjectDataChanged()`, which refreshes the header, the tab counts and every fragment.
* On the toolbar arrow or system Back, the project goes back in the result intent and Home replaces its copy **by id**
  (no duplicate project, no new HomeActivity).
* All totals (task counts, percentage, attachment count) are **calculated from the records**; nothing is stored twice.
  Progress = Done / total x 100, fraction dropped (4/12 = 33%, empty project = 0/0 and 0%).
* Attachments store a catalog id and a **resource reference** (for example `drawable/catalog_wireframe`), never image bytes.
* Dates are stored as `yyyyMMdd` numbers. Overdue = due date before today **and** status is not Done.

## Manual test checklist

- [ ] Splash -> Get Started opens Home
- [ ] Home shows 5 projects; main project reads 4/12 tasks, 33%, 3 attachments, 3 comments
- [ ] Search "WEB" finds Service Page Website; search "backend" finds Login API (tag)
- [ ] + New Project: empty / spaces-only fields show errors; valid input adds a 0/0, 0% project at the end
- [ ] Open a project: header shows its name and tag; tabs read Tasks (12), Files (3), Discussion (3)
- [ ] Overview: To do 5 + In progress 3 + Done 4 = 12; Overdue 2 is separate
- [ ] Tap Overdue card -> Tasks tab selected, All statuses, Overdue date filter, 2 tasks
- [ ] Add Task from Overview: stays on Overview, counts and tab title update
- [ ] Tasks: tick a checkbox -> status Done, percentage updates everywhere; edit/delete a task
- [ ] Tasks: combine search + status + priority + date; Reset filters restores all five defaults
- [ ] Files: search + type chip together; Preview shows the real image / text; add from catalog; remove needs the typed name
- [ ] Back to Home: the project row shows the new counts; no duplicate project appears
