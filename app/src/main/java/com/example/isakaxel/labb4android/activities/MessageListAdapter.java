package com.example.isakaxel.labb4android.activities;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.isakaxel.labb4android.Model.Message;
import com.example.isakaxel.labb4android.Model.Topic;
import com.example.isakaxel.labb4android.R;

import java.util.ArrayList;

/**
 * Created by loyde on 2016-01-10.
 */
public class MessageListAdapter extends RecyclerView.Adapter<MessageListViewHolder> {
    private ArrayList<Message> messages;
    private Activity activity;
    private Topic topic;

    public MessageListAdapter(Activity activity, Topic topic) {
        messages = topic.getMessages();
        this.activity = activity;
        this.topic = topic;
    }

    @Override
    public MessageListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = activity.getLayoutInflater().inflate(R.layout.list_item_message, parent, false);
        return new MessageListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageListViewHolder holder, int position) {
        Message message;
        if ((message = topic.getMessage(position)) != null) {
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
