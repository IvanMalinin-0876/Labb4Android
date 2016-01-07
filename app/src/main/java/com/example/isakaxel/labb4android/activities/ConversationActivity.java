package com.example.isakaxel.labb4android.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.isakaxel.labb4android.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;

public class ConversationActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputMessage;
    private Button sendButton;
    private Activity activity;
    private GoogleCloudMessaging gcm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gcm = GoogleCloudMessaging.getInstance(this);

        inputMessage = (EditText) findViewById(R.id.activity_conversation_message);
        sendButton = (Button) findViewById(R.id.activity_conversation_sendButton);

        sendButton.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if(v == sendButton) {
            String message = inputMessage.getText().toString();
            if(message != null) {
                Log.i("Message to send", message);
                // Send message
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String msg = "";
                        try {
                            Bundle data = new Bundle();
                            data.putString("my_message", "Hello World");
                            data.putString("my_action","SAY_HELLO");
                            String id = Integer.toString(4);
                            gcm.send("602319958990" + "@gcm.googleapis.com", id, data);
                            msg = "Sent message";
                        } catch (IOException ex) {
                            msg = "Error :" + ex.getMessage();
                        }
                        return msg;
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                        Log.i("onPostExecute", msg);
                    }
                }.execute(null, null, null);

            }
        }
    }
}