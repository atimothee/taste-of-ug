package com.tasteofuganda.app.provider.category;

import java.util.Date;

import android.content.ContentResolver;
import android.net.Uri;

import com.tasteofuganda.app.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code category} table.
 */
public class CategoryContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return CategoryColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, CategorySelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public CategoryContentValues putName(String value) {
        mContentValues.put(CategoryColumns.NAME, value);
        return this;
    }

    public CategoryContentValues putNameNull() {
        mContentValues.putNull(CategoryColumns.NAME);
        return this;
    }


    public CategoryContentValues putColor(String value) {
        mContentValues.put(CategoryColumns.COLOR, value);
        return this;
    }

    public CategoryContentValues putColorNull() {
        mContentValues.putNull(CategoryColumns.COLOR);
        return this;
    }

}
