package com.example.isakaxel.labb4android.activities;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.isakaxel.labb4android.R;

/**
 * Created by loyde on 2016-01-10.
 */
public class ConversationListViewHolder extends RecyclerView.ViewHolder {
    public TextView conversationTextView;

    public ConversationListViewHolder(View itemView) {
        super(itemView);
        conversationTextView = (TextView) itemView.findViewById(R.id.list_item_conversation_name);
    }
}
