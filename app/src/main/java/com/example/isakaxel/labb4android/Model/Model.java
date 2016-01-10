package com.example.isakaxel.labb4android.Model;

import android.util.Log;

import com.example.isakaxel.labb4android.Views.TopicViewModel;

import java.util.HashSet;

/**
 * Created by loyde on 2016-01-10.
 */
public class Model {
    private static Model instance;
    private volatile HashSet<Topic> topics;
    private volatile String email;
    private long userId;

    private Model(String email){
        this.email = email;
        topics = new HashSet<>();
    }

    public static Model getInstance(){
        return instance;
    }

    public static Model prepModel(String email) {
        synchronized (Model.class){
            if (instance == null){
                instance = new Model(email);
            }
        }
        return instance;
    }

    public HashSet<Topic> getTopics(){
        return topics;
    }

    public String getEmail(){
        return email;
    }

    public void addTopic(Topic topic){
        topics.add(topic);
    }

    public Topic getTopic(int position){
        int i = 0;
        for (Topic topic : topics){
            if (i++ == position){
                return topic;
            }
        }
        return null;
    }

    public void addMessageToTopic(String topicName, Message msg){
        Log.i("addMessage", "topic to find: " + topicName);
        for(Topic t : topics){
            Log.i("addMessage", "topid: " + t.getName());
            if (t.getName().equals(topicName)){
                t.addMessage(msg);
                return;
            }
        }
    }

    public void addMemberToTopic(String topicName, String memberName){
        for(Topic t : topics){
            if (t.getName().equals(topicName)){
                t.addMember(memberName);
                return;
            }
        }
    }

    public long getUserId(){
        return userId;
    }

    public void setUserId(long userId){
        this.userId = userId;
    }

    public String getNewTopicName(){
        return userId + "-" + topics.size();
    }

}
