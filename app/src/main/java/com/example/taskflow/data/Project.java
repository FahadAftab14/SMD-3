package com.example.taskflow.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * One project. It owns its tasks and attachment links, and every total shown
 * anywhere in the app (task counts, percentage, attachment count) is computed
 * from these lists, never stored separately. That is what keeps Home, Overview,
 * Tasks and Files consistent.
 */
public class Project implements Parcelable {

    private final String id;
    private String name;
    private String tag;
    private String description;
    private final int commentCount;
    private final ArrayList<AttachmentLink> attachments;
    private final ArrayList<Task> tasks;

    public Project(String id, String name, String tag, String description, int commentCount) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.description = description;
        this.commentCount = commentCount;
        this.attachments = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }

    protected Project(Parcel in) {
        id = in.readString();
        name = in.readString();
        tag = in.readString();
        description = in.readString();
        commentCount = in.readInt();
        attachments = in.createTypedArrayList(AttachmentLink.CREATOR);
        tasks = in.createTypedArrayList(Task.CREATOR);
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    // ---- basic info ----

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public String getDescription() {
        return description;
    }

    /** Dummy comment count (the Discussion tab is "Coming Soon"). */
    public int getCommentCount() {
        return commentCount;
    }

    public void updateInfo(String name, String tag, String description) {
        this.name = name;
        this.tag = tag;
        this.description = description;
    }

    // ---- tasks ----

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    /** Finds a task by its unique id (never by name or row position). */
    public Task findTask(String taskId) {
        for (Task task : tasks) {
            if (task.getId().equals(taskId)) {
                return task;
            }
        }
        return null;
    }

    public boolean removeTask(String taskId) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(taskId)) {
                tasks.remove(i);
                return true;
            }
        }
        return false;
    }

    public int getTotalTaskCount() {
        return tasks.size();
    }

    public int getTaskCountByStatus(int status) {
        int count = 0;
        for (Task task : tasks) {
            if (task.getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    public int getDoneTaskCount() {
        return getTaskCountByStatus(Task.STATUS_DONE);
    }

    /** Overdue is a subset of unfinished tasks; it is NOT a fourth status. */
    public int getOverdueTaskCount(int today) {
        int count = 0;
        for (Task task : tasks) {
            if (task.isOverdue(today)) {
                count++;
            }
        }
        return count;
    }

    /** Done / total * 100 with the fraction dropped (4/12 -> 33). Empty -> 0. */
    public int getProgressPercent() {
        if (tasks.isEmpty()) {
            return 0;
        }
        return getDoneTaskCount() * 100 / tasks.size();
    }

    // ---- attachments ----

    public ArrayList<AttachmentLink> getAttachments() {
        return attachments;
    }

    public void addAttachment(AttachmentLink link) {
        attachments.add(link);
    }

    public boolean removeAttachment(String linkId) {
        for (int i = 0; i < attachments.size(); i++) {
            if (attachments.get(i).getId().equals(linkId)) {
                attachments.remove(i);
                return true;
            }
        }
        return false;
    }

    public Set<String> getLinkedCatalogIds() {
        Set<String> ids = new HashSet<>();
        for (AttachmentLink link : attachments) {
            ids.add(link.getCatalogId());
        }
        return ids;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(tag);
        dest.writeString(description);
        dest.writeInt(commentCount);
        dest.writeTypedList(attachments);
        dest.writeTypedList(tasks);
    }
}
