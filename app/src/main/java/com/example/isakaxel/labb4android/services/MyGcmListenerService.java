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
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.isakaxel.labb4android.Model.JsonParser;
import com.example.isakaxel.labb4android.Model.Message;
import com.example.isakaxel.labb4android.Model.Model;
import com.example.isakaxel.labb4android.Model.Topic;
import com.example.isakaxel.labb4android.R;
import com.example.isakaxel.labb4android.Views.MessageViewModel;
import com.example.isakaxel.labb4android.Views.TopicViewModel;
import com.example.isakaxel.labb4android.activities.ConversationActivity;
import com.example.isakaxel.labb4android.activities.MainActivity;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

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
        Model model = Model.getInstance();
        Intent broadcastMessage;

        switch (action) {
            case "message":
                // Lägg till meddelandet i rätt chat.
                model.addMessageToTopic(data.getString("topic"), new Message(data.getLong("id"), data.getString("email"), data.getString("to"), data.getString("message")));
                broadcastMessage = new Intent("new-message-received");
                LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastMessage);
                sendNotification(data.getString("message"));
                break;
            case "invite":
                try {
                    String token = InstanceID.getInstance(this).getToken("602319958990", GoogleCloudMessaging.INSTANCE_ID_SCOPE
                            , null);
                    GcmPubSub.getInstance(this).subscribe(token, "/topics/" + data.getString("topicName"), null);
                    downloadTopic(data.getString("topicName"));
                } catch (IOException e){

                }
                break;
            case "loginResult":
                sharedPreferences.edit().putBoolean("serverLoginSuccess",
                        data.getString("result").equals("success")).apply();
                Intent serverLogin = new Intent("serverLoginComplete");
                LocalBroadcastManager.getInstance(this).sendBroadcast(serverLogin);
                break;
            case "createChatResult":
                if (data.getString("result").equals("success")){
                    try {
                        String token = InstanceID.getInstance(this).getToken("602319958990", GoogleCloudMessaging.INSTANCE_ID_SCOPE
                                , null);
                        GcmPubSub.getInstance(this).subscribe(token, "/topics/" + data.getString("topicName"), null);
                        model.addTopic(new Topic(data.getString("name"), data.getString("displayName")));
                        broadcastMessage = new Intent("new-topic");
                        LocalBroadcastManager.getInstance(MyGcmListenerService.this).sendBroadcast(broadcastMessage);
                    } catch (IOException e){

                    }
                }
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

    private void downloadTopic(String topicName) {
        final String name = topicName;
        Log.i("inbox", "starting rest call");
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                StringBuilder result = new StringBuilder();
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL("http://192.168.0.4:8080/Labb4Server/rest/topic/getTopic?topicName=" + name);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    InputStream content = (InputStream) urlConnection.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(content));

                    String line;
                    while ((line = in.readLine()) != null) {
                        result.append(line + "\n");
                    }

                    content.close();
                    in.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }

                return result.toString();
            }

            @Override
            protected void onPostExecute(String result) {
                // extract conversations and add them to the conversation adapter
                TopicViewModel topic = JsonParser.getTopicFromJson(result);
                Log.i("Result", result);
                if (topic != null) {
                    Model model = Model.getInstance();
                    ArrayList<Message> messages = new ArrayList<Message>();
                    for (MessageViewModel mvm : topic.getMessages()) {
                        messages.add(new Message(mvm.getId(), mvm.getFrom(), mvm.getTo(), mvm.getMsg()));
                    }
                    model.addTopic(new Topic(topic.getName(), topic.getDisplayName(), messages, topic.getMemberNames()));
                    Intent broadcastMessage = new Intent("new-topic");
                    LocalBroadcastManager.getInstance(MyGcmListenerService.this).sendBroadcast(broadcastMessage);
                    sendNotification("You have been added to the topic" + topic.getDisplayName());
                }
            }
        }.execute(null, null, null);
    }
}
