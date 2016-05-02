package com.msc.myplace;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class EntryActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        context = getApplicationContext();
        // Setting status bar color
        // Didn't work with style and theme, so did it manually
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // TODO this causes problems on older phones, so disabling for now...
        //window.setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
    }

    // Create Button event listener
    // Open CreateActivity here
    public void onCreateClick(View v) {
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);
    }

    // Join Button event listener
    // Open JoinActivityHere
    public void onJoinClick(View v) {
        Intent intent = new Intent(this, JoinActivity.class);
        startActivity(intent);
    }
}
