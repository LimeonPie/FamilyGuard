package com.msc.myplace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.client.Firebase;

public class StartUp extends AppCompatActivity {

    public SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        checkUser();
    }

    private void checkUser() {
        String familyId = settings.getString(Constants.GROUP_ID, null);
        /*if (familyId != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else {*/
            Intent intent = new Intent(this, EntryActivity.class);
            startActivity(intent);
        //}
    }
}
