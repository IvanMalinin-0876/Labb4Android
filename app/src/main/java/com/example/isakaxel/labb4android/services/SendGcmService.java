package com.example.isakaxel.labb4android.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.example.isakaxel.labb4android.activities.InboxActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by alf on 1/8/16.
 */
public class SendGcmService extends IntentService {
    private GoogleCloudMessaging gcm;

    public SendGcmService() {
        super("SendGcmService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        gcm = GoogleCloudMessaging.getInstance(this);
        sendGcm(intent);
    }

    private void sendGcm(Intent intent) {
        final String act = intent.getStringExtra("action");
        final String tpc = intent.getStringExtra("topic");
        final String message = intent.getStringExtra("message");
        final String mail = intent.getStringExtra("userEmail");
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    Bundle data = new Bundle();
                    Log.i("sendGcm", message);
                    data.putString("message", message);
                    data.putString("action", act);
                    data.putString("topic", tpc);
                    data.putString("email", mail);
                    String id = Integer.toString(4);
                    gcm.send("602319958990" + "@gcm.googleapis.com", id, data);
                    msg = "Sent message";
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }
            @Override
            protected void onPostExecute(String msg) {
                Log.i("onPostExecute", msg);
            }
        }.execute(null, null, null);
    }
}
