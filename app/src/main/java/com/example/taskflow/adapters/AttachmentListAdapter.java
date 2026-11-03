package com.example.taskflow.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taskflow.R;
import com.example.taskflow.data.AttachmentCatalog;
import com.example.taskflow.data.AttachmentLink;
import com.example.taskflow.data.CatalogItem;
import com.example.taskflow.util.DateHelper;
import com.example.taskflow.util.ResourceRef;

import java.util.List;

/**
 * Feeds the Files ListView. Each row shows a thumbnail (images) or a document
 * icon (text), the display name, "type - size - linked date", a Preview
 * action and a remove icon.
 */
public class AttachmentListAdapter extends BaseAdapter {

    public interface Listener {
        void onPreview(AttachmentLink link);

        void onRemove(AttachmentLink link);
    }

    private final Context context;
    private final List<AttachmentLink> links;
    private final Listener listener;

    public AttachmentListAdapter(Context context, List<AttachmentLink> links, Listener listener) {
        this.context = context;
        this.links = links;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return links.size();
    }

    @Override
    public AttachmentLink getItem(int position) {
        return links.get(position);
    }

    @Override
    public long getItemId(int position) {
        return links.get(position).getId().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder;
        if (row == null) {
            row = LayoutInflater.from(context).inflate(R.layout.row_attachment, parent, false);
            holder = new Holder(row);
            row.setTag(holder);
        } else {
            holder = (Holder) row.getTag();
        }

        final AttachmentLink link = links.get(position);
        CatalogItem item = AttachmentCatalog.findById(link.getCatalogId());
        String name = item != null ? item.getDisplayName() : "Unknown file";

        holder.tvName.setText(name);
        if (item != null) {
            holder.tvMeta.setText(item.getTypeLabel() + " \u2022 " + item.getSizeLabel()
                    + " \u2022 Linked " + DateHelper.formatShort(link.getLinkedAt()));
        } else {
            holder.tvMeta.setText("Not in catalog");
        }

        if (item != null && item.isImage()) {
            int resId = ResourceRef.resolve(context, link.getResourceRef());
            if (resId != 0) {
                holder.ivThumb.setImageResource(resId);
            }
            holder.ivThumb.setVisibility(View.VISIBLE);
            holder.tvIcon.setVisibility(View.GONE);
        } else {
            holder.ivThumb.setVisibility(View.GONE);
            holder.tvIcon.setVisibility(View.VISIBLE);
        }

        holder.tvPreview.setContentDescription("Preview " + name);
        holder.tvRemove.setContentDescription("Remove " + name);
        holder.tvPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPreview(link);
            }
        });
        holder.tvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRemove(link);
            }
        });
        return row;
    }

    private static class Holder {
        final ImageView ivThumb;
        final TextView tvIcon;
        final TextView tvName;
        final TextView tvMeta;
        final TextView tvPreview;
        final TextView tvRemove;

        Holder(View row) {
            ivThumb = row.findViewById(R.id.ivAttachmentThumb);
            tvIcon = row.findViewById(R.id.tvAttachmentIcon);
            tvName = row.findViewById(R.id.tvAttachmentName);
            tvMeta = row.findViewById(R.id.tvAttachmentMeta);
            tvPreview = row.findViewById(R.id.tvAttachmentPreview);
            tvRemove = row.findViewById(R.id.tvAttachmentRemove);
        }
    }
}
