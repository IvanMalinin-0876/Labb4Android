package com.example.isakaxel.labb4android.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by loyde on 2016-01-10.
 */
public class SubscribeToTopicService extends IntentService {

    public SubscribeToTopicService(){
        super("SubscribeToTopicService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("subpub", "1");
        String topicName = intent.getExtras().getString("topicName");
        try {
            String token = InstanceID.getInstance(this).getToken("602319958990", GoogleCloudMessaging.INSTANCE_ID_SCOPE
                    , null);
            GcmPubSub.getInstance(this).subscribe(token, "/topics/" + topicName, null);
            Log.i("inbox", "subscribed to: " + topicName);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
