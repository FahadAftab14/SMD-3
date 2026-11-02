package com.example.taskflow.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * One task inside a project. Status and priority are small int codes so the
 * object stays easy to parcel; use the *_LABELS arrays to show them.
 * The due date is a yyyyMMdd number (0 = no due date), see DateHelper.
 */
public class Task implements Parcelable {

    public static final int STATUS_TODO = 0;
    public static final int STATUS_IN_PROGRESS = 1;
    public static final int STATUS_DONE = 2;

    public static final int PRIORITY_HIGH = 0;
    public static final int PRIORITY_NORMAL = 1;
    public static final int PRIORITY_LOW = 2;

    public static final String[] STATUS_LABELS = {"To do", "In progress", "Done"};
    public static final String[] PRIORITY_LABELS = {"High", "Normal", "Low"};

    private final String id;
    private String description;
    private int status;
    private int priority;
    private int dueDate;
    private final long createdAt;

    public Task(String id, String description, int status, int priority, int dueDate, long createdAt) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.createdAt = createdAt;
    }

    protected Task(Parcel in) {
        id = in.readString();
        description = in.readString();
        status = in.readInt();
        priority = in.readInt();
        dueDate = in.readInt();
        createdAt = in.readLong();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getDueDate() {
        return dueDate;
    }

    public void setDueDate(int dueDate) {
        this.dueDate = dueDate;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isDone() {
        return status == STATUS_DONE;
    }

    /** Overdue = has a due date before today AND is not Done. */
    public boolean isOverdue(int today) {
        return !isDone() && dueDate != 0 && dueDate < today;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(description);
        dest.writeInt(status);
        dest.writeInt(priority);
        dest.writeInt(dueDate);
        dest.writeLong(createdAt);
    }
}
