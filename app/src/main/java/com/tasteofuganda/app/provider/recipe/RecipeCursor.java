package com.tasteofuganda.app.provider.recipe;

import java.util.Date;

import android.database.Cursor;

import com.tasteofuganda.app.provider.base.AbstractCursor;
import com.tasteofuganda.app.provider.category.*;

/**
 * Cursor wrapper for the {@code recipe} table.
 */
public class RecipeCursor extends AbstractCursor {
    public RecipeCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Get the {@code recipe_name} value.
     * Can be {@code null}.
     */
    public String getRecipeName() {
        Integer index = getCachedColumnIndexOrThrow(RecipeColumns.RECIPE_NAME);
        return getString(index);
    }

    /**
     * Get the {@code description} value.
     * Can be {@code null}.
     */
    public String getDescription() {
        Integer index = getCachedColumnIndexOrThrow(RecipeColumns.DESCRIPTION);
        return getString(index);
    }

    /**
     * Get the {@code ingredients} value.
     * Can be {@code null}.
     */
    public String getIngredients() {
        Integer index = getCachedColumnIndexOrThrow(RecipeColumns.INGREDIENTS);
        return getString(index);
    }

    /**
     * Get the {@code directions} value.
     * Can be {@code null}.
     */
    public String getDirections() {
        Integer index = getCachedColumnIndexOrThrow(RecipeColumns.DIRECTIONS);
        return getString(index);
    }

    /**
     * Get the {@code categoryid} value.
     * Can be {@code null}.
     */
    public Long getCategoryid() {
        return getLongOrNull(RecipeColumns.CATEGORYID);
    }

    /**
     * Get the {@code imagekey} value.
     * Can be {@code null}.
     */
    public String getImagekey() {
        Integer index = getCachedColumnIndexOrThrow(RecipeColumns.IMAGEKEY);
        return getString(index);
    }

    /**
     * Get the {@code imageurl} value.
     * Can be {@code null}.
     */
    public String getImageurl() {
        Integer index = getCachedColumnIndexOrThrow(RecipeColumns.IMAGEURL);
        return getString(index);
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
