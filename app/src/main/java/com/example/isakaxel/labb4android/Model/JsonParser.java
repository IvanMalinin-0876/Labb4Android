package com.example.isakaxel.labb4android.Model;

import com.example.isakaxel.labb4android.Views.TopicViewModel;
import com.example.isakaxel.labb4android.Views.UserViewModel;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alf on 1/9/16.
 */
public class JsonParser {

    public static UserViewModel getUserFromJson(String jSon) {
        Gson gson = new Gson();
        return gson.fromJson(jSon, UserViewModel.class);
    }

    public static TopicViewModel getTopicFromJson(String jSon) {
        Gson gson = new Gson();
        return gson.fromJson(jSon, TopicViewModel.class);
    }

    public static String getTopicJson(TopicViewModel topic) {
        Gson gson = new Gson();
        return gson.toJson(topic);
    }
}
