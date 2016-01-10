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
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.isakaxel.labb4android.Model.JsonParser;
import com.example.isakaxel.labb4android.Model.Message;
import com.example.isakaxel.labb4android.Model.Model;
import com.example.isakaxel.labb4android.Model.Topic;
import com.example.isakaxel.labb4android.Model.Util;
import com.example.isakaxel.labb4android.R;
import com.example.isakaxel.labb4android.Views.MessageViewModel;
import com.example.isakaxel.labb4android.Views.TopicViewModel;
import com.example.isakaxel.labb4android.services.RegistrationIntentService;
import com.example.isakaxel.labb4android.services.SendGcmService;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class ConversationActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputMessage;
    private Button sendButton;
    private Topic topic;
    private Model model;
    private GoogleCloudMessaging gcm;
    private RecyclerView messageRecyclerView;
    private MessageListAdapter messageListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int topicPosition = getIntent().getIntExtra("topic", 0);
        model = Model.getInstance();
        topic = model.getTopic(topicPosition);

        this.setTitle(topic.getDisplayName());

        gcm = GoogleCloudMessaging.getInstance(this);

        messageRecyclerView = (RecyclerView) findViewById(R.id.activity_conversation_recyclerView);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageListAdapter = new MessageListAdapter(this, topic);
        messageRecyclerView.setAdapter(messageListAdapter);

        inputMessage = (EditText) findViewById(R.id.activity_conversation_message);
        sendButton = (Button) findViewById(R.id.activity_conversation_sendButton);
        sendButton.setOnClickListener(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                new IntentFilter("new-message-received"));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            messageListAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onClick(View v) {
        if (v == sendButton) {
            String message = inputMessage.getText().toString();
            if (message != null) {
                Log.i("Message to send", message + " from " + getIntent().getExtras().getString("userEmail"));
                // Send message
                Intent intent = new Intent(this, SendGcmService.class);
                intent.putExtra("userEmail", model.getEmail());
                intent.putExtra("message", message);
                intent.putExtra("action", "message");
                intent.putExtra("topic", topic.getName());
                startService(intent);
                inputMessage.setText("");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_to_topic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_to_topic) {
            startActivityForResult(new Intent(this, AddPersonToTopicActivity.class), 2);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2) {
            if(resultCode == Activity.RESULT_OK) {
                Intent addPersonIntent = new Intent(this, SendGcmService.class);
                addPersonIntent.putExtra("toInvite", data.getStringExtra("otherUsersEmail"));
                Log.i("otherUsersEmail", data.getStringExtra("otherUsersEmail"));
                addPersonIntent.putExtra("displayName", topic.getDisplayName());
                addPersonIntent.putExtra("action", "invite");
                addPersonIntent.putExtra("topic", topic.getName());
                addPersonIntent.putExtra("userEmail", Model.getInstance().getEmail());
                startService(addPersonIntent);
            }
        }
    }
}
