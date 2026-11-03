package com.example.taskflow.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.taskflow.R;
import com.example.taskflow.data.CatalogItem;
import com.example.taskflow.data.Project;

import java.util.HashSet;

/**
 * One AlertDialog for both "New Project" (Home) and "Edit Project" (Overview).
 * Rules: name 1-60, tag 1-20, description 1-500 characters, after trimming
 * surrounding spaces. A whitespace-only value counts as empty. The optional
 * attachment selector is only shown when creating a new project.
 */
public final class ProjectDialogHelper {

    public static final int MAX_NAME = 60;
    public static final int MAX_TAG = 20;
    public static final int MAX_DESCRIPTION = 500;

    public interface Listener {
        /** attachment is null when none was selected (or when editing). */
        void onSave(String name, String tag, String description, CatalogItem attachment);
    }

    private ProjectDialogHelper() {
    }

    /** Pass existing = null to create a new project, or a project to edit it. */
    public static void show(final Context context, Project existing, final Listener listener) {
        final boolean isEdit = existing != null;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_project_form, null);

        TextView tvTitle = view.findViewById(R.id.tvFormTitle);
        final EditText etName = view.findViewById(R.id.etFormName);
        final EditText etTag = view.findViewById(R.id.etFormTag);
        final EditText etDescription = view.findViewById(R.id.etFormDescription);
        View attachmentGroup = view.findViewById(R.id.groupFormAttachment);
        final TextView tvAttachment = view.findViewById(R.id.tvFormAttachment);
        final TextView tvAttachmentClear = view.findViewById(R.id.tvFormAttachmentClear);
        TextView btnCancel = view.findViewById(R.id.btnFormCancel);
        TextView btnSave = view.findViewById(R.id.btnFormSave);

        tvTitle.setText(isEdit ? "Edit Project" : "New Project");
        btnSave.setText(isEdit ? "Save" : "Add");

        final CatalogItem[] selected = {null};
        final Runnable showSelectedAttachment = new Runnable() {
            @Override
            public void run() {
                if (selected[0] == null) {
                    tvAttachment.setText("Select from catalog (optional)");
                    tvAttachmentClear.setVisibility(View.GONE);
                } else {
                    tvAttachment.setText(selected[0].getDisplayName());
                    tvAttachmentClear.setVisibility(View.VISIBLE);
                }
            }
        };

        if (isEdit) {
            etName.setText(existing.getName());
            etTag.setText(existing.getTag());
            etDescription.setText(existing.getDescription());
            attachmentGroup.setVisibility(View.GONE);
        } else {
            showSelectedAttachment.run();
            tvAttachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CatalogPickerDialog.show(context, "Select attachment", "Select",
                            new HashSet<String>(), new CatalogPickerDialog.OnItemChosen() {
                                @Override
                                public void onChosen(CatalogItem item) {
                                    selected[0] = item;
                                    showSelectedAttachment.run();
                                }
                            });
                }
            });
            tvAttachmentClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected[0] = null;
                    showSelectedAttachment.run();
                }
            });
        }

        final AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String tag = etTag.getText().toString().trim();
                String description = etDescription.getText().toString().trim();

                boolean valid = true;
                if (name.length() < 1 || name.length() > MAX_NAME) {
                    etName.setError("Name must be 1-" + MAX_NAME + " characters");
                    valid = false;
                }
                if (tag.length() < 1 || tag.length() > MAX_TAG) {
                    etTag.setError("Tag must be 1-" + MAX_TAG + " characters");
                    valid = false;
                }
                if (description.length() < 1 || description.length() > MAX_DESCRIPTION) {
                    etDescription.setError("Description must be 1-" + MAX_DESCRIPTION + " characters");
                    valid = false;
                }
                if (!valid) {
                    return;
                }

                dialog.dismiss();
                listener.onSave(name, tag, description, isEdit ? null : selected[0]);
            }
        });

        dialog.show();
    }
}
