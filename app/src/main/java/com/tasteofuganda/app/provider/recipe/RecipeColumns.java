package com.tasteofuganda.app.provider.recipe;

import java.util.HashSet;
import java.util.Set;

import android.net.Uri;
import android.provider.BaseColumns;

import com.tasteofuganda.app.provider.TasteOfUgProvider;

/**
 * Columns for the {@code recipe} table.
 */
public class RecipeColumns implements BaseColumns {
    public static final String TABLE_NAME = "recipe";
    public static final Uri CONTENT_URI = Uri.parse(TasteOfUgProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    public static final String _ID = BaseColumns._ID;
    public static final String RECIPE_NAME = "recipe_name";
    public static final String DESCRIPTION = "description";
    public static final String INGREDIENTS = "ingredients";
    public static final String DIRECTIONS = "directions";
    public static final String CATEGORYID = "categoryid";
    public static final String IMAGEKEY = "imagekey";

    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] FULL_PROJECTION = new String[] {
            TABLE_NAME + "." + _ID + " AS " + BaseColumns._ID,
            TABLE_NAME + "." + RECIPE_NAME,
            TABLE_NAME + "." + DESCRIPTION,
            TABLE_NAME + "." + INGREDIENTS,
            TABLE_NAME + "." + DIRECTIONS,
            TABLE_NAME + "." + CATEGORYID,
            TABLE_NAME + "." + IMAGEKEY
    };
    // @formatter:on

    private static final Set<String> ALL_COLUMNS = new HashSet<String>();
    static {
        ALL_COLUMNS.add(_ID);
        ALL_COLUMNS.add(RECIPE_NAME);
        ALL_COLUMNS.add(DESCRIPTION);
        ALL_COLUMNS.add(INGREDIENTS);
        ALL_COLUMNS.add(DIRECTIONS);
        ALL_COLUMNS.add(CATEGORYID);
        ALL_COLUMNS.add(IMAGEKEY);
    }

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (ALL_COLUMNS.contains(c)) return true;
        }
        return false;
    }
}
