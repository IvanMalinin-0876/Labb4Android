package com.example.isakaxel.labb4android.Views;

import java.util.HashSet;

/**
 * Created by alf on 1/9/16.
 */
public class UserViewModel {
    private String email;
    private HashSet<TopicViewModel> topics;

    public UserViewModel(String email, HashSet<TopicViewModel> topics){
        this.email = email;
        this.topics = topics;
    }

    public String getEmail() {
        return email;
    }

    public HashSet<TopicViewModel> getTopics() {
        return topics;
    }
}
