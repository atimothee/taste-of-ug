package com.tasteofuganda.app.provider;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.tasteofuganda.app.BuildConfig;
import com.tasteofuganda.app.provider.category.CategoryColumns;
import com.tasteofuganda.app.provider.recipe.RecipeColumns;

public class TasteOfUgSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = TasteOfUgSQLiteOpenHelper.class.getSimpleName();

    public static final String DATABASE_FILE_NAME = "tasteofug.db";
    private static final int DATABASE_VERSION = 1;
    private static TasteOfUgSQLiteOpenHelper sInstance;
    private final Context mContext;
    private final TasteOfUgSQLiteOpenHelperCallbacks mOpenHelperCallbacks;

    // @formatter:off
    private static final String SQL_CREATE_TABLE_CATEGORY = "CREATE TABLE IF NOT EXISTS "
            + CategoryColumns.TABLE_NAME + " ( "
            + CategoryColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CategoryColumns.NAME + " TEXT, "
            + CategoryColumns.COLOR + " TEXT "
            + " );";

    private static final String SQL_CREATE_TABLE_RECIPE = "CREATE TABLE IF NOT EXISTS "
            + RecipeColumns.TABLE_NAME + " ( "
            + RecipeColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + RecipeColumns.RECIPE_NAME + " TEXT, "
            + RecipeColumns.DESCRIPTION + " TEXT, "
            + RecipeColumns.INGREDIENTS + " TEXT, "
            + RecipeColumns.DIRECTIONS + " TEXT, "
            + RecipeColumns.CATEGORYID + " INTEGER, "
            + RecipeColumns.IMAGEKEY + " TEXT, "
            + RecipeColumns.IMAGEURL + " TEXT "
            + ", CONSTRAINT fk_categoryid FOREIGN KEY (categoryid) REFERENCES category (_id) ON DELETE CASCADE"
            + " );";

    // @formatter:on

    public static TasteOfUgSQLiteOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static TasteOfUgSQLiteOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */

    private static TasteOfUgSQLiteOpenHelper newInstancePreHoneycomb(Context context) {
        return new TasteOfUgSQLiteOpenHelper(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
    }

    private TasteOfUgSQLiteOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
        mOpenHelperCallbacks = new TasteOfUgSQLiteOpenHelperCallbacks();
    }


    /*
     * Post Honeycomb.
     */

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static TasteOfUgSQLiteOpenHelper newInstancePostHoneycomb(Context context) {
        return new TasteOfUgSQLiteOpenHelper(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private TasteOfUgSQLiteOpenHelper(Context context, String name, CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new TasteOfUgSQLiteOpenHelperCallbacks();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_CATEGORY);
        db.execSQL(SQL_CREATE_TABLE_RECIPE);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledPreJellyBean(db);
        } else {
            setForeignKeyConstraintsEnabledPostJellyBean(db);
        }
    }

    private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}
