package com.msc.myplace;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class Settings extends AppCompatActivity {
    private Switch location_sharing_switch;
    private Switch notifications_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
        Boolean location_sharing_enabled = settings.getBoolean(Constants.LOCATION_SHARING, true);
        Boolean notifications_enabled = settings.getBoolean(Constants.NOTIFICATIONS, true);

        this.location_sharing_switch = (Switch) findViewById(R.id.sw_location_sharing);
        this.notifications_switch = (Switch) findViewById(R.id.sw_notifications);
        this.location_sharing_switch.setChecked(location_sharing_enabled);
        this.notifications_switch.setChecked(notifications_enabled);

        this.location_sharing_switch.setOnCheckedChangeListener(
                new Switch.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean(Constants.LOCATION_SHARING, isChecked);
                        editor.commit();
                    }
                });
        this.notifications_switch.setOnCheckedChangeListener(
                new Switch.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean(Constants.NOTIFICATIONS, isChecked);
                        editor.commit();
                    }
                });
    }
}
