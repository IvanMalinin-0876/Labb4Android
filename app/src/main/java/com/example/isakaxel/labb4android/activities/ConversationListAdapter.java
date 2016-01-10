package com.example.isakaxel.labb4android.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.isakaxel.labb4android.Model.JsonParser;
import com.example.isakaxel.labb4android.Model.Model;
import com.example.isakaxel.labb4android.Model.Topic;
import com.example.isakaxel.labb4android.Model.Util;
import com.example.isakaxel.labb4android.R;
import com.example.isakaxel.labb4android.Views.TopicViewModel;

import java.util.HashSet;

/**
 * Created by loyde on 2016-01-10.
 */
public class ConversationListAdapter extends RecyclerView.Adapter<ConversationListViewHolder> {

    private HashSet<Topic> topics;
    private Activity activity;
    private RecyclerView inboxList;
    private Model model;

    public ConversationListAdapter(Activity activity, RecyclerView inboxList, Model model) {
        topics = model.getTopics();
        this.activity = activity;
        this.inboxList = inboxList;
        this.model = model;
    }

    @Override
    public ConversationListViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view = activity.getLayoutInflater().inflate(R.layout.list_item_conversation, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView.ViewHolder holder = inboxList.getChildViewHolder(v);
                Log.i("onClick", "" + holder.getLayoutPosition());

                if (model.getTopic(holder.getLayoutPosition()) != null) {
                    Intent conversationIntent = new Intent(v.getContext(), ConversationActivity.class);
                    conversationIntent.putExtra("topic", holder.getLayoutPosition());
                    activity.startActivity(conversationIntent);
                }
            }
        });
        return new ConversationListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ConversationListViewHolder holder, int position) {
        Topic topic;
        if ((topic = model.getTopic(position)) != null) {
            String topicDisplayName = topic.getDisplayName();
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
