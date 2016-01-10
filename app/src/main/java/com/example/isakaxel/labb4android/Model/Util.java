package com.example.isakaxel.labb4android.Model;

import com.example.isakaxel.labb4android.Views.MessageViewModel;
import com.example.isakaxel.labb4android.Views.TopicViewModel;

import java.util.HashSet;

/**
 * Created by alf on 1/9/16.
 */
public class Util {

    public static TopicViewModel getTopic(HashSet<TopicViewModel> topics, int position) {
        int i = 0;
        for (TopicViewModel topic : topics) {
            if(i++ == position) {
                return topic;
            }
        }
        return null;
    }

    public static MessageViewModel getMessage(HashSet<MessageViewModel> messages, int position) {
        int i = 0;
        for (MessageViewModel message : messages) {
            if(i++ == position) {
                return message;
            }
        }
        return null;
    }
}
