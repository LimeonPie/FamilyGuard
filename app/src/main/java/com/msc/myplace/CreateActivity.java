package com.msc.myplace;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateActivity extends AppCompatActivity {

    private EditText familyNameInput;
    private EditText userNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        familyNameInput = (EditText) findViewById(R.id.familyNameInput);
        userNameInput = (EditText) findViewById(R.id.creatorNameInput);
    }

    // Create button event listener
    // Gather input data and try to create a new family
    public void onCreateButtonClick(View v) {

        String familyName = familyNameInput.getText().toString();
        String userName = userNameInput.getText().toString();

        if (!familyName.isEmpty() && !userName.isEmpty()) {
            Client.createNewFamily(this, familyName, userName);
        }
        else {
            Toast.makeText(getApplicationContext(), "Fill the fields", Toast.LENGTH_SHORT).show();
        }
    }
}
