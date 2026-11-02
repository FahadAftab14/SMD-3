package com.example.taskflow.data;

import android.os.Bundle;

import com.example.taskflow.util.DateHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * The Tasks tab's search + filters + sort, held in one object so they are
 * always applied together. A task is shown only if it matches the search AND
 * every selected filter.
 */
public class TaskQuery {

    /** Used for "All statuses" and "All priorities". */
    public static final int ANY = -1;

    public static final int DATE_ALL = 0;
    public static final int DATE_TODAY = 1;
    public static final int DATE_OVERDUE = 2;
    public static final int DATE_NONE = 3;

    public static final int SORT_DUE_ASC = 0;
    public static final int SORT_DUE_DESC = 1;
    public static final int SORT_PRIORITY = 2;
    public static final int SORT_NEWEST = 3;
    public static final int SORT_ALPHA = 4;

    // Menu texts. Index 0 is "all" for status/priority, so choice index = value + 1.
    public static final String[] STATUS_OPTIONS = {"All statuses", "To do", "In progress", "Done"};
    public static final String[] PRIORITY_OPTIONS = {"All priorities", "High", "Normal", "Low"};
    public static final String[] DATE_OPTIONS = {"All dates", "Today", "Overdue", "No due date"};
    public static final String[] SORT_OPTIONS = {
            "Due date (earliest first)", "Due date (latest first)",
            "Priority (High to Low)", "Newest first", "Description (A-Z)"};

    // Short texts for the filter pills.
    public static final String[] STATUS_SHORT = {"All", "To do", "In progress", "Done"};
    public static final String[] PRIORITY_SHORT = {"All", "High", "Normal", "Low"};
    public static final String[] DATE_SHORT = {"All", "Today", "Overdue", "No due date"};
    public static final String[] SORT_SHORT = {"Due date \u2191", "Due date \u2193", "Priority", "Newest", "A-Z"};

    private static final String KEY_SEARCH = "query_search";
    private static final String KEY_STATUS = "query_status";
    private static final String KEY_PRIORITY = "query_priority";
    private static final String KEY_DATE = "query_date";
    private static final String KEY_SORT = "query_sort";

    public String search = "";
    public int status = ANY;
    public int priority = ANY;
    public int date = DATE_ALL;
    public int sort = SORT_DUE_ASC;

    /** Restores all five defaults. */
    public void reset() {
        search = "";
        status = ANY;
        priority = ANY;
        date = DATE_ALL;
        sort = SORT_DUE_ASC;
    }

    public void saveTo(Bundle bundle) {
        bundle.putString(KEY_SEARCH, search);
        bundle.putInt(KEY_STATUS, status);
        bundle.putInt(KEY_PRIORITY, priority);
        bundle.putInt(KEY_DATE, date);
        bundle.putInt(KEY_SORT, sort);
    }

    public void restoreFrom(Bundle bundle) {
        search = bundle.getString(KEY_SEARCH, "");
        status = bundle.getInt(KEY_STATUS, ANY);
        priority = bundle.getInt(KEY_PRIORITY, ANY);
        date = bundle.getInt(KEY_DATE, DATE_ALL);
        sort = bundle.getInt(KEY_SORT, SORT_DUE_ASC);
    }

    public boolean matches(Task task, int today) {
        String needle = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        if (!needle.isEmpty() && !task.getDescription().toLowerCase(Locale.ROOT).contains(needle)) {
            return false;
        }
        if (status != ANY && task.getStatus() != status) {
            return false;
        }
        if (priority != ANY && task.getPriority() != priority) {
            return false;
        }
        switch (date) {
            case DATE_TODAY:
                return task.getDueDate() == today;
            case DATE_OVERDUE:
                return task.isOverdue(today);
            case DATE_NONE:
                return task.getDueDate() == DateHelper.NO_DATE;
            default:
                return true;
        }
    }

    /** Returns a new list: the matching tasks, sorted. The source list is not changed. */
    public ArrayList<Task> apply(List<Task> source, int today) {
        ArrayList<Task> result = new ArrayList<>();
        for (Task task : source) {
            if (matches(task, today)) {
                result.add(task);
            }
        }
        Collections.sort(result, comparator());
        return result;
    }

    private Comparator<Task> comparator() {
        final int mode = sort;
        return new Comparator<Task>() {
            @Override
            public int compare(Task a, Task b) {
                int result;
                switch (mode) {
                    case SORT_DUE_DESC:
                        result = compareDue(a, b, true);
                        break;
                    case SORT_PRIORITY:
                        result = Integer.compare(a.getPriority(), b.getPriority());
                        if (result == 0) {
                            result = compareDue(a, b, false);
                        }
                        break;
                    case SORT_NEWEST:
                        result = Long.compare(b.getCreatedAt(), a.getCreatedAt());
                        break;
                    case SORT_ALPHA:
                        result = a.getDescription().compareToIgnoreCase(b.getDescription());
                        break;
                    default:
                        result = compareDue(a, b, false);
                        break;
                }
                if (result != 0) {
                    return result;
                }
                // Stable tie-breakers so the order never jumps around.
                result = Long.compare(a.getCreatedAt(), b.getCreatedAt());
                return result != 0 ? result : a.getId().compareTo(b.getId());
            }
        };
    }

    /** Tasks without a due date always go last, in both directions. */
    private static int compareDue(Task a, Task b, boolean descending) {
        int dueA = a.getDueDate();
        int dueB = b.getDueDate();
        if (dueA == dueB) {
            return 0;
        }
        if (dueA == DateHelper.NO_DATE) {
            return 1;
        }
        if (dueB == DateHelper.NO_DATE) {
            return -1;
        }
        return descending ? Integer.compare(dueB, dueA) : Integer.compare(dueA, dueB);
    }
}
