package com.example.isakaxel.labb4android.Views;

import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by alf on 1/8/16.
 */
public class TopicViewModel {
    private String name, displayName;
    private ArrayList<MessageViewModel> messages;
    private Set<String> memberNames;

    public TopicViewModel(String name, String displayName, ArrayList<MessageViewModel> messages, Set<String> memberNames) {
        this.name = name;
        this.displayName = displayName;
        this.messages = messages;
        this.memberNames = memberNames;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ArrayList<MessageViewModel> getMessages() {
        return messages;
    }

    public Set<String> getMemberNames() {
        return memberNames;
    }
}
