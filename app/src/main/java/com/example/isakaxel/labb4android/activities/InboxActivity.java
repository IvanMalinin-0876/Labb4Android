package com.example.isakaxel.labb4android.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.isakaxel.labb4android.Model.JsonParser;
import com.example.isakaxel.labb4android.Model.Util;
import com.example.isakaxel.labb4android.R;
import com.example.isakaxel.labb4android.Views.TopicViewModel;
import com.example.isakaxel.labb4android.Views.UserViewModel;

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
    private UserViewModel user;
    private ConversationListAdapter conversationAdapter;
    private RecyclerView inboxList;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newMessageIntent = new Intent(view.getContext(), ConversationActivity.class);
                newMessageIntent.putExtra("userEmail", getIntent().getExtras().getString("userEmail"));
                startActivity(newMessageIntent);

            }
        });
        this.setTitle("My inbox");
        inboxList = (RecyclerView) findViewById(R.id.activity_inbox_recyclerView);
        inboxList.setLayoutManager(new LinearLayoutManager(this));

        conversationAdapter = new ConversationListAdapter();
        inboxList.setAdapter(conversationAdapter);

        callRest(getIntent().getStringExtra("userEmail"));
    }

    private class ConversationListAdapter extends RecyclerView.Adapter<ConversationListViewHolder> {

        private HashSet<TopicViewModel> topics;

        public ConversationListAdapter() {
            topics = new HashSet<>();
        }

        public void addConversation(TopicViewModel topic) {
            topics.add(topic);
            notifyDataSetChanged();
        }

        @Override
        public ConversationListViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_conversation, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecyclerView.ViewHolder holder = inboxList.getChildViewHolder(v);
                    Log.i("onClick", "" + holder.getLayoutPosition());

                    Intent conversationIntent = new Intent(v.getContext(), ConversationActivity.class);
                    conversationIntent.putExtra("userEmail", getIntent().getExtras().getString("userEmail"));
                    conversationIntent.putExtra("topic",
                            JsonParser.getTopicJson(Util.getTopic(topics, holder.getLayoutPosition())));
                    startActivity(conversationIntent);

                }
            });
            return new ConversationListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ConversationListViewHolder holder, int position) {
            String topicDisplayName;
            if((topicDisplayName = Util.getTopic(topics, position).getDisplayName()) != null) {
                holder.conversationTextView.setText(topicDisplayName);
                holder.itemView.setTag(topicDisplayName);

                if (position % 2 == 0) {
                    holder.conversationTextView.setBackgroundColor(Color.parseColor("#22000000"));
                } else {
                    holder.conversationTextView.setBackground(null);
                }
            }
        }

        @Override
        public int getItemCount() {
            return topics.size();
        }
    }

    private class ConversationListViewHolder extends RecyclerView.ViewHolder {
        public TextView conversationTextView;

        public ConversationListViewHolder(View itemView) {
            super(itemView);
            conversationTextView = (TextView) itemView.findViewById(R.id.list_item_conversation_name);
        }
    }

    private void callRest(String email) {
        final String mail = email;
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
                user = JsonParser.getUserFromJson(result);
                Log.i("Result", result);
                for (TopicViewModel topicViewModel : user.getTopics()) {
                    Log.i("Topic", topicViewModel.getDisplayName());
                    conversationAdapter.addConversation(topicViewModel);
                }
            }
        }.execute(null, null, null);
    }
}
