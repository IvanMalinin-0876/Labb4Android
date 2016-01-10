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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.isakaxel.labb4android.Model.JsonParser;
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
    private TopicViewModel topic;
    private GoogleCloudMessaging gcm;
    private RecyclerView messageRecyclerView;
    private MessageListAdapter messageListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        topic = JsonParser.getTopicFromJson(getIntent().getStringExtra("topic"));

        this.setTitle(topic.getDisplayName());

        gcm = GoogleCloudMessaging.getInstance(this);

        messageRecyclerView = (RecyclerView) findViewById(R.id.activity_conversation_recyclerView);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageListAdapter = new MessageListAdapter();
        messageRecyclerView.setAdapter(messageListAdapter);

        for (MessageViewModel messageViewModel : topic.getMessages()) {
            messageListAdapter.addMessage(messageViewModel);
        }


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
            if(intent.getStringExtra("to").equals(topic.getName())) {
                messageListAdapter.addMessage(new MessageViewModel(intent.getLongExtra("id", 0), intent.getStringExtra("from"),
                        intent.getStringExtra("to"), intent.getStringExtra("message")));
            }
        }
    };

    private class MessageListAdapter extends RecyclerView.Adapter<MessageListViewHolder> {
        private HashMap<Long, MessageViewModel> messages;

        public MessageListAdapter() {
            messages = new HashMap<>();
        }

        public void addMessage(MessageViewModel message) {
            messages.put((long)messages.size(), message);
            notifyDataSetChanged();
        }

        @Override
        public MessageListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_message, parent, false);
            return new MessageListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MessageListViewHolder holder, int position) {
            MessageViewModel message;
            if((message = messages.get((long)position)) != null) {
                holder.messageFromTextView.setText(message.getFrom() + " said:");
                holder.messageTextView.setText(message.getMsg());

                if (position % 2 == 0) {
                    holder.messageFromTextView.setBackgroundColor(Color.parseColor("#22000000"));
                    holder.messageTextView.setBackgroundColor(Color.parseColor("#22000000"));
                } else {
                    holder.messageFromTextView.setBackground(null);
                    holder.messageTextView.setBackground(null);
                }
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }
    }

    private class MessageListViewHolder extends RecyclerView.ViewHolder {
        public TextView messageFromTextView;
        public TextView messageTextView;


        public MessageListViewHolder(View itemView) {
            super(itemView);
            messageFromTextView = (TextView) itemView.findViewById(R.id.list_item_message_messageFrom);
            messageTextView = (TextView) itemView.findViewById(R.id.list_item_message_message);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == sendButton) {
            String message = inputMessage.getText().toString();
            if(message != null) {
                Log.i("Message to send", message + " from " + getIntent().getExtras().getString("userEmail"));
                // Send message
                Intent intent = new Intent(this, SendGcmService.class);
                intent.putExtra("userEmail", getIntent().getExtras().getString("userEmail"));
                intent.putExtra("message", message);
                intent.putExtra("action", "message");
                intent.putExtra("topic", topic.getName());
                startService(intent);
                inputMessage.setText("");
            }
        }
    }


}
