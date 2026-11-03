package com.example.taskflow.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * The fixed local catalog every project can link to. Each item has an id,
 * a display name, a type and real content in the app's resources.
 * (The first three display names come from the Figma design; their content
 * is a PNG image or a plain-text document.)
 */
public final class AttachmentCatalog {

    public static final String SPORTS_PROJECT = "cat_sports_project";
    public static final String CLIENT_BRIEF = "cat_client_brief";
    public static final String MOUNT_ASSET = "cat_mount_asset";
    public static final String BRAND_GUIDE = "cat_brand_guide";
    public static final String WIREFRAME = "cat_wireframe";
    public static final String COLOR_PALETTE = "cat_color_palette";
    public static final String MEETING_NOTES = "cat_meeting_notes";
    public static final String API_SPEC = "cat_api_spec";
    public static final String HERO_BANNER = "cat_hero_banner";
    public static final String RELEASE_NOTES = "cat_release_notes";

    private static final List<CatalogItem> ITEMS = new ArrayList<>();

    static {
        ITEMS.add(new CatalogItem(SPORTS_PROJECT, "sports-projectv2.fig", CatalogItem.TYPE_IMAGE,
                "drawable/catalog_sports_project", "2.4 MB"));
        ITEMS.add(new CatalogItem(CLIENT_BRIEF, "briefing from client.pdf", CatalogItem.TYPE_TEXT,
                "raw/catalog_client_brief", "450 KB"));
        ITEMS.add(new CatalogItem(MOUNT_ASSET, "mount asset.png", CatalogItem.TYPE_IMAGE,
                "drawable/catalog_mount_asset", "1.1 MB"));
        ITEMS.add(new CatalogItem(BRAND_GUIDE, "brand-guidelines.txt", CatalogItem.TYPE_TEXT,
                "raw/catalog_brand_guide", "12 KB"));
        ITEMS.add(new CatalogItem(WIREFRAME, "wireframe-sketch.png", CatalogItem.TYPE_IMAGE,
                "drawable/catalog_wireframe", "860 KB"));
        ITEMS.add(new CatalogItem(COLOR_PALETTE, "color-palette.jpg", CatalogItem.TYPE_IMAGE,
                "drawable/catalog_color_palette", "540 KB"));
        ITEMS.add(new CatalogItem(MEETING_NOTES, "meeting-notes.txt", CatalogItem.TYPE_TEXT,
                "raw/catalog_meeting_notes", "8 KB"));
        ITEMS.add(new CatalogItem(API_SPEC, "api-spec.txt", CatalogItem.TYPE_TEXT,
                "raw/catalog_api_spec", "21 KB"));
        ITEMS.add(new CatalogItem(HERO_BANNER, "hero-banner.png", CatalogItem.TYPE_IMAGE,
                "drawable/catalog_hero_banner", "1.8 MB"));
        ITEMS.add(new CatalogItem(RELEASE_NOTES, "release-notes.txt", CatalogItem.TYPE_TEXT,
                "raw/catalog_release_notes", "6 KB"));
    }

    private AttachmentCatalog() {
    }

    public static List<CatalogItem> getAll() {
        return Collections.unmodifiableList(ITEMS);
    }

    public static CatalogItem findById(String catalogId) {
        for (CatalogItem item : ITEMS) {
            if (item.getId().equals(catalogId)) {
                return item;
            }
        }
        return null;
    }

    /** Creates a brand-new link (with its own unique id) to a catalog item. */
    public static AttachmentLink createLink(CatalogItem item) {
        return new AttachmentLink(UUID.randomUUID().toString(), item.getId(),
                item.getResourceRef(), System.currentTimeMillis());
    }

    /** Same, but with a chosen "linked on" date (used by the sample data). */
    public static AttachmentLink createLink(CatalogItem item, long linkedAt) {
        return new AttachmentLink(UUID.randomUUID().toString(), item.getId(),
                item.getResourceRef(), linkedAt);
    }
}
