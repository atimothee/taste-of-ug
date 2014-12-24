package com.tasteofuganda.app.provider.category;

import java.util.Date;

import android.database.Cursor;

import com.tasteofuganda.app.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code category} table.
 */
public class CategoryCursor extends AbstractCursor {
    public CategoryCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Get the {@code name} value.
     * Can be {@code null}.
     */
    public String getName() {
        Integer index = getCachedColumnIndexOrThrow(CategoryColumns.NAME);
        return getString(index);
    }

    /**
     * Get the {@code color} value.
     * Can be {@code null}.
     */
    public String getColor() {
        Integer index = getCachedColumnIndexOrThrow(CategoryColumns.COLOR);
        return getString(index);
    }
}
