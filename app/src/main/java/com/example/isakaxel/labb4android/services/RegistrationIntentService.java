package com.example.isakaxel.labb4android.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by alf on 1/4/16.
 */
public class RegistrationIntentService extends IntentService {
    private GoogleCloudMessaging gcm;

    public RegistrationIntentService() {
        super("RegIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken("602319958990", GoogleCloudMessaging.INSTANCE_ID_SCOPE
            , null);

            gcm = GoogleCloudMessaging.getInstance(this);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token, intent.getStringExtra("userEmail"));

            Log.i("RegIntent", token);
            sharedPreferences.edit().putBoolean("sentTokenToServer", true).apply();

        } catch (Exception e) {
            Log.i("RegIntentService", "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean("sentTokenToServer", false).apply();
        }

        Intent registrationComplete = new Intent("registrationComplete");
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    // Modify this method to associate the user's GCM registration token with
    // any server-side account maintained by your application.
    private void sendRegistrationToServer(String token, String userEmail) {
        String topic = "/topics/testtopic";

        try {
            GcmPubSub.getInstance(this).subscribe(token, topic, null);
            Log.i("GcmPubSub", "Succeeded");
        } catch (IOException e) {
            Log.i("GcmPubSub", "unable to subscibe");
            e.printStackTrace();
        }
    }
}
