package com.example.isakaxel.labb4android.services;

/**
 * Created by alf on 1/4/16.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.isakaxel.labb4android.R;
import com.example.isakaxel.labb4android.activities.ConversationActivity;
import com.example.isakaxel.labb4android.activities.MainActivity;
import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";


    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String action = data.getString("action");

        switch (action){
            case "message":
                // Lägg till meddelandet i rätt chat.
                Intent broadcastMessage = new Intent("new-message-received");
                broadcastMessage.putExtra("id", data.getLong("id"));
                broadcastMessage.putExtra("message", data.getString("message"));
                broadcastMessage.putExtra("to", data.getString("topic"));
                broadcastMessage.putExtra("from", data.getString("email"));
                        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastMessage);
                sendNotification(data.getString("message"));
                break;
            case "invite":
                // Subscribe:a till topic och lägg till chatten i chatt listan.
                break;
            case "loginResult":
                sharedPreferences.edit().putBoolean("serverLoginSuccess",
                        data.getString("result").equals("success")).apply();
                Intent serverLogin = new Intent("serverLoginComplete");
                LocalBroadcastManager.getInstance(this).sendBroadcast(serverLogin);
                break;
            default:
                break;
        }

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.cast_ic_notification_1)
                .setContentTitle("New MessageViewModel")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
