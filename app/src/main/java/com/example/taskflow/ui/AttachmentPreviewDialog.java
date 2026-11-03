package com.example.taskflow.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.taskflow.R;
import com.example.taskflow.data.AttachmentLink;
import com.example.taskflow.data.CatalogItem;
import com.example.taskflow.util.ResourceRef;

/**
 * Shows the REAL content of an attachment: an image in an ImageView, or a text
 * document in a TextView, both read from the resource reference stored in the link.
 */
public final class AttachmentPreviewDialog {

    private AttachmentPreviewDialog() {
    }

    public static void show(Context context, AttachmentLink link, CatalogItem item) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_attachment_preview, null);
        TextView tvTitle = view.findViewById(R.id.tvPreviewTitle);
        TextView tvMeta = view.findViewById(R.id.tvPreviewMeta);
        ImageView ivImage = view.findViewById(R.id.ivPreviewImage);
        View textContainer = view.findViewById(R.id.scrollPreviewText);
        TextView tvText = view.findViewById(R.id.tvPreviewText);
        TextView btnClose = view.findViewById(R.id.btnPreviewClose);

        String name = item != null ? item.getDisplayName() : "Attachment";
        tvTitle.setText(name);
        tvMeta.setText(item != null ? item.getTypeLabel() : "");

        boolean isImage = item != null && item.isImage();
        if (isImage) {
            int resId = ResourceRef.resolve(context, link.getResourceRef());
            if (resId != 0) {
                ivImage.setImageResource(resId);
                ivImage.setContentDescription("Preview of " + name);
                ivImage.setVisibility(View.VISIBLE);
            } else {
                showMessage(textContainer, tvText, ivImage, "Preview is not available.");
            }
        } else {
            String text = ResourceRef.readText(context, link.getResourceRef());
            showMessage(textContainer, tvText, ivImage, text != null ? text : "Preview is not available.");
        }

        final AlertDialog dialog = new AlertDialog.Builder(context).setView(view).create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static void showMessage(View container, TextView textView, ImageView imageView, String message) {
        imageView.setVisibility(View.GONE);
        textView.setText(message);
        container.setVisibility(View.VISIBLE);
    }
}
