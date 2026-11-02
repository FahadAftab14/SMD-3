package com.example.taskflow.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A link between one project and one catalog item. It stores only the catalog
 * id and a resource REFERENCE (for example "drawable/catalog_wireframe"),
 * never the file bytes.
 */
public class AttachmentLink implements Parcelable {

    private final String id;
    private final String catalogId;
    private final String resourceRef;
    private final long linkedAt;

    public AttachmentLink(String id, String catalogId, String resourceRef, long linkedAt) {
        this.id = id;
        this.catalogId = catalogId;
        this.resourceRef = resourceRef;
        this.linkedAt = linkedAt;
    }

    protected AttachmentLink(Parcel in) {
        id = in.readString();
        catalogId = in.readString();
        resourceRef = in.readString();
        linkedAt = in.readLong();
    }

    public static final Creator<AttachmentLink> CREATOR = new Creator<AttachmentLink>() {
        @Override
        public AttachmentLink createFromParcel(Parcel in) {
            return new AttachmentLink(in);
        }

        @Override
        public AttachmentLink[] newArray(int size) {
            return new AttachmentLink[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public String getResourceRef() {
        return resourceRef;
    }

    public long getLinkedAt() {
        return linkedAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(catalogId);
        dest.writeString(resourceRef);
        dest.writeLong(linkedAt);
    }
}
