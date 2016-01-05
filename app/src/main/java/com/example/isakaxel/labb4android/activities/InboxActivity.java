package com.example.isakaxel.labb4android.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.isakaxel.labb4android.R;

import java.util.ArrayList;

public class InboxActivity extends AppCompatActivity {
    private TextView displayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        RecyclerView inboxList = (RecyclerView) findViewById(R.id.activity_inbox_recyclerView);
        inboxList.setLayoutManager(new LinearLayoutManager(this));

        ConversationListAdapter conversationAdapter = new ConversationListAdapter();
        inboxList.setAdapter(conversationAdapter);

        for(int i = 0; i < 20; i++) {
            conversationAdapter.addConversation("Conversation " + Integer.toString(i));
        }

    }

    private class ConversationListAdapter extends RecyclerView.Adapter<ConversationListViewHolder> {

        private ArrayList<String> conversations;

        public ConversationListAdapter() {
            conversations = new ArrayList<>();
        }

        public void addConversation(String conversation) {
            conversations.add(conversation);
            notifyDataSetChanged();
        }

        public void removeConversation(String conversation) {
            int pos = conversations.indexOf(conversation);
            if(pos == -1) {
                return;
            }
            conversations.remove(pos);
            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos, conversations.size());
        }

        @Override
        public ConversationListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.list_item_conversation, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = (String) v.getTag();
                    Log.i("onClick", name);
                }
            });
            return new ConversationListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ConversationListViewHolder holder, int position) {
            String conversation = conversations.get(position);
            holder.ConversationTextView.setText(conversation);
            holder.itemView.setTag(conversation);

            if(position % 2 == 0) {
                holder.ConversationTextView.setBackgroundColor(Color.parseColor("#22000000"));
            } else {
                holder.ConversationTextView.setBackground(null);
            }
        }

        @Override
        public int getItemCount() {
            return conversations.size();
        }
    }

    private class ConversationListViewHolder extends RecyclerView.ViewHolder {
        public TextView ConversationTextView;

        public ConversationListViewHolder(View itemView) {
            super(itemView);
            ConversationTextView = (TextView) itemView.findViewById(R.id.list_item_conversation_name);
        }
    }

}
