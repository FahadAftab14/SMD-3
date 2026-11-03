package com.example.taskflow.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.taskflow.R;
import com.example.taskflow.util.SimpleTextWatcher;

/**
 * Asks the user to type the attachment's name before it is removed.
 * The Remove button stays disabled until the typed name matches exactly
 * (surrounding spaces are ignored).
 */
public final class RemoveAttachmentDialog {

    public interface OnConfirmed {
        void onConfirmed();
    }

    private RemoveAttachmentDialog() {
    }

    public static void show(Context context, final String attachmentName, final OnConfirmed callback) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_remove_attachment, null);
        TextView tvMessage = view.findViewById(R.id.tvRemoveMessage);
        final EditText etName = view.findViewById(R.id.etRemoveName);
        TextView btnCancel = view.findViewById(R.id.btnRemoveCancel);
        final TextView btnRemove = view.findViewById(R.id.btnRemoveConfirm);

        tvMessage.setText("To remove this attachment, type its name exactly:\n\n" + attachmentName);
        btnRemove.setEnabled(false);
        btnRemove.setAlpha(0.4f);

        etName.addTextChangedListener(new SimpleTextWatcher(new SimpleTextWatcher.Callback() {
            @Override
            public void onTextChanged(String text) {
                boolean matches = text.trim().equals(attachmentName);
                btnRemove.setEnabled(matches);
                btnRemove.setAlpha(matches ? 1f : 0.4f);
            }
        }));

        final AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.onConfirmed();
            }
        });
        dialog.show();
    }
}
