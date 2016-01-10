package com.example.isakaxel.labb4android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.isakaxel.labb4android.R;

/**
 * Created by alf on 1/10/16.
 */
public class AddPersonToTopicActivity extends AppCompatActivity {
    private Button addPersonButton;
    private EditText newPersonEmailInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person_to_topic);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.6));

        addPersonButton = (Button) findViewById(R.id.addPersonButton);
        newPersonEmailInput = (EditText) findViewById(R.id.newPersonEmailInput);

        addPersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == addPersonButton) {
                    String newPersonEmail = newPersonEmailInput.getText().toString();
                    if (newPersonEmail != null) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("otherUsersEmail", newPersonEmail);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }
            }
        });
    }
}
