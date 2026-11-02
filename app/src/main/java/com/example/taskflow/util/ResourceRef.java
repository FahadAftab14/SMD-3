package com.example.taskflow.util;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Attachments store a resource REFERENCE such as "drawable/catalog_wireframe"
 * or "raw/catalog_api_spec", never the image bytes. This class turns a
 * reference back into a real resource id and reads text resources.
 */
public final class ResourceRef {

    private ResourceRef() {
    }

    /** Returns the resource id, or 0 if the reference is invalid. */
    public static int resolve(Context context, String reference) {
        if (reference == null) {
            return 0;
        }
        int slash = reference.indexOf('/');
        if (slash <= 0 || slash == reference.length() - 1) {
            return 0;
        }
        String type = reference.substring(0, slash);
        String name = reference.substring(slash + 1);
        return context.getResources().getIdentifier(name, type, context.getPackageName());
    }

    /** Reads a raw text resource, or returns null if it cannot be read. */
    public static String readText(Context context, String reference) {
        int resId = resolve(context, reference);
        if (resId == 0) {
            return null;
        }
        try (InputStream in = context.getResources().openRawResource(resId);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return out.toString("UTF-8");
        } catch (IOException e) {
            return null;
        }
    }
}
