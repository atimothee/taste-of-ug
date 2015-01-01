package com.tasteofuganda.app.provider.recipe;

import java.util.Date;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.tasteofuganda.app.provider.base.AbstractSelection;

/**
 * Selection for the {@code recipe} table.
 */
public class RecipeSelection extends AbstractSelection<RecipeSelection> {
    @Override
    public Uri uri() {
        return RecipeColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort
     *            order, which may be unordered.
     * @return A {@code RecipeCursor} object, which is positioned before the first entry, or null.
     */
    public RecipeCursor query(ContentResolver contentResolver, String[] projection, String sortOrder) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) return null;
        return new RecipeCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null}.
     */
    public RecipeCursor query(ContentResolver contentResolver, String[] projection) {
        return query(contentResolver, projection, null);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null, null}.
     */
    public RecipeCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null, null);
    }


    public RecipeSelection id(long... value) {
        addEquals(RecipeColumns._ID, toObjectArray(value));
        return this;
    }


    public RecipeSelection recipeName(String... value) {
        addEquals(RecipeColumns.RECIPE_NAME, value);
        return this;
    }

    public RecipeSelection recipeNameNot(String... value) {
        addNotEquals(RecipeColumns.RECIPE_NAME, value);
        return this;
    }

    public RecipeSelection recipeNameLike(String... value) {
        addLike(RecipeColumns.RECIPE_NAME, value);
        return this;
    }

    public RecipeSelection description(String... value) {
        addEquals(RecipeColumns.DESCRIPTION, value);
        return this;
    }

    public RecipeSelection descriptionNot(String... value) {
        addNotEquals(RecipeColumns.DESCRIPTION, value);
        return this;
    }

    public RecipeSelection descriptionLike(String... value) {
        addLike(RecipeColumns.DESCRIPTION, value);
        return this;
    }

    public RecipeSelection ingredients(String... value) {
        addEquals(RecipeColumns.INGREDIENTS, value);
        return this;
    }

    public RecipeSelection ingredientsNot(String... value) {
        addNotEquals(RecipeColumns.INGREDIENTS, value);
        return this;
    }

    public RecipeSelection ingredientsLike(String... value) {
        addLike(RecipeColumns.INGREDIENTS, value);
        return this;
    }

    public RecipeSelection directions(String... value) {
        addEquals(RecipeColumns.DIRECTIONS, value);
        return this;
    }

    public RecipeSelection directionsNot(String... value) {
        addNotEquals(RecipeColumns.DIRECTIONS, value);
        return this;
    }

    public RecipeSelection directionsLike(String... value) {
        addLike(RecipeColumns.DIRECTIONS, value);
        return this;
    }

    public RecipeSelection categoryid(Long... value) {
        addEquals(RecipeColumns.CATEGORYID, value);
        return this;
    }

    public RecipeSelection categoryidNot(Long... value) {
        addNotEquals(RecipeColumns.CATEGORYID, value);
        return this;
    }

    public RecipeSelection categoryidGt(long value) {
        addGreaterThan(RecipeColumns.CATEGORYID, value);
        return this;
    }

    public RecipeSelection categoryidGtEq(long value) {
        addGreaterThanOrEquals(RecipeColumns.CATEGORYID, value);
        return this;
    }

    public RecipeSelection categoryidLt(long value) {
        addLessThan(RecipeColumns.CATEGORYID, value);
        return this;
    }

    public RecipeSelection categoryidLtEq(long value) {
        addLessThanOrEquals(RecipeColumns.CATEGORYID, value);
        return this;
    }

    public RecipeSelection imagekey(String... value) {
        addEquals(RecipeColumns.IMAGEKEY, value);
        return this;
    }

    public RecipeSelection imagekeyNot(String... value) {
        addNotEquals(RecipeColumns.IMAGEKEY, value);
        return this;
    }

    public RecipeSelection imagekeyLike(String... value) {
        addLike(RecipeColumns.IMAGEKEY, value);
        return this;
    }

    public RecipeSelection imageurl(String... value) {
        addEquals(RecipeColumns.IMAGEURL, value);
        return this;
    }

    public RecipeSelection imageurlNot(String... value) {
        addNotEquals(RecipeColumns.IMAGEURL, value);
        return this;
    }

    public RecipeSelection imageurlLike(String... value) {
        addLike(RecipeColumns.IMAGEURL, value);
        return this;
    }
}
