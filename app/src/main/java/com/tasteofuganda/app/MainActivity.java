package com.tasteofuganda.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.tasteofuganda.app.provider.TasteOfUgProvider;
import com.tasteofuganda.backend.recipeApi.model.Recipe;
import com.tasteofuganda.backend.registration.Registration;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainActivity extends ActionBarActivity {

    // Constants
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static final String TAG = MainActivity.class.getSimpleName();
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String SENDER_ID = "10592780844";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "com.tasteofuganda.datasync";
    // The account name
    public static final String ACCOUNT = "dummyaccount";
    // Instance fields
    Account mAccount;

    private GoogleCloudMessaging gcm;
    private Context context;
    private String regid;
    private static Registration regService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create the dummy account
        mAccount = CreateSyncAccount(this);
        ContentResolver.setSyncAutomatically(mAccount, TasteOfUgProvider.AUTHORITY, true);
        setContentView(R.layout.activity_main);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setIcon(R.drawable.ic_launcher);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
        context = getApplicationContext();

        //new GcmRegistrationAsyncTask(this).execute();
        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                Log.d(TAG, "Registering in background...");
                registerInBackground();
                Bundle bundle = new Bundle();
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                bundle.putString("message", "sync-all");
                ContentResolver.requestSync(mAccount, TasteOfUgProvider.AUTHORITY, bundle);
            }
            else {
                Log.d(TAG, "Device already registered with no "+regid);
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
        Button btn = (Button)findViewById(R.id.clickme);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("com.tasteofuganda.app.RecipeActivity");
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
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
        Log.i(TAG, "Saving regId "+regId+" on app version " + appVersion);
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

    private void sendRegistrationIdToBackend(){
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

    private void registerInBackground() {
        new AsyncTask() {

            @Override
            protected String doInBackground(Object[] params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            protected void onPostExecute(String msg) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
            }

        }.execute(null, null, null);

    }


}

//class GcmRegistrationAsyncTask extends AsyncTask<Context, Void, String> {
//    private static Registration regService = null;
//    private GoogleCloudMessaging gcm;
//    private Context context;
//
//    private static final String SENDER_ID = "10592780844";
//
//    public GcmRegistrationAsyncTask(Context context) {
//        this.context = context;
//    }
//
//    @Override
//    protected String doInBackground(Context... params) {
//        if (regService == null) {
//            Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
//                    new AndroidJsonFactory(), null)
//                    .setRootUrl("https://tasteofuganda.appspot.com/_ah/api/");
//            // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
//                    // otherwise they can be skipped
//                    //.setRootUrl("http://10.0.2.2:8080/_ah/api/")
////                    .setRootUrl("http://10.0.3.2:8080/_ah/api/")//for genymotion
////                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
////                        @Override
////                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
////                                throws IOException {
////                            abstractGoogleClientRequest.setDisableGZipContent(true);
//
////                        }
////                    });
//            // end of optional local run code
//
//            regService = builder.build();
//        }
//
//        //RecipeApi.Builder builder = new RecipeApi.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
//
//        String msg = "";
//        try {
//            if (gcm == null) {
//                gcm = GoogleCloudMessaging.getInstance(context);
//            }
//            String regId = gcm.register(SENDER_ID);
//            msg = "Device registered, registration ID=" + regId;
//
//            regService.register(regId).execute();
//
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            msg = "Error: " + ex.getMessage();
//        }
//        return msg;
//    }
//
//
//    @Override
//    protected void onPostExecute(String msg) {
//        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
//        Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
//    }
//
//}
