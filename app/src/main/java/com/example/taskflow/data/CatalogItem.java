package com.example.taskflow.data;

/**
 * One file in the local catalog. The actual content lives in the app's
 * resources: images are PNG/JPG files in res/drawable, text documents are
 * .txt files in res/raw. resourceRef points at it, e.g. "drawable/catalog_x".
 */
public final class CatalogItem {

    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_TEXT = 1;

    private final String id;
    private final String displayName;
    private final int type;
    private final String resourceRef;
    private final String sizeLabel;

    public CatalogItem(String id, String displayName, int type, String resourceRef, String sizeLabel) {
        this.id = id;
        this.displayName = displayName;
        this.type = type;
        this.resourceRef = resourceRef;
        this.sizeLabel = sizeLabel;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getType() {
        return type;
    }

    public boolean isImage() {
        return type == TYPE_IMAGE;
    }

    public String getResourceRef() {
        return resourceRef;
    }

    /** Sample metadata only (not a real file size). */
    public String getSizeLabel() {
        return sizeLabel;
    }

    public String getTypeLabel() {
        return isImage() ? "Image" : "Text";
    }
}
