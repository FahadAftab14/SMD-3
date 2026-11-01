package com.example.taskflow.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Represents a Project shown on the Home screen and opened in ProjectActivity.
 * Implements Parcelable so the SAME object (with live task/attachment data)
 * can be passed forward via Intent.putExtra() and returned back via
 * setResult()/Activity Result API instead of being hardcoded or duplicated.
 */
public class Project implements Parcelable {

    private final String id;
    private final String tag;
    private final String name;
    private final String description;
    private final ArrayList<String> attachments;
    private final ArrayList<Task> tasks;

    public Project(String id, String tag, String name, String description,
                   ArrayList<String> attachments, ArrayList<Task> tasks) {
        this.id = id;
        this.tag = tag;
        this.name = name;
        this.description = description;
        this.attachments = attachments != null ? attachments : new ArrayList<>();
        this.tasks = tasks != null ? tasks : new ArrayList<>();
    }

    protected Project(Parcel in) {
        id = in.readString();
        tag = in.readString();
        name = in.readString();
        description = in.readString();
        attachments = in.createStringArrayList();
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

    public String getId() {
        return id;
    }

    public String getTag() {
        return tag;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getAttachments() {
        return attachments;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public int getCompletedTaskCount() {
        int count = 0;
        for (Task task : tasks) {
            if (task.isCompleted()) count++;
        }
        return count;
    }

    public int getTotalTaskCount() {
        return tasks.size();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(tag);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeStringList(attachments);
        dest.writeTypedList(tasks);
    }
}
