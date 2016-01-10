package com.example.isakaxel.labb4android.Views;

import java.util.Comparator;

/**
 * Created by alf on 1/10/16.
 */
public class MessageViewModelComparator implements Comparator<MessageViewModel> {
    @Override
    public int compare(MessageViewModel o1, MessageViewModel o2) {
        return (int) o1.getId() - (int) o2.getId();
    }
}
