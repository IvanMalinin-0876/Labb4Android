package com.example.isakaxel.labb4android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.isakaxel.labb4android.Model.Model;
import com.example.isakaxel.labb4android.R;
import com.example.isakaxel.labb4android.services.SendGcmService;

/**
 * Created by alf on 1/10/16.
 */
public class NewTopicActivity extends AppCompatActivity {
    private Button createTopic;
    private EditText newTopicName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newtopic);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.6));

        createTopic = (Button) findViewById(R.id.createTopicButton);
        newTopicName = (EditText) findViewById(R.id.newTopicName);

        createTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == createTopic) {
                    String newTopic = newTopicName.getText().toString();
                    if(newTopic != null) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("displayName", newTopic);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }
            }
        });
    }
}
