package com.msc.myplace;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class JoinActivity extends AppCompatActivity {

    private EditText keyInput;
    private EditText userNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        keyInput = (EditText) findViewById(R.id.keyInput);
        userNameInput = (EditText) findViewById(R.id.joinNameInput);
    }

    // Join button event listener
    // Gather input data and try to join
    public void onJoinButtonClick(View v) {

        String key = keyInput.getText().toString();
        String userName = userNameInput.getText().toString();

        if (!key.isEmpty() && !userName.isEmpty()) {
            Client.joinFamily(this, key, userName);
        }
        else {
            Toast.makeText(getApplicationContext(), "Fill the fields", Toast.LENGTH_SHORT).show();
        }
    }
}
