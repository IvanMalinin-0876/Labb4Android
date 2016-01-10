package com.example.isakaxel.labb4android.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.isakaxel.labb4android.Model.JsonParser;
import com.example.isakaxel.labb4android.Model.Message;
import com.example.isakaxel.labb4android.Model.Model;
import com.example.isakaxel.labb4android.Model.Topic;
import com.example.isakaxel.labb4android.Model.Util;
import com.example.isakaxel.labb4android.R;
import com.example.isakaxel.labb4android.Views.MessageViewModel;
import com.example.isakaxel.labb4android.Views.TopicViewModel;
import com.example.isakaxel.labb4android.Views.UserViewModel;
import com.example.isakaxel.labb4android.services.SendGcmService;
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

public class InboxActivity extends AppCompatActivity {
    private ConversationListAdapter conversationAdapter;
    private RecyclerView inboxList;
    private Model model;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        model = Model.prepModel(getIntent().getExtras().getString("userEmail"));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(view.getContext(), NewTopicActivity.class), 1);

            }
        });
        this.setTitle("My inbox");
        inboxList = (RecyclerView) findViewById(R.id.activity_inbox_recyclerView);
        inboxList.setLayoutManager(new LinearLayoutManager(this));

        conversationAdapter = new ConversationListAdapter(this, inboxList, model);
        inboxList.setAdapter(conversationAdapter);

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                new IntentFilter("new-topic"));

        callRest(model.getEmail());
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("onReceive", "hej");
            conversationAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                Intent createTopicIntent = new Intent(this, SendGcmService.class);
                createTopicIntent.putExtra("displayName", data.getStringExtra("displayName"));
                createTopicIntent.putExtra("action", "createTopic");
                createTopicIntent.putExtra("topic", Model.getInstance().getNewTopicName());
                createTopicIntent.putExtra("userEmail", Model.getInstance().getEmail());
                startService(createTopicIntent);
            }
        }
    }

    private void callRest(String email) {
        final String mail = email;
        Log.i("inbox", "starting rest call");
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                StringBuilder result = new StringBuilder();
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL("http://nightloyd.eu:8080/Labb4Server/rest/topic/getAllTopics?email=" + mail);
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
                UserViewModel user = JsonParser.getUserFromJson(result);
                Log.i("Result", result);
                if (user != null) {
                    for (TopicViewModel tvm : user.getTopics()) {
                        ArrayList<Message> messages = new ArrayList<Message>();
                        for (MessageViewModel msg : tvm.getMessages()) {
                            messages.add(new Message(msg.getId(), msg.getFrom(), msg.getTo(), msg.getMsg()));
                        }
                        model.addTopic(new Topic(tvm.getName(), tvm.getDisplayName(), messages, tvm.getMemberNames()));
                    }
                    Log.i("AddTopics", "" + model.getTopics().size());
                    conversationAdapter.notifyDataSetChanged();
                    model.setUserId(user.getId());
                    try {
                        String token = InstanceID.getInstance(InboxActivity.this).getToken("602319958990", GoogleCloudMessaging.INSTANCE_ID_SCOPE
                                , null);
                        GcmPubSub.getInstance(InboxActivity.this).subscribe(token, "/topics/" + user.getId(), null);
                    } catch (IOException e){

                    }
                }
            }
        }.execute(null, null, null);
    }
}
