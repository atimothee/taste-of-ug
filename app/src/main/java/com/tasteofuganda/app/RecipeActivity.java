package com.tasteofuganda.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.tasteofuganda.sync.SyncAdapter;

/**
 * Created by Timo on 12/29/14.
 */
public class RecipeActivity extends ActionBarActivity implements RecipeFragment.Callback{
    private Boolean mTwoPane;
    public static final String AUTHORITY = "com.tasteofuganda.app.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "com.tasteofuganda.datasync";
    // The account name
    public static final String ACCOUNT = "dummyaccount";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Account myAccount = CreateSyncAccount(RecipeActivity.this);
        //ContentResolver.setSyncAutomatically(myAccount, AUTHORITY, true);


        setContentView(R.layout.activity_browse_recipes);
        if(findViewById(R.id.recipe_detail_container)!=null){
            mTwoPane = true;
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_detail_container, new RecipeDetailFragment())
                        .commit();
            }
        }
        else {
            mTwoPane = false;
        }
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
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putLong("id", id);
            RecipeDetailFragment detailFragment = new RecipeDetailFragment();
            detailFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_detail_container, detailFragment)
                    .commit();

        }else{
            Intent i = new Intent(this, RecipeDetailActivity.class);
            i.putExtra("id", id);
            startActivity(i);
        }

    }
}
