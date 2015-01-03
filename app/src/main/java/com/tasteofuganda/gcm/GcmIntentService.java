package com.tasteofuganda.gcm;

import android.accounts.Account;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tasteofuganda.app.R;
import com.tasteofuganda.app.RecipeActivity;
import com.tasteofuganda.app.provider.TasteOfUgProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Timo on 12/22/14.
 */
public class GcmIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private final String TYPE_KEY = "type";
    private final String TICKLE_VALUE = "tickle";
    private final String NOTIFICATION_VALUE = "notification";
    private static final String ID_KEY = "id";
    private static final String MESSAGE_KEY = "message";
    private static final String TITLE_KEY = "title";
    private static final String SELECTED_ID_KEY = "selected_id";
    public static final String ACCOUNT_TYPE = "com.tasteofuganda.datasync";
    public static final String ACCOUNT = "dummyaccount";
    private Account account = new Account(ACCOUNT, ACCOUNT_TYPE);
    private static final String TAG = GcmIntentService.class.getSimpleName();

    private NotificationManager mNotificationManager;


    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {

            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());

                Log.d(TAG, "Request sync called via GCM");
                if(extras.containsKey(TYPE_KEY) && extras.getString(TYPE_KEY).equalsIgnoreCase(NOTIFICATION_VALUE)){
                    extras.putLong(ID_KEY, Long.valueOf(extras.getString(ID_KEY)));
                    sendNotification(extras);
                    Log.d(TAG, "send notification called");

                }else if(extras.containsKey(TYPE_KEY) && extras.getString(TYPE_KEY).equalsIgnoreCase(TICKLE_VALUE)){
                    extras.putLong(ID_KEY, Long.valueOf(extras.getString(ID_KEY)));
                    ContentResolver.requestSync(account, TasteOfUgProvider.AUTHORITY, extras);
                }
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(Bundle extras) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(getApplicationContext(), RecipeActivity.class);
        resultIntent.putExtra(SELECTED_ID_KEY, extras.getLong(ID_KEY));
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(extras.getString(TITLE_KEY))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(extras.getString(MESSAGE_KEY)))
                        .setAutoCancel(true)
                        .setSound(uri)
                        .setContentText(extras.getString(MESSAGE_KEY));

        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    protected void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
