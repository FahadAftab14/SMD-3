package com.example.taskflow.ui;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.example.taskflow.data.AttachmentCatalog;
import com.example.taskflow.data.CatalogItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Single-choice AlertDialog listing the local catalog. Items whose ids are in
 * "excludedCatalogIds" (already linked) are not offered. The positive button
 * stays disabled until one item is selected.
 */
public final class CatalogPickerDialog {

    public interface OnItemChosen {
        void onChosen(CatalogItem item);
    }

    private CatalogPickerDialog() {
    }

    public static void show(Context context, String title, String positiveLabel,
                            Set<String> excludedCatalogIds, final OnItemChosen callback) {
        final List<CatalogItem> eligible = new ArrayList<>();
        for (CatalogItem item : AttachmentCatalog.getAll()) {
            if (!excludedCatalogIds.contains(item.getId())) {
                eligible.add(item);
            }
        }

        if (eligible.isEmpty()) {
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage("Every catalog item is already linked to this project.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        CharSequence[] labels = new CharSequence[eligible.size()];
        for (int i = 0; i < eligible.size(); i++) {
            CatalogItem item = eligible.get(i);
            labels[i] = item.getDisplayName() + "  (" + item.getTypeLabel() + ")";
        }

        final int[] selectedIndex = {-1};
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setSingleChoiceItems(labels, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        selectedIndex[0] = which;
                        android.widget.Button positive = ((AlertDialog) d).getButton(AlertDialog.BUTTON_POSITIVE);
                        if (positive != null) {
                            positive.setEnabled(true);
                        }
                    }
                })
                .setPositiveButton(positiveLabel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        if (selectedIndex[0] >= 0) {
                            callback.onChosen(eligible.get(selectedIndex[0]));
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                android.widget.Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if (positive != null) {
                    positive.setEnabled(selectedIndex[0] >= 0);
                }
            }
        });
        dialog.show();
    }
}
