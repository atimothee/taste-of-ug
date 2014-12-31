package com.tasteofuganda.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import com.tasteofuganda.app.provider.category.CategoryColumns;

/**
 * Created by Timo on 12/29/14.
 */
public class RecipeActivity extends ActionBarActivity implements RecipeFragment.Callback, LoaderManager.LoaderCallbacks {
    private Boolean mTwoPane;
    private static final String TAG = RecipeActivity.class.getSimpleName();
    public static final int CATEGORY_LOADER = 3;
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "com.tasteofuganda.datasync";
    // The account name
    public static final String ACCOUNT = "dummyaccount";
    private Cursor categoryCursor;
    private SimpleCursorAdapter mSpinnerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Account myAccount = CreateSyncAccount(RecipeActivity.this);
        //ContentResolver.setSyncAutomatically(myAccount, AUTHORITY, true);
        getSupportLoaderManager().initLoader(CATEGORY_LOADER, null, RecipeActivity.this);

        setContentView(R.layout.activity_browse_recipes);
        if (findViewById(R.id.recipe_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_detail_container, new RecipeDetailFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
        mSpinnerAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_dropdown_item, categoryCursor, new String[]{CategoryColumns.NAME}, new int[]{android.R.id.text1}, 0);

        View spinnerContainer = LayoutInflater.from(RecipeActivity.this)
                .inflate(R.layout.actionbar_spinner, null);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        Spinner spinner = (Spinner) spinnerContainer.findViewById(R.id.actionbar_spinner);
        spinner.setAdapter(mSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long itemId) {
                RecipeFragment frag = (RecipeFragment) getSupportFragmentManager().findFragmentById(
                        R.id.fragment_recipe);
                if(categoryCursor!=null) {
                    categoryCursor.moveToPosition(position);

                    Bundle args = new Bundle();
                    args.putLong("category_id", categoryCursor.getLong(0));
                    frag.reloadRecipeFragmentFromArgs(args);
                }else {
                    frag.reloadRecipeFragmentFromArgs(null);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Log.d(TAG, "spinner adapter count " + mSpinnerAdapter.getCount());
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(spinnerContainer, lp);

    }

    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }


    @Override
    public void onItemSelected(Long id) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putLong("id", id);
            RecipeDetailFragment detailFragment = new RecipeDetailFragment();
            detailFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_detail_container, detailFragment)
                    .commit();

        } else {
            Intent i = new Intent(this, RecipeDetailActivity.class);
            i.putExtra("id", id);
            startActivity(i);
        }

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(RecipeActivity.this, CategoryColumns.CONTENT_URI, CategoryColumns.FULL_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        categoryCursor = (Cursor) data;
        mSpinnerAdapter.swapCursor(categoryCursor);
        Log.d(TAG, "Cursor has been swapped");
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}


