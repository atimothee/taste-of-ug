package com.tasteofuganda.sync;

/**
 * Created by Timo on 12/22/14.
 */

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.tasteofuganda.app.provider.TasteOfUgProvider;
import com.tasteofuganda.app.provider.category.CategoryColumns;
import com.tasteofuganda.app.provider.recipe.RecipeColumns;
import com.tasteofuganda.backend.categoryApi.CategoryApi;
import com.tasteofuganda.backend.categoryApi.model.Category;
import com.tasteofuganda.backend.recipeApi.RecipeApi;
import com.tasteofuganda.backend.recipeApi.model.Recipe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    private  final String TAG = SyncAdapter.class.getSimpleName();

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();

    }

    /*
     * Specify the code you want to run in the sync adapter. The entire
     * sync adapter runs in a background thread, so you don't have to set
     * up your own background processing.
     */
    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {
        Log.d(TAG, "Perform sync called..."+extras.toString());

        CategoryApi.Builder cBuilder = new CategoryApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
        CategoryApi categoryApi = cBuilder.build();
        RecipeApi.Builder builder = new RecipeApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
        RecipeApi recipeApi = builder.build();
        try{

        if(extras.getString("resource_type")=="category"){
            try {
                Category c = categoryApi.get(extras.getLong("id")).execute();
                ContentValues categoryContentValues = new ContentValues();
                categoryContentValues.put(CategoryColumns._ID, c.getId());
                categoryContentValues.put(CategoryColumns.NAME, c.getName());
                categoryContentValues.put(CategoryColumns.COLOR, c.getColor());
                provider.insert(CategoryColumns.CONTENT_URI, categoryContentValues);
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if(extras.getString("resource_type")=="recipe"){
            try {
                Recipe r = recipeApi.get(extras.getLong("id")).execute();
                ContentValues contentValues = new ContentValues();
                contentValues.put(RecipeColumns._ID, r.getId());
                contentValues.put(RecipeColumns.RECIPE_NAME, r.getName());
                contentValues.put(RecipeColumns.CATEGORYID, r.getCategoryId());
                contentValues.put(RecipeColumns.DESCRIPTION, r.getDescription());
                contentValues.put(RecipeColumns.DIRECTIONS, r.getDirections().getValue());
                contentValues.put(RecipeColumns.IMAGEKEY, r.getImage().getKeyString());
                provider.insert(RecipeColumns.CONTENT_URI, contentValues);
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if(extras.getString("message").equals("sync-all")) {
            Log.d(TAG, "Sync all is called...");

            List<ContentValues> categoryCValuesList = new ArrayList<ContentValues>();
            ContentValues categoryContentValues = null;

            try {
                List<Category> categories = categoryApi.list().execute().getItems();
                for (Category c : categories) {
                    categoryContentValues = new ContentValues();
                    categoryContentValues.put(CategoryColumns._ID, c.getId());
                    categoryContentValues.put(CategoryColumns.NAME, c.getName());
                    categoryContentValues.put(CategoryColumns.COLOR, c.getColor());
                    categoryCValuesList.add(categoryContentValues);
                }
                provider.bulkInsert(CategoryColumns.CONTENT_URI, categoryCValuesList.toArray(new ContentValues[categoryCValuesList.size()]));
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<ContentValues> contentValuesList = new ArrayList<ContentValues>();
            ContentValues contentValues = null;
            try {
                List<Recipe> recipes = recipeApi.list().execute().getItems();

                for (Recipe r : recipes) {
                    contentValues = new ContentValues();
                    contentValues.put(RecipeColumns._ID, r.getId());
                    contentValues.put(RecipeColumns.RECIPE_NAME, r.getName());
                    contentValues.put(RecipeColumns.CATEGORYID, r.getCategoryId());
                    contentValues.put(RecipeColumns.DESCRIPTION, r.getDescription());
                    contentValues.put(RecipeColumns.DIRECTIONS, r.getDirections().getValue());
                    contentValues.put(RecipeColumns.IMAGEKEY, r.getImage().getKeyString());
                    contentValuesList.add(contentValues);
                    Log.d(TAG, "Recipe " + r.getName() + " downloaded");
                }

                provider.bulkInsert(RecipeColumns.CONTENT_URI, contentValuesList.toArray(new ContentValues[contentValuesList.size()]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        }catch(Exception e){
            e.printStackTrace();
        }






    }

}

