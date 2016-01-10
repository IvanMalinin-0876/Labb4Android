package com.example.isakaxel.labb4android.activities;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.isakaxel.labb4android.R;

/**
 * Created by loyde on 2016-01-10.
 */
public class MessageListViewHolder extends RecyclerView.ViewHolder {
    public TextView messageFromTextView;
    public TextView messageTextView;


    public MessageListViewHolder(View itemView) {
        super(itemView);
        messageFromTextView = (TextView) itemView.findViewById(R.id.list_item_message_messageFrom);
        messageTextView = (TextView) itemView.findViewById(R.id.list_item_message_message);
    }
}
