package com.example.taskflow.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a single to-do item that belongs to a Project.
 * Implements Parcelable so it can travel inside a Project object
 * through Intent extras between activities.
 */
public class Task implements Parcelable {

    private final String id;
    private final String description;
    private boolean completed;

    public Task(String id, String description, boolean completed) {
        this.id = id;
        this.description = description;
        this.completed = completed;
    }

    protected Task(Parcel in) {
        id = in.readString();
        description = in.readString();
        completed = in.readByte() != 0;
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(description);
        dest.writeByte((byte) (completed ? 1 : 0));
    }
}
