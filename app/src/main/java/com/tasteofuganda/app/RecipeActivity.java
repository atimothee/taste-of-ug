package com.tasteofuganda.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.tasteofuganda.app.provider.TasteOfUgProvider;
import com.tasteofuganda.app.provider.category.CategoryColumns;
import com.tasteofuganda.app.provider.recipe.RecipeColumns;
import com.tasteofuganda.backend.categoryApi.CategoryApi;
import com.tasteofuganda.backend.registration.Registration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Timo on 12/29/14.
 */
public class RecipeActivity extends ActionBarActivity implements RecipeFragment.Callback, LoaderManager.LoaderCallbacks {
    private Boolean mTwoPane;
    private static final String TAG = RecipeActivity.class.getSimpleName();
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String SENDER_ID = "10592780844";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final int CATEGORY_LOADER = 3;
    private static final String DETAIL_ID_KEY = "id";
    private static final String INTENT_SAVED_STATE_RECIPE_SELECTED_ID_KEY = "selected_id";//for when fragment is called from notification
    private static final String ARGS_CATEGORY_ID_KEY = "category_id";
    private static final String SAVED_STATE_SPINNER_SELECTION_KEY = "selection";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "com.tasteofuganda.datasync";
    // The account name
    public static final String ACCOUNT = "dummyaccount";
    private Cursor categoryCursor;
    private SimpleCursorAdapter mSpinnerAdapter;
    private int mSpinnerSelectedPosition;
    private Long mSelectedId;//id of recipe to select -- if activity is opened from notification
    private Spinner mSpinner;
    private GoogleCloudMessaging gcm;
    private Context context;
    private String regid;
    private static Registration regService = null;
    private Account mAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_recipes);
        context = RecipeActivity.this;
        initializeSyncAdapter();
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                Log.d(TAG, "Registering in background...");
                syncImmediately();
                new RegisterAsyncTask().execute();
            } else {
                Log.d(TAG, "Device already registered with no " + regid);
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

        getSupportLoaderManager().initLoader(CATEGORY_LOADER, null, RecipeActivity.this);


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

        mSpinner = (Spinner) spinnerContainer.findViewById(R.id.actionbar_spinner);
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long itemId) {
                Log.d(TAG, "on spinner item selected has been called");
                mSpinnerSelectedPosition = position;
                RecipeFragment frag = (RecipeFragment) getSupportFragmentManager().findFragmentById(
                        R.id.fragment_recipe);
                if (categoryCursor != null) {
                    categoryCursor.moveToPosition(position);
                    Bundle args = new Bundle();
                    args.putLong(ARGS_CATEGORY_ID_KEY, categoryCursor.getLong(categoryCursor.getColumnIndex(CategoryColumns._ID)));
                    if(mSelectedId!=null && mSelectedId!=0){
                        args.putLong(INTENT_SAVED_STATE_RECIPE_SELECTED_ID_KEY, mSelectedId);
                        Log.d(TAG, INTENT_SAVED_STATE_RECIPE_SELECTED_ID_KEY+" "+ mSelectedId+" has been added to arguments");
                    }
                    frag.reloadRecipeFragmentFromArgs(args);
                } else {
                    frag.reloadRecipeFragmentFromArgs(null);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        Log.d(TAG, "mSpinner adapter count " + mSpinnerAdapter.getCount());
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(spinnerContainer, lp);

        if (getIntent().hasExtra(INTENT_SAVED_STATE_RECIPE_SELECTED_ID_KEY)) {
            Log.d(TAG, "intent has extra "+INTENT_SAVED_STATE_RECIPE_SELECTED_ID_KEY+", value is "+getIntent().getLongExtra(INTENT_SAVED_STATE_RECIPE_SELECTED_ID_KEY, 0));
            mSelectedId = getIntent().getLongExtra(INTENT_SAVED_STATE_RECIPE_SELECTED_ID_KEY, 0);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_STATE_SPINNER_SELECTION_KEY)) {
            mSpinnerSelectedPosition = savedInstanceState.getInt(SAVED_STATE_SPINNER_SELECTION_KEY);
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(INTENT_SAVED_STATE_RECIPE_SELECTED_ID_KEY)) {
            mSelectedId = savedInstanceState.getLong(INTENT_SAVED_STATE_RECIPE_SELECTED_ID_KEY);
        }

    }

    private void syncImmediately() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putString("message", "sync-all");
        ContentResolver.requestSync(mAccount, TasteOfUgProvider.AUTHORITY, bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
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

    private void initializeSyncAdapter() {
        mAccount = CreateSyncAccount(RecipeActivity.this);
        ContentResolver.setSyncAutomatically(mAccount, TasteOfUgProvider.AUTHORITY, true);
    }


    @Override
    public void onItemSelected(Long id) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putLong(DETAIL_ID_KEY, id);
            if (mSelectedId != null && mSelectedId != 0) {
                args.putLong(DETAIL_ID_KEY, mSelectedId);
            }
            RecipeDetailFragment detailFragment = new RecipeDetailFragment();
            detailFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_detail_container, detailFragment)
                    .commit();

        } else {
            Intent i = new Intent(this, RecipeDetailActivity.class);
            i.putExtra(DETAIL_ID_KEY, id);
            if (mSelectedId != null && mSelectedId != 0) {
                i.putExtra(DETAIL_ID_KEY, mSelectedId);
            }
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
        mSpinner.setSelection(mSpinnerSelectedPosition);
        if (mSelectedId!=null) {
            mSpinner.setSelection(0); //set spinner to show all
        }
        Log.d(TAG, "Cursor has been swapped");
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager)


                getSystemService


                        (Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(


                getComponentName()


        ));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mSpinnerSelectedPosition != Spinner.INVALID_POSITION) {
            outState.putInt(SAVED_STATE_SPINNER_SELECTION_KEY, mSpinnerSelectedPosition);
        }
        if (getIntent().hasExtra(INTENT_SAVED_STATE_RECIPE_SELECTED_ID_KEY)) {
            outState.putLong(INTENT_SAVED_STATE_RECIPE_SELECTED_ID_KEY, getIntent().getLongExtra(INTENT_SAVED_STATE_RECIPE_SELECTED_ID_KEY, 0));
        }
        super.onSaveInstanceState(outState);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return registrationId;
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {

            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId " + regId + " on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void sendRegistrationIdToBackend() {
        if (regService == null) {
            Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setRootUrl("https://tasteofuganda.appspot.com/_ah/api/");

            /*Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
            otherwise they can be skipped*/
            /*.setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setRootUrl("http://10.0.3.2:8080/_ah/api/")//for genymotion
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                                throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);

                        }
                    });*/
            /*end of optional local run code*/

            regService = builder.build();
        }
        try {
            regService.register(regid).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class RegisterAsyncTask extends AsyncTask<Context, Void, String> {

        @Override
        protected String doInBackground(Context... params) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regid = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID=" + regid;

                // Send the registration ID to server over HTTP
                sendRegistrationIdToBackend();

                // Persist the regID - no need to register again.
                storeRegistrationId(context, regid);

            } catch (IOException ex) {
                ex.printStackTrace();
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            //Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
        }
    }
}



