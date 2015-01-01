package com.tasteofuganda.app.provider.recipe;

import java.util.Date;

import android.content.ContentResolver;
import android.net.Uri;

import com.tasteofuganda.app.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code recipe} table.
 */
public class RecipeContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return RecipeColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, RecipeSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public RecipeContentValues putRecipeName(String value) {
        mContentValues.put(RecipeColumns.RECIPE_NAME, value);
        return this;
    }

    public RecipeContentValues putRecipeNameNull() {
        mContentValues.putNull(RecipeColumns.RECIPE_NAME);
        return this;
    }


    public RecipeContentValues putDescription(String value) {
        mContentValues.put(RecipeColumns.DESCRIPTION, value);
        return this;
    }

    public RecipeContentValues putDescriptionNull() {
        mContentValues.putNull(RecipeColumns.DESCRIPTION);
        return this;
    }


    public RecipeContentValues putIngredients(String value) {
        mContentValues.put(RecipeColumns.INGREDIENTS, value);
        return this;
    }

    public RecipeContentValues putIngredientsNull() {
        mContentValues.putNull(RecipeColumns.INGREDIENTS);
        return this;
    }


    public RecipeContentValues putDirections(String value) {
        mContentValues.put(RecipeColumns.DIRECTIONS, value);
        return this;
    }

    public RecipeContentValues putDirectionsNull() {
        mContentValues.putNull(RecipeColumns.DIRECTIONS);
        return this;
    }


    public RecipeContentValues putCategoryid(Long value) {
        mContentValues.put(RecipeColumns.CATEGORYID, value);
        return this;
    }

    public RecipeContentValues putCategoryidNull() {
        mContentValues.putNull(RecipeColumns.CATEGORYID);
        return this;
    }


    public RecipeContentValues putImagekey(String value) {
        mContentValues.put(RecipeColumns.IMAGEKEY, value);
        return this;
    }

    public RecipeContentValues putImagekeyNull() {
        mContentValues.putNull(RecipeColumns.IMAGEKEY);
        return this;
    }


    public RecipeContentValues putImageurl(String value) {
        mContentValues.put(RecipeColumns.IMAGEURL, value);
        return this;
    }

    public RecipeContentValues putImageurlNull() {
        mContentValues.putNull(RecipeColumns.IMAGEURL);
        return this;
    }

}
