package com.example.isakaxel.labb4android.Model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by loyde on 2016-01-10.
 */
public class Topic {
    private String name, displayName;
    private ArrayList<Message> messages;
    private Set<String> memberNames;

    public Topic(String name, String displayName, ArrayList<Message> messages, Set<String> memberNames){
        this.name = name;
        this.displayName = displayName;
        this.memberNames = memberNames;
        this.messages = messages;
    }

    public Topic(String name, String displayName){
        this.name = name;
        this.displayName = displayName;
        memberNames = new HashSet<>();
        messages = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message msg) {
        messages.add(msg);
    }

    public Set<String> getMemberNames() {
        return memberNames;
    }

    public void addMember(String name) {
        memberNames.add(name);
    }

    public Message getMessage(int position){
        return messages.get(position);
    }
}
